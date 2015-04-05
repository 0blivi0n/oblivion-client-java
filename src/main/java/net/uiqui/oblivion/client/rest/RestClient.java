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
import java.net.URL;
import java.util.concurrent.TimeUnit;

import net.uiqui.oblivion.client.api.Cluster;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class RestClient {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private OkHttpClient client = null;

	public RestClient(final Cluster cluster) {
		client = new OkHttpClient();
		
		client.setConnectTimeout(5, TimeUnit.SECONDS);
	    client.setWriteTimeout(5, TimeUnit.SECONDS);
	    client.setReadTimeout(5, TimeUnit.SECONDS);
	    
		client.interceptors().add(new RetryHandler(cluster));
	}

	public RestOutput put(final URL url, final String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);

		Request request = new Request.Builder().url(url).put(body).build();

		Response response = client.newCall(request).execute();

		return RestOutput.parse(response);
	}

	public RestOutput post(final URL url, final String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);

		Request request = new Request.Builder().url(url).post(body).build();

		Response response = client.newCall(request).execute();

		return RestOutput.parse(response);
	}

	public RestOutput get(final URL url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();

		return RestOutput.parse(response);
	}
	
	public RestOutput delete(final URL url) throws IOException {
		Request request = new Request.Builder().url(url).delete().build();

		Response response = client.newCall(request).execute();

		return RestOutput.parse(response);
	}	
	
	public RestOutput head(final URL url) throws IOException {
		Request request = new Request.Builder().url(url).head().build();

		Response response = client.newCall(request).execute();

		return RestOutput.parse(response);
	}		
}
