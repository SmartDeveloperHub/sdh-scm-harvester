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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.4.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

final class CountingNotificationListener implements NotificationListener {

	private static final Logger LOGGER=LoggerFactory.getLogger(CountingNotificationListener.class);

	private final ConcurrentMap<String,EventCounter> notifications;
	private final CountDownLatch expectedNotifications;
	private final Random random;

	CountingNotificationListener(final CountDownLatch expectedNotifications) {
		this.expectedNotifications = expectedNotifications;
		this.notifications=Maps.newConcurrentMap();
		this.random=new Random(System.nanoTime());
	}

	private void processEvent(final Notification notification, final Event event) {
		sleep();
		incrementCounter(event);
		notification.consume();
		this.expectedNotifications.countDown();
		LOGGER.info("Consumed event {{}}{{}}{}",event.getInstance(),new Date(event.getTimestamp()),event.getClass().getSimpleName());
	}

	private void incrementCounter(final Event event) {
		final String instance = event.getInstance();
		final EventCounter transientCounter = new EventCounter(instance);
		EventCounter cachedCounter = this.notifications.putIfAbsent(instance,transientCounter);
		if(cachedCounter==null) {
			cachedCounter=transientCounter;
		}
		cachedCounter.count(event);
	}

	private void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(this.random.nextInt(100));
		} catch (final InterruptedException e) {
		}
	}

	@Override
	public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public String toString() {
		return
			MoreObjects.
				toStringHelper(getClass()).
					add("notifications",this.notifications).
					toString();
	}

	List<String> events(final String instance) {
		return this.notifications.get(instance).events();
	}

	int eventCount(final String instance, final String event) {
		return this.notifications.get(instance).count(event);
	}

}