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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0
 *   Bundle      : scm-harvester-backend-0.3.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

final class NotificationConsumer extends DefaultConsumer {

	private interface NotificationHandler {

		boolean canHandle(String routingKey);

		SuspendedNotification suspend(AcknowledgeableNotification notification, String payload) throws IOException;

	}

	protected abstract static class CustomSuspendedNotification<T extends Event> implements SuspendedNotification {

		private final AcknowledgeableNotification notification;
		private final T event;

		protected CustomSuspendedNotification(final AcknowledgeableNotification notification, final T event) {
			this.notification = notification;
			this.event = event;
		}

		protected final T getEvent() {
			return this.event;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void consume() {
			this.notification.consume();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void discard(final Throwable exception) {
			this.notification.discard(exception);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void resume(final NotificationListener listener) {
			try {
				doResume(listener);
			} finally {
				enforceAcknowledgement();
			}
		}

		private void enforceAcknowledgement() {
			if(!this.notification.isAcknowledged()) {
				this.notification.acknowledge();
			}
		}

		protected abstract void doResume(NotificationListener listener);

	}

	private abstract static class CustomNotificationHandler<T extends Event> implements NotificationHandler {

		private final String acceptedKey;
		private final Class<? extends T> clazz;

		protected CustomNotificationHandler(final Class<? extends T> clazz) {
			this.clazz = clazz;
			this.acceptedKey=Notifications.routingKey(clazz);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean canHandle(final String routingKey) {
			return this.acceptedKey.equals(routingKey);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final SuspendedNotification suspend(final AcknowledgeableNotification notification, final String payload) throws IOException {
			return
				createPropagator(
					notification,
					EventUtil.unmarshall(payload, this.clazz));
		}

		protected abstract CustomSuspendedNotification<T> createPropagator(AcknowledgeableNotification notification, T event);

	}

	private static final class CommitterCreatedNotificationHandler extends CustomNotificationHandler<CommitterCreatedEvent> {

		protected CommitterCreatedNotificationHandler() {
			super(CommitterCreatedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<CommitterCreatedEvent> createPropagator(final AcknowledgeableNotification notification, final CommitterCreatedEvent event) {
			return new CustomSuspendedNotification<CommitterCreatedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onCommitterCreation(this, super.getEvent());
				}
			};
		}

	}

	private static final class CommitterDeletedNotificationHandler extends CustomNotificationHandler<CommitterDeletedEvent> {

		protected CommitterDeletedNotificationHandler() {
			super(CommitterDeletedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<CommitterDeletedEvent> createPropagator(final AcknowledgeableNotification notification, final CommitterDeletedEvent event) {
			return new CustomSuspendedNotification<CommitterDeletedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onCommitterDeletion(this,super.getEvent());
				}
			};
		}

	}
	private static final class RepositoryCreatedNotificationHandler extends CustomNotificationHandler<RepositoryCreatedEvent> {

		protected RepositoryCreatedNotificationHandler() {
			super(RepositoryCreatedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<RepositoryCreatedEvent> createPropagator(final AcknowledgeableNotification notification, final RepositoryCreatedEvent event) {
			return new CustomSuspendedNotification<RepositoryCreatedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onRepositoryCreation(this,super.getEvent());
				}
			};
		}
	}

	private static final class RepositoryDeletedNotificationHandler extends CustomNotificationHandler<RepositoryDeletedEvent> {

		protected RepositoryDeletedNotificationHandler() {
			super(RepositoryDeletedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<RepositoryDeletedEvent> createPropagator(final AcknowledgeableNotification notification, final RepositoryDeletedEvent event) {
			return new CustomSuspendedNotification<RepositoryDeletedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onRepositoryDeletion(this,super.getEvent());
				}
			};
		}
	}

	private static final class RepositoryUpdatedNotificationHandler extends CustomNotificationHandler<RepositoryUpdatedEvent> {

		protected RepositoryUpdatedNotificationHandler() {
			super(RepositoryUpdatedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<RepositoryUpdatedEvent> createPropagator(final AcknowledgeableNotification notification, final RepositoryUpdatedEvent event) {
			return new CustomSuspendedNotification<RepositoryUpdatedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onRepositoryUpdate(this,super.getEvent());
				}
			};
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationConsumer.class);

	private final BlockingQueue<SuspendedNotification> notifications;
	private final List<NotificationHandler> handlers;

	NotificationConsumer(final Channel channel, final BlockingQueue<SuspendedNotification> pendingNotifications) {
		super(channel);
		this.notifications = pendingNotifications;
		this.handlers=
			ImmutableList.
				<NotificationHandler>builder().
					add(new CommitterCreatedNotificationHandler()).
					add(new CommitterDeletedNotificationHandler()).
					add(new RepositoryCreatedNotificationHandler()).
					add(new RepositoryDeletedNotificationHandler()).
					add(new RepositoryUpdatedNotificationHandler()).
					build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleDelivery(final String consumerTag, final Envelope envelope, final BasicProperties properties, final byte[] body) throws IOException {
		final String payload=new String(body, "UTF-8");
		final String routingKey=envelope.getRoutingKey();
		final AcknowledgeableNotification notification=
			new AcknowledgeableNotification(
				super.getChannel(),
				envelope.getDeliveryTag());
		try {
			verifyHeader(properties);
			this.notifications.
				offer(
					findHandler(routingKey).
						suspend(notification,payload));
		} catch(final Exception e) {
			LOGGER.error("Discarding message:\n{}\nReason:\n",payload,e);
			notification.acknowledge();
		}
	}

	private NotificationHandler findHandler(final String routingKey) {
		for(final NotificationHandler handler:this.handlers) {
			if(handler.canHandle(routingKey)) {
				return handler;
			}
		}
		throw new IllegalStateException("Unsupported routing key "+routingKey);
	}

	private void verifyHeader(final BasicProperties properties) throws IOException {
		final Object header = properties.getHeaders().get(HttpHeaders.CONTENT_TYPE);
		checkNotNull(header,"No %s header defined",HttpHeaders.CONTENT_TYPE);
		checkArgument(Notifications.MIME.equals(header.toString()),"Unsupported %s header (%s)",HttpHeaders.CONTENT_TYPE,header);
	}

}