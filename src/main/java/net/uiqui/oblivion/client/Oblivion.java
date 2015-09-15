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
package net.uiqui.oblivion.client;

import java.io.IOException;
import java.util.List;

import net.uiqui.oblivion.client.api.APIClient;
import net.uiqui.oblivion.client.api.error.CacheException;
import net.uiqui.oblivion.client.impl.JSONCacheContext;

/**
 * Oblivion REST API client main class.
 */
public class Oblivion {
	private APIClient apiClient = null;
	
	private Oblivion(final Builder builder) {
		this.apiClient = new APIClient(builder.server, builder.port, builder.refreshInterval);
	}
	
	/**
	 * Create a cache context without automatic JSON&lt;-&gt;Object conversion.
	 *
	 * @param cache the cache name
	 * @return the cache context
	 */
	public CacheContext<String> newCacheContext(final String cache) {
		return new JSONCacheContext(cache, apiClient);
	}
	
	/**
	 * Create a cache context with automatic JSON&lt;-&gt;Object conversion.
	 *
	 * @param <X> the generic type
	 * @param cache the cache name
	 * @param clazz the clazz
	 * @return the cache context
	 */
	public <X> CacheContext<X> newCacheContext(final String cache, final Class<X> clazz) {
		return new CacheContext<X>(cache, apiClient, clazz);
	}
	
	/**
	 * Available caches.
	 *
	 * @return the cache list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException Signals that an cache specific exception has occurred.
	 */
	public List<String> caches() throws IOException, CacheException {
		return apiClient.caches();
	}
	
	/**
	 * Oblivion version.
	 *
	 * @return the oblivion version
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException Signals that an cache specific exception has occurred.
	 */
	public String version() throws IOException, CacheException {
		return apiClient.systemVersion();
	}	
	
	/**
	 * The Class Builder.
	 */
	public static class Builder {
		private String server = "localhost";
		private int port = 12522; 
		private int refreshInterval = 60000;
		
		/**
		 * Oblivion server ip/name.
		 *
		 * @param server the server
		 * @return the builder
		 */
		public Builder server(final String server) {
			this.server = server;
			return this;
		}
		
		/**
		 * Oblivion server port.
		 *
		 * @param port the port
		 * @return the builder
		 */
		public Builder port(final int port) {
			this.port = port;
			return this;
		}
		
		/**
		 * Refresh interval.
		 *
		 * @param refreshInterval the refresh interval
		 * @return the builder
		 */
		public Builder refreshInterval(final int refreshInterval) {
			this.refreshInterval = refreshInterval;
			return this;
		}		
		
		/**
		 * Builds an Oblivion instance.
		 *
		 * @return the oblivion
		 */
		public Oblivion build() {
			return new Oblivion(this);
		}
	}
	
	/**
	 * Create a builder instance.
	 *
	 * @return the builder
	 */
	public static Builder builder() {
		return new Builder();
	}
}
