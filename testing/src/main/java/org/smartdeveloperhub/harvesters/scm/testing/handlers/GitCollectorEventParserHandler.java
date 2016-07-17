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

import io.undertow.io.Receiver.ErrorCallback;
import io.undertow.io.Receiver.FullStringCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

import java.io.IOException;

import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Event;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryUpdatedEvent;

import com.google.common.collect.ImmutableMap;

final class GitCollectorEventParserHandler extends HandlerUtil implements HttpHandler {

	private static final ImmutableMap<String,Class<? extends Event>> EVENT_TYPES=
		ImmutableMap.
			<String,Class<? extends Event>>builder().
				put(CommitterCreatedEvent.class.getSimpleName(),CommitterCreatedEvent.class).
				put(CommitterDeletedEvent.class.getSimpleName(),CommitterDeletedEvent.class).
				put(RepositoryCreatedEvent.class.getSimpleName(),RepositoryCreatedEvent.class).
				put(RepositoryDeletedEvent.class.getSimpleName(),RepositoryDeletedEvent.class).
				put(RepositoryUpdatedEvent.class.getSimpleName(),RepositoryUpdatedEvent.class).
				build();

	private final GitCollectorEventProcessorHandler next;

	GitCollectorEventParserHandler(final GitCollectorEventProcessorHandler next) {
		this.next = next;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) {
		final HeaderValues eventTypeHeader = exchange.getRequestHeaders().get(HttpString.tryFromString("X-Event-Type"));
		if(eventTypeHeader==null || eventTypeHeader.isEmpty()) {
			fail(exchange,StatusCodes.BAD_REQUEST,"No event type specified (use X-Event-Type header)");
			return;
		}
		final String eventType = eventTypeHeader.getFirst();
		final Class<? extends Event> eventClazz = EVENT_TYPES.get(eventType);
		if(eventClazz==null) {
			fail(exchange,StatusCodes.BAD_REQUEST,"Unsupported event type '%s'",eventType);
			return;
		}
		exchange.
			getRequestReceiver().
				receiveFullString(
					new FullStringCallback() {
						@Override
						public void handle(final HttpServerExchange exchange, final String message)  {
							try {
								final Event event = JsonUtil.unmarshall(message, eventClazz);
								processEvent(exchange, event, eventType);
							} catch (final IOException e) {
								fail(exchange,e,StatusCodes.UNPROCESSABLE_ENTITY,"Could not parse event as '%s'",eventType);
							}
						}

						private void processEvent(final HttpServerExchange exchange, final Event event, final String eventType) {
							try {
								GitCollectorEventParserHandler.this.next.setEvent(event);
								GitCollectorEventParserHandler.this.next.handleRequest(exchange);
							} catch (final Exception e) {
								fail(exchange,e,"Could not process event '%s': %n%s",eventType,event);
							}
						}

					},
					new ErrorCallback() {
						@Override
						public void error(final HttpServerExchange exchange, final IOException e) {
							fail(exchange,e,StatusCodes.UNPROCESSABLE_ENTITY,"Could not read event");
						}
					}
				);
	}
}