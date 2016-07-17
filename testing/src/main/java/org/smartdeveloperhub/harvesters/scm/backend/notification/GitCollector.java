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
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import java.io.IOException;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;

public final class GitCollector {

	private final CollectorController publisher;

	private GitCollector(final Collector collector) {
		this.publisher = CollectorController.createPublisher(collector);
	}

	public Collector getConfig() {
		return this.publisher.collector();
	}

	public String getInstance() {
		return this.publisher.collector().getInstance();
	}

	public void start() throws IOException {
		try {
			this.publisher.connect();
		} catch (final ControllerException e) {
			throw new IOException("Could not start collector",e);
		}
	}

	public void notify(final Event event) throws IOException {
		try {
			this.publisher.publishEvent(event);
		} catch (final ControllerException e) {
			throw new IOException("Could not send notification "+event,e);
		}
	}

	public void shutdown() {
		this.publisher.disconnect();
	}

	public static GitCollector newInstance(final Collector collector) {
		return new GitCollector(collector);
	}

}
