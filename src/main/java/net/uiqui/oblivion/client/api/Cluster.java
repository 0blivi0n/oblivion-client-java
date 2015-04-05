/*
 * 0blivi0n-cache
 * ==============
 * Java REST Client
 * 
 * Copyright (C) 2015 Joaquim Rocha <jrocha@gmailbox.org>
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.uiqui.oblivion.client.api;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.uiqui.oblivion.client.api.error.CacheException;
import net.uiqui.oblivion.client.api.model.Server;

public class Cluster {
	private Ring servers = new Ring(); 
	private APIClient apiClient = null;
	private Server defaultServer = null;
	private final Timer timer = new Timer(true);
	
	protected Cluster(final APIClient apiClient, final String server, final int port, final int refreshInterval) {
		this.apiClient = apiClient;
		this.defaultServer = new Server(server, port);
		
		timer.schedule(new Refresh(), 100, refreshInterval);
	}
	
	public Server server() {
		Server server = servers.current();
		
		if (server == null) {
			return defaultServer;
		}
		
		return server;
	}	

	public void nextServer() {
		servers.next();
	}
	
	private class Refresh extends TimerTask {
		@Override
		public void run() {
			try {
				servers.add(apiClient.nodes());
			} catch (IOException e) {
				nextServer();
			} catch (CacheException e) {
			}
		}
	}
	
	private static class Ring {
		private Node current = null;
		
		public synchronized void add(final List<Server> list) {
			current = null;
			
			for (Server server : list) {
				add(server);
			}
		}
		
		private void add(final Server value) {
			if (current == null) {
				current = new Node(value, null);
				current.next = current;
			} else {
				Node next = current.next;
				current.next = new Node(value, next);
			}
		}
		
		public synchronized Server current() {
			if (current == null) {
				return null;
			}
			
			return current.server;
		}
		
		public synchronized void next() {
			if (current != null) {
				current = current.next;
			}
		}		
		
		private static class Node {
			public final Server server;
			public Node next;

			public Node(final Server server, final Node next) {
				this.server = server;
				this.next = next;
			}
		}
	}
}
