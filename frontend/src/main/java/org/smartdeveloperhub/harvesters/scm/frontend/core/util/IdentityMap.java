/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015-2016 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.util;

import java.util.concurrent.ConcurrentHashMap;

import org.ldp4j.application.data.Name;

public class IdentityMap<T> {

	ConcurrentHashMap<T,Name<String>> keytoResourceName;
	ConcurrentHashMap<Name<String>,T> resourceNameToKey;

	public IdentityMap() {
		this.keytoResourceName = new ConcurrentHashMap<T,Name<String>> ();
		this.resourceNameToKey = new ConcurrentHashMap<Name<String>,T>();
	}


	public void addKey(final T key,final Name<String> resourceName ){
		this.keytoResourceName.put(key, resourceName);
		this.resourceNameToKey.put(resourceName, key);
	}

	public T getKey(final Name<String> resourceName){
		return this.resourceNameToKey.get(resourceName);
	}

	public Name<String> getResourceName(final T key){
		return this.keytoResourceName.get(key);
	}

}