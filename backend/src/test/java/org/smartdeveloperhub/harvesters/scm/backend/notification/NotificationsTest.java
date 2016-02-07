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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;


public class NotificationsTest {

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationsTest.class);

	@Test
	public void verifyIsUtilityClass() {
		assertThat(Utils.isUtilityClass(Notifications.class),equalTo(true));
	}

	@Test
	public void testNotifications() throws ControllerException, IOException, InterruptedException {
		final Collector collector=new Collector();
		collector.setInstance("http://russell.dia.fi.upm.es:5000/api");
		collector.setBrokerHost("localhost");
		collector.setBrokerPort(5672);
		collector.setVirtualHost("/");
		collector.setExchangeName("sdh");

		final BlockingQueue<SuspendedNotification> notifications=new LinkedBlockingQueue<SuspendedNotification>();
		final CollectorController controller=new CollectorController(collector,"client",notifications);
		final NotificationPump pump=
			new NotificationPump(
				notifications,
				new NotificationListener() {
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
					private final Random random = new Random(System.currentTimeMillis());
					private void consume(final Notification notification) {
						notification.consume();
						try {
							TimeUnit.MILLISECONDS.sleep(this.random.nextInt(10));
						} catch (final Exception e) {
						}
					}
				}
			);
		pump.start();
		controller.connect();
		try {
			for(long i=0;i<1000;i++) {
				final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
				event.setInstance("138.100.10.216");
				event.setTimestamp(i);
				controller.publishEvent(event);
			}
			TimeUnit.SECONDS.sleep(5);
		} finally {
			pump.stop();
			controller.disconnect();
		}
	}

}
