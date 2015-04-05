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
package net.uiqui.oblivion.client.rest;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;

import net.uiqui.oblivion.client.api.Cluster;
import net.uiqui.oblivion.client.api.model.Server;
import net.uiqui.oblivion.client.api.util.URLBuilder;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RetryHandler implements Interceptor {
	private Cluster cluster = null;

	public RetryHandler(final Cluster cluster) {
		this.cluster = cluster;
	}

	public Response intercept(final Chain chain) throws IOException {
		final Request request = chain.request();

		try {
			return chain.proceed(request);
		} catch (SocketTimeoutException e) {
			cluster.nextServer();

			final Server server = cluster.server();
			final URL url = URLBuilder.change(request.url(), server.getServer(), server.getPort());
			final Request retry = request.newBuilder().url(url).build();
			
			return chain.proceed(retry);
		}
	}

}
