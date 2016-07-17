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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.3.0
 *   Bundle      : scm-harvester-testing-0.3.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Event;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer.UpdateReport;

import com.google.common.collect.Maps;

final class GitCollectorEventProcessorHandler extends HandlerUtil implements HttpHandler {

	private static final String APPLICATION_JSON = "application/json";

	private static final Logger LOGGER=LoggerFactory.getLogger(GitCollectorEventProcessorHandler.class);

	private Event event;

	private final GitLabEnhancer enhancer;

	GitCollectorEventProcessorHandler(final GitLabEnhancer enhancer) {
		this.enhancer = enhancer;
	}

	Event getEvent() {
		return this.event;
	}

	GitCollectorEventProcessorHandler setEvent(final Event event) {
		this.event = event;
		return this;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) {
		try {
			final UpdateReport report = this.enhancer.update(this.event);
			int statusCode=StatusCodes.INTERNAL_SERVER_ERROR;
			if(!report.enhancerUpdated()) {
				statusCode=StatusCodes.UNPROCESSABLE_ENTITY;
			} else if(!report.notificationSent()) {
				statusCode=StatusCodes.SERVICE_UNAVAILABLE;
			} else {
				statusCode=StatusCodes.OK;
			}
			answer(exchange,statusCode,APPLICATION_JSON,marshall(report));
		} catch (final Throwable e) {
			fail(exchange,e,"Could not update service");
		}
	}

	private String marshall(final UpdateReport report) {
		final Map<String, Object> data = toMap(report);
		try {
			return JsonUtil.marshall(data);
		} catch (final IOException e) {
			LOGGER.debug("Could not serialize response event",e);
			return report.toString();
		}
	}

	private Map<String, Object> toMap(final UpdateReport report) {
		final Map<String,Object> data=Maps.newLinkedHashMap();
		if(report.enhancerUpdated()) {
			data.put("curatedEvent",report.curatedEvent());
			if(!report.notificationSent()) {
				data.put("failure",report.updateFailure());
			}
		}
		data.put("warnings", report.warnings());
		data.put("sideEffects", report.sideEffects());
		return data;
	}


}