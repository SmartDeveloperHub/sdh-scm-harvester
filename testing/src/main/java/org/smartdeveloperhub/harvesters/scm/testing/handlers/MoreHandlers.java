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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester-testing-0.4.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.util.HttpString;

import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer;

public final class MoreHandlers {

	public enum APIVersion {
		v1,
		v2,
	}

	private MoreHandlers() {
	}

	public static HttpHandler allow(final HttpString method, final HttpHandler aHandler) {
		return AllowedMethodsHandler.create().allow(method).setNext(aHandler);
	}

	public static HttpHandler consume(final String contentType, final HttpHandler aHandler) {
		return ContentTypeConsumerHandler.create().consumes(contentType).setNext(aHandler);
	}

	public static <T> HttpHandler provideEntity(APIVersion version, final EntityProvider<T> provider) {
		return GitLabEnhancerHandler.<T>create(version).entityProvider(provider);
	}

	public static HttpHandler handleEvents(final GitLabEnhancer enhancer) {
		return new GitCollectorEventParserHandler(new GitCollectorEventProcessorHandler(enhancer));
	}

}