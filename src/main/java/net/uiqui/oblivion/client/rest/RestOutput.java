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

import com.squareup.okhttp.Response;

public class RestOutput {
	private int status = 0;
	private Long etag = null;
	private String json = null;

	private RestOutput(final int status, final Long etag, final String json) {
		this.status = status;
		this.etag = etag;
		this.json = json;
	}

	public int getStatus() {
		return status;
	}

	public Long getEtag() {
		return etag;
	}

	public String getJson() {
		return json;
	}

	public static RestOutput parse(Response response) throws IOException {
		final int status = response.code();

		final String etagHeader = response.header("ETag");

		Long etag = null;

		if (etagHeader != null) {
			etag = Long.valueOf(etagHeader);
		}

		final String json = response.body().string();

		return new RestOutput(status, etag, json);
	}
}
