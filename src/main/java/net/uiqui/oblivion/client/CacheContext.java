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
import net.uiqui.oblivion.client.api.Response;
import net.uiqui.oblivion.client.api.error.CacheException;

/**
 * The Class CacheContext.
 *
 * @param <T> the generic type
 */
public class CacheContext<T> {
	private final Gson gson = new Gson();
	private String cache = null;
	private APIClient apiClient = null;
	private Class<T> type = null;
	
	/**
	 * Instantiates a new cache context.
	 *
	 * @param cache the cache
	 * @param apiClient the api client
	 * @param clazz the clazz
	 */
	protected CacheContext(final String cache, final APIClient apiClient, final Class<T> clazz) {
		this.cache = cache;
		this.apiClient = apiClient;
		this.type = clazz;
	}
	
	/**
	 * Version.
	 *
	 * @param key the key
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public long version(final Object key) throws IOException, CacheException {
		final String keyStr = key.toString();
		return apiClient.version(cache, keyStr);
	}		
	
	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the t
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public T get(final Object key) throws IOException, CacheException {
		final String keyStr = key.toString();
		final Response value = apiClient.get(cache, keyStr);
		
		if (value == null) {
			return null;
		}
		
		return fromJson(value.getContent());
	}	
	
	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public Value<T> getValue(final Object key) throws IOException, CacheException {
		final String keyStr = key.toString();
		final Response value = apiClient.get(cache, keyStr);
		
		if (value == null) {
			return null;
		}
		
		return new Value<T>(fromJson(value.getContent()), value.getVersion());
	}	
	
	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public long put(final Object key, final T value) throws IOException, CacheException {
		final String keyStr = key.toString();
		final String json = toJson(value);
		return apiClient.put(cache, keyStr, json);
	}	
	
	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 * @param version the version
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public long put(final Object key, final T value, long version) throws IOException, CacheException {
		final String keyStr = key.toString();
		final String json = toJson(value);
		return apiClient.put(cache, keyStr, json, version);
	}	
	
	/**
	 * Delete.
	 *
	 * @param key the key
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public void delete(final Object key) throws IOException, CacheException {
		final String keyStr = key.toString();
		apiClient.delete(cache, keyStr);
	}	
	
	/**
	 * Delete.
	 *
	 * @param key the key
	 * @param version the version
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public void delete(final Object key, long version) throws IOException, CacheException {
		final String keyStr = key.toString();
		apiClient.delete(cache, keyStr, version);
	}	
	
	/**
	 * Keys.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public List<String> keys() throws IOException, CacheException {
		return apiClient.keys(cache);
	}
	
	/**
	 * Flush.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CacheException the cache exception
	 */
	public void flush() throws IOException, CacheException {
		apiClient.flush(cache);
	}
	
	/**
	 * To json.
	 *
	 * @param value the value
	 * @return the string
	 */
	protected String toJson(final T value) {
		return gson.toJson(value);
	}
	
	/**
	 * From json.
	 *
	 * @param json the json
	 * @return the t
	 */
	protected T fromJson(final String json) {
		return gson.fromJson(json, type);
	}
}
