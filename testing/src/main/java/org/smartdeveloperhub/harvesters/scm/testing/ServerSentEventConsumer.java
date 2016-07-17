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
package org.smartdeveloperhub.harvesters.scm.testing;

import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;

import java.io.IOException;

import org.smartdeveloperhub.harvesters.scm.testing.enhancer.Activity;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.ActivityListener;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.JsonUtil;

final class ServerSentEventConsumer implements ActivityListener {

	private final ServerSentEventHandler sseHandler;

	ServerSentEventConsumer(final ServerSentEventHandler sseHandler) {
		this.sseHandler=sseHandler;
	}

	@Override
	public void onActivity(final Activity<?> message) {
		for(final ServerSentEventConnection h : this.sseHandler.getConnections()) {
			try {
				final String data=JsonUtil.marshall(message).replace(System.lineSeparator(), System.lineSeparator()+"data:");
				h.send(data);
			} catch (final IOException e) {
				h.send(message.toString());
			}
		}
	}

}