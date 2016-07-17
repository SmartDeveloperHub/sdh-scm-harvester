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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

@RunWith(JMockit.class)
public class NotificationConsumerTest {

	@Mocked Channel channel;
	@Mocked Envelope envelope;
	@Mocked BlockingQueue<SuspendedNotification> pendingNotifications;
	@Mocked BasicProperties properties;
	@Mocked Map<String,Object> headers;

	@Test
	public void testConsumerDoesNotFailIfCannotProcessBody() throws Exception {
		final NotificationConsumer sut=new NotificationConsumer(this.channel, this.pendingNotifications);
		new Expectations() {{
			NotificationConsumerTest.this.envelope.getRoutingKey();this.result=Notifications.routingKey(CommitterCreatedEvent.class);
			NotificationConsumerTest.this.envelope.getDeliveryTag();this.result=1000;
			NotificationConsumerTest.this.properties.getHeaders();this.result=NotificationConsumerTest.this.headers;
			NotificationConsumerTest.this.headers.get(HttpHeaders.CONTENT_TYPE);this.result=Notifications.MIME;
			NotificationConsumerTest.this.channel.basicAck(1000,false);
		}};
		sut.handleDelivery("consumerTag", this.envelope, this.properties, "not json body".getBytes());
		new Verifications() {{
			NotificationConsumerTest.this.pendingNotifications.offer((SuspendedNotification)this.any);this.times=0;
		}};
	}

	@Test
	public void testConsumerDoesNotFailIfUnknownRoutingKey() throws Exception {
		final NotificationConsumer sut=new NotificationConsumer(this.channel, this.pendingNotifications);
		new Expectations() {{
			NotificationConsumerTest.this.envelope.getRoutingKey();this.result="Unknown";
			NotificationConsumerTest.this.envelope.getDeliveryTag();this.result=1000;
			NotificationConsumerTest.this.properties.getHeaders();this.result=NotificationConsumerTest.this.headers;
			NotificationConsumerTest.this.headers.get(HttpHeaders.CONTENT_TYPE);this.result=Notifications.MIME;
			NotificationConsumerTest.this.channel.basicAck(1000,false);
		}};
		sut.handleDelivery("consumerTag", this.envelope, this.properties, "not json body".getBytes());
		new Verifications() {{
			NotificationConsumerTest.this.pendingNotifications.offer((SuspendedNotification)this.any);this.times=0;
		}};
	}

	@Test
	public void testConsumerDoesNotFailIfNoHeadersAvailable() throws Exception {
		final NotificationConsumer sut=new NotificationConsumer(this.channel, this.pendingNotifications);
		new Expectations() {{
			NotificationConsumerTest.this.envelope.getRoutingKey();this.result=Notifications.routingKey(CommitterCreatedEvent.class);
			NotificationConsumerTest.this.envelope.getDeliveryTag();this.result=1000;
			NotificationConsumerTest.this.properties.getHeaders();this.result=null;
			NotificationConsumerTest.this.channel.basicAck(1000,false);
		}};
		sut.handleDelivery("consumerTag", this.envelope, this.properties, "not json body".getBytes());
		new Verifications() {{
			NotificationConsumerTest.this.pendingNotifications.offer((SuspendedNotification)this.any);this.times=0;
		}};
	}

	@Test
	public void testConsumerDoesNotFailIfNoContentTypeAvailable() throws Exception {
		final NotificationConsumer sut=new NotificationConsumer(this.channel, this.pendingNotifications);
		new Expectations() {{
			NotificationConsumerTest.this.envelope.getRoutingKey();this.result=Notifications.routingKey(CommitterCreatedEvent.class);
			NotificationConsumerTest.this.envelope.getDeliveryTag();this.result=1000;
			NotificationConsumerTest.this.properties.getHeaders();this.result=NotificationConsumerTest.this.headers;
			NotificationConsumerTest.this.headers.get(HttpHeaders.CONTENT_TYPE);this.result=null;
			NotificationConsumerTest.this.channel.basicAck(1000,false);
		}};
		sut.handleDelivery("consumerTag", this.envelope, this.properties, "not json body".getBytes());
		new Verifications() {{
			NotificationConsumerTest.this.pendingNotifications.offer((SuspendedNotification)this.any);this.times=0;
		}};
	}

