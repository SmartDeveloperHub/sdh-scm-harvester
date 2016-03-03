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

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;

public class CollectorAggregatorITest extends NotificationTestHelper {

	private final class CustomNotificationListener implements NotificationListener {
		private final Random random = new Random(System.currentTimeMillis());

		private final boolean delay;

		private CustomNotificationListener(final boolean delay) {
			this.delay=delay;
		}

		@Override
		public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		private void consume(final Notification notification) {
			notification.consume();
			if(this.delay) {
				try {
					TimeUnit.MILLISECONDS.sleep(this.random.nextInt(10));
				} catch (final Exception e) {
				}
			}
		}
	}

	private static final Logger LOGGER=LoggerFactory.getLogger(CollectorAggregatorITest.class);

	@Test
	public void testLifecycle() throws Exception {
		final CustomNotificationListener listener=new CustomNotificationListener(false);
		final CollectorAggregator sut = CollectorAggregator.newInstance("example", listener);
		final Collector collector=defaultCollector();
		sut.connect(Arrays.asList(collector));
		final CollectorController controller=sut.controller(collector.getInstance());
		try {
			for(long i=0;i<1000;i++) {
				final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
				event.setInstance(collector.getInstance());
				event.setTimestamp(i);
				controller.publishEvent(event);
			}
			TimeUnit.SECONDS.sleep(5);
		} finally {
			sut.disconnect();
		}
	}

}
