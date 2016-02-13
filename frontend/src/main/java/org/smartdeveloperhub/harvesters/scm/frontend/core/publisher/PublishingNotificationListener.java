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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.publisher;

import java.util.concurrent.CountDownLatch;

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.ApplicationContextException;
import org.ldp4j.application.session.SessionTerminationException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Event;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Notification;
import org.smartdeveloperhub.harvesters.scm.backend.notification.NotificationListener;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryUpdatedEvent;

final class PublishingNotificationListener implements NotificationListener {

	private static final Logger LOGGER=LoggerFactory.getLogger(PublishingNotificationListener.class);

	private final CountDownLatch publishingCompleted;

	PublishingNotificationListener(final CountDownLatch publishingCompleted) {
		this.publishingCompleted = publishingCompleted;
	}

	private WriteSession session() throws InterruptedException, ApplicationContextException {
		this.publishingCompleted.await();
		return ApplicationContext.getInstance().createSession();
	}

	private void consumeEvent(final Notification notification, final Event event) {
		try(WriteSession session=session()) {
			LOGGER.warn("TODO: Implement consumption of {}",event);
			session.discardChanges();
			notification.consume();
		} catch(final InterruptedException e) {
			Thread.currentThread().interrupt();
			notification.discard(e);
		} catch(final WriteSessionException | SessionTerminationException | ApplicationContextException e) {
			notification.discard(e);
		}
	}

	@Override
	public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
		consumeEvent(notification, event);
	}

	@Override
	public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
		consumeEvent(notification, event);
	}

	@Override
	public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
		consumeEvent(notification, event);
	}

	@Override
	public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
		consumeEvent(notification, event);
	}

	@Override
	public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
		consumeEvent(notification, event);
	}

}