	@Test
	public void testSuspendedNotificationResumesAndAcknowledgesIfListenerDoesNot() throws Exception {
		final CommitterCreatedEvent event=new CommitterCreatedEvent();
		setUpScenario(event);
		final BlockingQueue<SuspendedNotification> queue=new LinkedBlockingQueue<>();
		final NotificationConsumer sut=new NotificationConsumer(this.channel, queue);
		sut.handleDelivery("consumerTag", this.envelope, this.properties, EventUtil.marshall(event).getBytes());
		assertThat(queue,hasSize(1));
		Iterables.
			getFirst(queue, null).
				resume(
					new NotificationListener() {
						@Override
						public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
						}
						@Override
						public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
							fail("Unexpected notification");
						}
					});
	}

	@Test
	public void testSuspendedNotificationResumesAndAcknowledgesIfListenerFails() throws Exception {
		final CommitterDeletedEvent event=new CommitterDeletedEvent();
		setUpScenario(event);
		final BlockingQueue<SuspendedNotification> queue=new LinkedBlockingQueue<>();
		final NotificationConsumer sut=new NotificationConsumer(this.channel, queue);
		sut.handleDelivery("consumerTag", this.envelope, this.properties, EventUtil.marshall(event).getBytes());
		assertThat(queue,hasSize(1));
		try {
			Iterables.
				getFirst(queue,null).
					resume(
						new NotificationListener() {
							@Override
							public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
								fail("Unexpected notification");
							}
							@Override
							public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
								throw new RuntimeException("Failure");
							}
							@Override
							public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
								fail("Unexpected notification");
							}
							@Override
							public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
								fail("Unexpected notification");
							}
							@Override
							public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
								fail("Unexpected notification");
							}
						});
			fail("Should fail if listener fails");
		} catch (final RuntimeException e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testSuspendedNotificationDoesNotAcknowledgeIfListenerConsumes() throws Exception {
		final RepositoryDeletedEvent event=new RepositoryDeletedEvent();
		setUpScenario(event);
		final BlockingQueue<SuspendedNotification> queue=new LinkedBlockingQueue<>();
		final NotificationConsumer sut=new NotificationConsumer(this.channel, queue);
		sut.handleDelivery("consumerTag", this.envelope, this.properties, EventUtil.marshall(event).getBytes());
		assertThat(queue,hasSize(1));
		Iterables.
			getFirst(queue,null).
				resume(
					new NotificationListener() {
						@Override
						public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
							notification.consume();
						}
						@Override
						public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
							fail("Unexpected notification");
						}
					});
	}

	@Test
	public void testSuspendedNotificationDoesNotAcknowledgeIfListenerDiscards() throws Exception {
		final RepositoryCreatedEvent event=new RepositoryCreatedEvent();
		setUpScenario(event);
		final BlockingQueue<SuspendedNotification> queue=new LinkedBlockingQueue<>();
		final NotificationConsumer sut=new NotificationConsumer(this.channel, queue);
		sut.handleDelivery("consumerTag", this.envelope, this.properties, EventUtil.marshall(event).getBytes());
		assertThat(queue,hasSize(1));
		Iterables.
			getFirst(queue,null).
				resume(
					new NotificationListener() {
						@Override
						public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
							notification.discard(new RuntimeException("Failure"));
						}
						@Override
						public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
							fail("Unexpected notification");
						}
					});
	}

	@Test
	public void testConsumerProducesValidNotificationForRepositoryUpdatedEvent() throws Exception {
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		setUpScenario(event);
		final BlockingQueue<SuspendedNotification> queue=new LinkedBlockingQueue<>();
		final NotificationConsumer sut=new NotificationConsumer(this.channel, queue);
		sut.handleDelivery("consumerTag", this.envelope, this.properties, EventUtil.marshall(event).getBytes());
		assertThat(queue,hasSize(1));
		Iterables.
			getFirst(queue,null).
				resume(
					new NotificationListener() {
						@Override
						public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
							notification.consume();
						}
					});
	}

	@Test
	public void testConsumerProducesValidNotificationForCommiterDeletedEvent() throws Exception {
		final CommitterDeletedEvent event=new CommitterDeletedEvent();
		setUpScenario(event);
		final BlockingQueue<SuspendedNotification> queue=new LinkedBlockingQueue<>();
		final NotificationConsumer sut=new NotificationConsumer(this.channel, queue);
		sut.handleDelivery("consumerTag", this.envelope, this.properties, EventUtil.marshall(event).getBytes());
		assertThat(queue,hasSize(1));
		Iterables.
			getFirst(queue,null).
				resume(
					new NotificationListener() {
						@Override
						public void onCommitterCreation(final Notification notification, final CommitterCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onCommitterDeletion(final Notification notification, final CommitterDeletedEvent event) {
							notification.consume();
						}
						@Override
						public void onRepositoryCreation(final Notification notification, final RepositoryCreatedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryDeletion(final Notification notification, final RepositoryDeletedEvent event) {
							fail("Unexpected notification");
						}
						@Override
						public void onRepositoryUpdate(final Notification notification, final RepositoryUpdatedEvent event) {
							fail("Unexpected notification");
						}
					});
	}

	private void setUpScenario(final Event event) throws IOException {
		event.setInstance(event.getClass().getName());
		event.setTimestamp(System.nanoTime());
		new Expectations() {{
			NotificationConsumerTest.this.envelope.getRoutingKey();this.result=Notifications.routingKey(event.getClass());
			NotificationConsumerTest.this.envelope.getDeliveryTag();this.result=1000;
			NotificationConsumerTest.this.properties.getHeaders();this.result=NotificationConsumerTest.this.headers;
			NotificationConsumerTest.this.headers.get(HttpHeaders.CONTENT_TYPE);this.result=Notifications.MIME;
			NotificationConsumerTest.this.channel.basicAck(1000,false);this.times=1;
		}};
	}

}
