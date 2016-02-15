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

import java.io.IOException;
import java.net.URI;
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

	abstract class NotificationHandler<T extends Event> {

		private WriteSession session() throws InterruptedException, ApplicationContextException {
			PublishingNotificationListener.this.publishingCompleted.await();
			return ApplicationContext.getInstance().createSession();
		}

		final void consumeEvent(final Notification notification, final T event) {
			try(WriteSession session=session()) {
				doConsumeEvent(notification, event, session);
			} catch(final InterruptedException e) {
				Thread.currentThread().interrupt();
				notification.discard(e);
			} catch(final WriteSessionException | SessionTerminationException | ApplicationContextException e) {
				notification.discard(e);
			}
		}

		private void doConsumeEvent(final Notification notification, final T event, final WriteSession session) throws WriteSessionException {
			try {
				handleEvent(event, session);
				notification.consume();
				session.saveChanges();
			} catch (final IOException e) {
				session.discardChanges();
				notification.discard(e);
			}
		}

		abstract void handleEvent(final T event, final WriteSession session) throws IOException;

	}

	final class CommitterCreationHandler extends NotificationHandler<CommitterCreatedEvent> {

		@Override
		void handleEvent(final CommitterCreatedEvent event, final WriteSession session) {
			PublisherHelper.publishUsers(session, event.getNewCommitters());
		}

	}

	final class CommitterDeletionHandler extends NotificationHandler<CommitterDeletedEvent> {

		@Override
		void handleEvent(final CommitterDeletedEvent event, final WriteSession session) {
			PublisherHelper.unpublishUsers(session, event.getDeletedCommitters());
		}

	}

	final class RepositoryCreationHandler extends NotificationHandler<RepositoryCreatedEvent> {

		@Override
		void handleEvent(final RepositoryCreatedEvent event, final WriteSession session) {
			PublisherHelper.
				publishRepositories(
					session,
					PublishingNotificationListener.this.target,
					event.getNewRepositories());
		}

	}

	final class RepositoryDeletionHandler extends NotificationHandler<RepositoryDeletedEvent> {

		@Override
		void handleEvent(final RepositoryDeletedEvent event, final WriteSession session) {
			PublisherHelper.unpublishRepositories(session,event.getDeletedRepositories());
		}

	}

	final class RepositoryUpdateHandler extends NotificationHandler<RepositoryUpdatedEvent> {

		@Override
		void handleEvent(final RepositoryUpdatedEvent event, final WriteSession session) throws IOException {
			PublisherHelper.updateRepository(session,event);
		}

	}

	@SuppressWarnings("unused")
	private static final Logger LOGGER=LoggerFactory.getLogger(PublishingNotificationListener.class);

	private final CountDownLatch publishingCompleted;

	private final URI target;

	PublishingNotificationListener(final CountDownLatch publishingCompleted, final URI target) {
		this.publishingCompleted = publishingCompleted;
		this.target = target;
	}

	@Override
	public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
		new CommitterCreationHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
		new CommitterDeletionHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
		new RepositoryCreationHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
		new RepositoryDeletionHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
		new RepositoryUpdateHandler().
			consumeEvent(notification, event);
	}

}