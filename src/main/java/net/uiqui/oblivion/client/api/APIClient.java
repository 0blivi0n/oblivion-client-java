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
import java.net.URL;
import java.util.List;

import net.uiqui.oblivion.client.api.error.CacheException;
import net.uiqui.oblivion.client.api.model.Caches;
import net.uiqui.oblivion.client.api.model.Keys;
import net.uiqui.oblivion.client.api.model.Nodes;
import net.uiqui.oblivion.client.api.model.Reason;
import net.uiqui.oblivion.client.api.model.Server;
import net.uiqui.oblivion.client.api.model.SystemInfo;
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
	private static final URLBuilder GET_ALL_KEYS = new URLBuilder("http://%s:%s/api/caches/%s/keys?list=true");
	private static final URLBuilder GET_CACHE_SIZE = new URLBuilder("http://%s:%s/api/caches/%s/keys");
	private static final URLBuilder DELETE_ALL_KEYS = new URLBuilder("http://%s:%s/api/caches/%s/keys");
	private static final URLBuilder GET_CACHE_LIST = new URLBuilder("http://%s:%s/api/caches?sort=true");
	private static final URLBuilder GET_NODE_LIST = new URLBuilder("http://%s:%s/api/nodes");
	private static final URLBuilder GET_SYSTEM = new URLBuilder("http://%s:%s/api/system");

	private final Gson gson = new Gson();
	private Cluster cluster = null;
	private RestClient client = null;

	public APIClient(final String server, final int port, final int refreshInterval) {
		this.cluster = new Cluster(this, server, port, refreshInterval);
		this.client = new RestClient(cluster);
	}

	public List<String> caches() throws IOException, CacheException {
		final Server server = cluster.server();
		final URL url = GET_CACHE_LIST.build(server.getServer(), server.getPort());
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			final Caches caches = gson.fromJson(output.getJson(), Caches.class);
			return caches.getCaches();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public void flush(final String cache) throws IOException, CacheException {
		final Server server = cluster.server();
		final URL url = DELETE_ALL_KEYS.build(server.getServer(), server.getPort(), cache);
		final RestOutput output = client.delete(url);

		if (output.getStatus() != 202) {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}
	
	public long size(final String cache) throws IOException, CacheException {
		final Server server = cluster.server();
		final URL url = GET_CACHE_SIZE.build(server.getServer(), server.getPort(), cache);
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			return gson.fromJson(output.getJson(), Long.class);
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}	

	public List<String> keys(final String cache) throws IOException, CacheException {
		final Server server = cluster.server();
		final URL url = GET_ALL_KEYS.build(server.getServer(), server.getPort(), cache);
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
		final Server server = cluster.server();
		final URL url = PUT_KEY_VALUE.build(server.getServer(), server.getPort(), cache, keyEncoded);
		return store(url, value);
	}

	public long put(final String cache, final String key, final String value, long version) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final Server server = cluster.server();
		final URL url = PUT_KEY_VALUE_WITH_VERSION.build(server.getServer(), server.getPort(), cache, keyEncoded, version);
		return store(url, value);
	}

	private long store(final URL url, final String value) throws IOException, CacheException {
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
		final Server server = cluster.server();
		final URL url = DELETE_KEY.build(server.getServer(), server.getPort(), cache, keyEncoded);
		remove(url);
	}

	public void delete(final String cache, final String key, long version) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final Server server = cluster.server();
		final URL url = DELETE_KEY_WITH_VERSION.build(server.getServer(), server.getPort(), cache, keyEncoded, version);
		remove(url);
	}

	private void remove(final URL url) throws IOException, CacheException {
		final RestOutput output = client.delete(url);

		if (output.getStatus() != 200) {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}

	public long version(final String cache, final String key) throws IOException, CacheException {
		final String keyEncoded = KeyEncoder.encode(key);
		final Server server = cluster.server();
		final URL url = HEAD_KEY_VERSION.build(server.getServer(), server.getPort(), cache, keyEncoded);
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
		final Server server = cluster.server();
		final URL url = GET_KEY_VALUE.build(server.getServer(), server.getPort(), cache, keyEncoded);
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
	
	protected List<Server> nodes() throws IOException, CacheException {
		final Server server = cluster.server();
		final URL url = GET_NODE_LIST.build(server.getServer(), server.getPort());
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			final Nodes nodes = gson.fromJson(output.getJson(), Nodes.class);
			return nodes.getOnlineNodes();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}	
	
	public String systemVersion() throws IOException, CacheException {
		final Server server = cluster.server();
		final URL url = GET_SYSTEM.build(server.getServer(), server.getPort());
		final RestOutput output = client.get(url);

		if (output.getStatus() == 200) {
			final SystemInfo system = gson.fromJson(output.getJson(), SystemInfo.class);
			return system.getVersion();
		} else {
			final Reason reason = gson.fromJson(output.getJson(), Reason.class);
			throw new CacheException(reason);
		}
	}	
}
