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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-testing-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;

import com.google.common.base.Throwables;

final class GitLabEnhancerHandler<T> extends HandlerUtil implements HttpHandler {

	private static final String APPLICATION_JSON = "application/json";

	private EntityProvider<T> provider;

	private GitLabEnhancerHandler() {
	}

	GitLabEnhancerHandler<T> entityProvider(final EntityProvider<T> provider) {
		this.provider = provider;
		return this;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		if(!Methods.GET.equals(exchange.getRequestMethod())) {
			fail(exchange, StatusCodes.METHOD_NOT_ALLOWED, "Only GET is allowed");
			exchange.getResponseHeaders().put(Headers.ALLOW,Methods.GET_STRING);
		} else if(!APPLICATION_JSON.equals(exchange.getRequestHeaders().get(Headers.ACCEPT).getFirst())) {
			fail(exchange, StatusCodes.NOT_ACCEPTABLE, "Only application/json representations can be retrieved");
		} else {
			try {
				final T entity=this.provider.getEntity(new Parameters(exchange));
				String body="false";
				if(entity!=null) {
					body=JsonUtil.marshall(entity);
				}
				answer(exchange,StatusCodes.OK,APPLICATION_JSON,body);
			} catch (final Exception e) {
				fail(exchange,e,"Upps!!\n%s",Throwables.getStackTraceAsString(e));
			}
		}
	}

	static <T> GitLabEnhancerHandler<T> create() {
		return new GitLabEnhancerHandler<T>();
	}

}