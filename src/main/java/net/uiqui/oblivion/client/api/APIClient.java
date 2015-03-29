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

import net.uiqui.oblivion.client.api.error.CacheException;
import net.uiqui.oblivion.client.api.model.Caches;
import net.uiqui.oblivion.client.api.model.Keys;
import net.uiqui.oblivion.client.api.model.Nodes;
import net.uiqui.oblivion.client.api.model.Reason;
import net.uiqui.oblivion.client.api.util.KeyEncoder;
import net.uiqui.oblivion.client.api.util.URLBuilder;
import net.uiqui.oblivion.client.rest.RestClient;
import net.uiqui.oblivion.client.rest.RestOutput;

import com.google.gson.Gson;

public class APIClient {
	private static final URLBuilder GET_KEY_VALUE = new URLBuilder("http://%s:%s/api/caches/%s/keys/%s");
	private static final URLBuilder PUT_KEY_VALUE_WITH_VERSION = new URLBuilder("http://%s:%s/api/caches/%s/keys/%s?version=%s");
	private static final URLBuilder PUT_KEY_VALUE = new URLBuilder("http://%s:%s/api/caches/%s/keys/%s");
	private static final URLBuilder HEAD_KEY_VERSION = new URLBuilder("http://%s:%s/api/caches/%s/keys/%s");
	private static final URLBuilder DELETE_KEY_WITH_VERSION = new URLBuilder("http://%s:%s/api/caches/%s/keys/%s?version=%s");
	private static final URLBuilder DELETE_KEY = new URLBuilder("http://%s:%s/api/caches/%s/keys/%s");
	private static final URLBuilder GET_ALL_KEYS = new URLBuilder("http://%s:%s/api/caches/%s/keys");
	private static final URLBuilder DELETE_ALL_KEYS = new URLBuilder("http://%s:%s/api/caches/%s/keys");
	private static final URLBuilder GET_CACHE_LIST = new URLBuilder("http://%s:%s/api/caches");
	private static final URLBuilder GET_NODE_LIST = new URLBuilder("http://%s:%s/api/nodes");

	private final RestClient client = new RestClient();
	private final Gson gson = new Gson();

	private String server = null;
	private int port = 0;

	public APIClient(final String server, final int port) {
		this.server = server;
		this.port = port;
	}

	public List<String> caches() throws IOException, CacheException {
		final String url = GET_CACHE_LIST.build(server, port);
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			final Caches caches = gson.fromJson(output.getJson(), Caches.class);
			return caches.getCaches();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public List<String> nodes() throws IOException, CacheException {
		final String url = GET_NODE_LIST.build(server, port);
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			final Nodes nodes = gson.fromJson(output.getJson(), Nodes.class);
			return nodes.getOnlineNodes();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public void flush(final String cache) throws IOException, CacheException {
		final String url = DELETE_ALL_KEYS.build(server, port, cache);
		final RestOutput output = client.delete(url);

		if (output.getStatus() != 202) {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public List<String> keys(final String cache) throws IOException, CacheException {
		final String url = GET_ALL_KEYS.build(server, port, cache);
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			final Keys keys = gson.fromJson(output.getJson(), Keys.class);
			return keys.getKeys();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public long put(final String cache, final String key, final String value) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final String url = PUT_KEY_VALUE.build(server, port, cache, keyEncoded);
		return store(url, value);
	}

	public long put(final String cache, final String key, final String value, long version) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final String url = PUT_KEY_VALUE_WITH_VERSION.build(server, port, cache, keyEncoded, version);
		return store(url, value);
	}

	private long store(final String url, final String value) throws IOException, CacheException {
		final RestOutput output = client.put(url, value);

		if (output.getStatus() == 201) {
			return output.getEtag().longValue();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public void delete(final String cache, final String key) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final String url = DELETE_KEY.build(server, port, cache, keyEncoded);
		remove(url);
	}

	public void delete(final String cache, final String key, long version) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final String url = DELETE_KEY_WITH_VERSION.build(server, port, cache, keyEncoded, version);
		remove(url);
	}

	private void remove(final String url) throws IOException, CacheException {
		final RestOutput output = client.delete(url);

		if (output.getStatus() != 200) {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public long version(final String cache, final String key) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final String url = HEAD_KEY_VERSION.build(server, port, cache, keyEncoded);
		final RestOutput output = client.head(url);

		if (output.getStatus() == 200) {
			return output.getEtag().longValue();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public Response get(final String cache, final String key) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final String url = GET_KEY_VALUE.build(server, port, cache, keyEncoded);
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			return new Response(output.getJson(), output.getEtag().longValue());
		} else if (output.getStatus() == 404) {
			return null;
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}
}
