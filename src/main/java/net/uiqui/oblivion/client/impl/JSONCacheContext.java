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
package net.uiqui.oblivion.client.impl;

import net.uiqui.oblivion.client.CacheContext;
import net.uiqui.oblivion.client.api.APIClient;

public class JSONCacheContext extends CacheContext<String> {

	public JSONCacheContext(String cache, APIClient apiClient) {
		super(cache, apiClient, String.class);
	}

	@Override
	protected String toJson(String value) {
		return value;
	}

	@Override
	protected String fromJson(String json) {
		return json;
	}
}
