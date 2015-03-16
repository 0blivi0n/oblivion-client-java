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
package net.uiqui.oblivion.client.api.error;

import net.uiqui.oblivion.client.api.model.Reason;

public class CacheException extends Exception {
	private static final long serialVersionUID = -5842545184791468672L;

	private Reason reason = null;
	
	public CacheException(final Reason reason) {
		super(reason.toString());
		
		this.reason = reason;
	}
	
	public Reason reason() {
		return reason;
	}
}
