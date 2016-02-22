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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Event;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer.UpdateReport;

final class GitCollectorEventProcessorHandler extends HandlerUtil implements HttpHandler {

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
			Event processEvent=this.event;
			String action="ignored";
			final UpdateReport report = this.enhancer.update(processEvent);
			if(report.notificationSent()) {
				processEvent=report.curatedEvent();
				action="sent";
			}
			final String entity=marshall(processEvent);
			answer(exchange,StatusCodes.OK, "Notification %s:\n%s",action,entity);
			for(final String warning:report.warnings()) {
				LOGGER.debug("{}",warning);
			}
			LOGGER.debug("Notification {} {}:\n{}",processEvent.getClass().getSimpleName(),action,entity);
		} catch (final Throwable e) {
			fail(exchange,e,"Could not update enhancer");
		}
	}

	private String marshall(final Event event) {
		try {
			return JsonUtil.marshall(event);
		} catch (final IOException e) {
			LOGGER.debug("Could not serialize response event",e);
			return event.toString();
		}
	}


}