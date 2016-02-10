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
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.controller.EnhancerController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;

public final class NotificationManager {

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationManager.class);

	private final URI target;
	private final CollectorAggregator aggregator;

	private NotificationManager(final URI target, final NotificationListener listener) {
		this.target=target;
		this.aggregator=CollectorAggregator.newInstance(target.toString(), listener);
	}

	public void connect() throws IOException {
		LOGGER.info("Setting up notification manager for {}...",this.target);
		final List<Collector> usedCollectors = getEnhancerCollectorConfiguration();
		try {
			this.aggregator.connect(usedCollectors);
			LOGGER.info("Notification notification manager for {} connected",this.target);
		} catch (final ControllerException e) {
			LOGGER.warn("Could not connect to collectors of {}. Full stacktrace follows",this.target,this.target);
			throw new IOException("Could not connect to collectors of "+this.target,e);
		}
	}

	public void disconnect() {
		LOGGER.info("Disconnecting manager for {}...",this.target);
		this.aggregator.disconnect();
		LOGGER.info("Notification manager for {} disconnected",this.target);
	}

	private List<Collector> getEnhancerCollectorConfiguration() throws IOException {
		final EnhancerController controller=new EnhancerController(this.target.toString());
		final Enhancer enhancer = controller.getEnhancer();
		return enhancer.getCollectors();
	}

	public static NotificationManager newInstance(final URI target, final NotificationListener listener) throws IOException {
		return new NotificationManager(target,listener);
	}

}