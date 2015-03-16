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

import com.google.gson.Gson;

import net.uiqui.oblivion.client.api.APIClient;
import net.uiqui.oblivion.client.api.Value;
import net.uiqui.oblivion.client.api.error.CacheException;

public class CacheContext {
	private final Gson gson = new Gson();
	private String cache = null;
	private APIClient apiClient = null;
	private Class<?> defaultClass = null;
	
	protected CacheContext(final String cache, final APIClient apiClient, final Class<?> clazz) {
		this.cache = cache;
		this.apiClient = apiClient;
		this.defaultClass = clazz;
	}
	
	public long version(final Object key) throws IOException, CacheException {
		final String keyStr = key.toString();
		return apiClient.version(cache, keyStr);
	}	
	
	public Object get(final Object key) throws IOException, CacheException {
		return get(key, null);
	}	
	
	public Object get(final Object key, final Class<?> clazz) throws IOException, CacheException {
		final String keyStr = key.toString();
		final Value value = apiClient.get(cache, keyStr);
		
		if (value == null) {
			return null;
		}
		
		return output(value.getContent(), clazz);
	}	
	
	public GetResponse getValueAndVersion(final Object key) throws IOException, CacheException {
		return getValueAndVersion(key, null);
	}	
	
	public GetResponse getValueAndVersion(final Object key, final Class<?> clazz) throws IOException, CacheException {
		final String keyStr = key.toString();
		final Value value = apiClient.get(cache, keyStr);
		
		if (value == null) {
			return null;
		}
		
		return new GetResponse(output(value.getContent(), clazz), value.getVersion());
	}	
	
	public long put(final Object key, final Object value) throws IOException, CacheException {
		final String keyStr = key.toString();
		final String json = gson.toJson(value);
		return apiClient.put(cache, keyStr, json);
	}	
	
	public long put(final Object key, final Object value, long version) throws IOException, CacheException {
		final String keyStr = key.toString();
		final String json = gson.toJson(value);
		return apiClient.put(cache, keyStr, json, version);
	}	
	
	public void delete(final Object key) throws IOException, CacheException {
		final String keyStr = key.toString();
		apiClient.delete(cache, keyStr);
	}	
	
	public void delete(final Object key, long version) throws IOException, CacheException {
		final String keyStr = key.toString();
		apiClient.delete(cache, keyStr, version);
	}	
	
	public List<String> keys() throws IOException, CacheException {
		return apiClient.keys(cache);
	}
	
	public void flush() throws IOException, CacheException {
		apiClient.flush(cache);
	}
	
	private Object output(final String json, final Class<?> clazz) {
		Class<?> outputClass = (clazz != null? clazz : defaultClass);
		
		if (outputClass == null) {
			return json;
		}
		
		return gson.fromJson(json, outputClass);
	}
}
