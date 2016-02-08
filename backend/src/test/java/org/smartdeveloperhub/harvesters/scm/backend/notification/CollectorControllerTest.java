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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;

import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

@RunWith(JMockit.class)
public class CollectorControllerTest extends NotificationTestHelper {

	@Test
	public void testCreatePublisher$nullCollector() throws Exception {
		try {
			CollectorController.createPublisher(null);
			fail("Should not create controller with null collector");
		} catch (final NullPointerException e) {
			assertThat(e.getMessage(),equalTo("Collector cannot be null"));
		}
	}

	@Test
	public void testCreateAnonymousCollector$nullNotificationQueue() throws Exception {
		try {
			CollectorController.createAnonymousReceiver(defaultCollector(),null);
			fail("Should not create receiver with null blocking queue");
		} catch (final NullPointerException e) {
			assertThat(e.getMessage(),equalTo("Notification queue cannot be null"));
		}
	}

	@Test
	public void testCreateNamedCollector$nullQueueName() throws Exception {
		try {
			CollectorController.createNamedReceiver(defaultCollector(),null,null);
			fail("Should not create receiver with null queue name");
		} catch (final NullPointerException e) {
			assertThat(e.getMessage(),equalTo("Queue name cannot be null"));
		}
	}

	@Test
	public void testCreateNamedCollector$nullNotificationQueue() throws Exception {
		try {
			CollectorController.createNamedReceiver(defaultCollector(),"name",null);
			fail("Should not create receiver with null blocking queue");
		} catch (final NullPointerException e) {
			assertThat(e.getMessage(),equalTo("Notification queue cannot be null"));
		}
	}

	@Test
	public void testCollector() {
		final Collector defaultCollector = defaultCollector();
		final CollectorController sut = collectorController(defaultCollector);
		assertThat(sut.collector(),sameInstance(defaultCollector));
	}

	@Test
	public void testActualQueueName$requiresBeingConnected() {
		final CollectorController sut = defaultController();
		try {
			sut.actualQueueName();
			fail("Should not allow retrieving the actual queue name without being connected");
		} catch (final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("Not connected"));
		}
	}

	@Test
	public void testActualQueueName$onlyAvailableForReceivers() throws ControllerException {
		final CollectorController sut = CollectorController.createPublisher(defaultCollector());
		sut.connect();
		try {
			assertThat(sut.actualQueueName(),nullValue());
		} finally {
			sut.disconnect();
		}
	}

	@Test
	public void testActualQueueName$anonymous() throws ControllerException {
		final LinkedBlockingQueue<SuspendedNotification> queue = new LinkedBlockingQueue<SuspendedNotification>();
		final Collector defaultCollector = defaultCollector();
		final CollectorController sut = CollectorController.createAnonymousReceiver(defaultCollector,queue);
		try {
			sut.connect();
			assertThat(sut.actualQueueName(),notNullValue());
		} finally {
			sut.disconnect();
		}
	}

	@Test
	public void testActualQueueName$named() throws ControllerException {
		final CollectorController sut = defaultController();
		try {
			sut.connect();
			assertThat(sut.actualQueueName(),equalTo("name"));
		} finally {
			sut.disconnect();
		}
	}

	@Test
	public void testQueueName$anonymous() throws ControllerException {
		final LinkedBlockingQueue<SuspendedNotification> queue = new LinkedBlockingQueue<SuspendedNotification>();
		final Collector defaultCollector = defaultCollector();
		final CollectorController sut = CollectorController.createAnonymousReceiver(defaultCollector,queue);
		assertThat(sut.queueName(),nullValue());
	}

	@Test
	public void testQueueName$named() throws ControllerException {
		final CollectorController sut = defaultController();
		assertThat(sut.queueName(),equalTo("name"));
	}

	@Test
	public void testConnect$cannotConnectTwice() throws ControllerException {
		final CollectorController sut = defaultController();
		sut.connect();
		try {
			sut.connect();
			fail("Should not allow connecting twice");
		} catch (final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("Already connected"));
		}
	}

	@Test
	public void testConnect$shouldConnectIfExchangeAlreadyAvailable(@Mocked final Channel channel) throws Exception {
		new MockUp<ConnectionManager>() {
			private boolean connected=false;
			@Mock(invocations=1)
			void connect() {
				this.connected=true;
			}
			@Mock(invocations=1)
			void disconnect() {
			}
			@Mock
			boolean isConnected() {
				return this.connected;
			}
			@Mock
			Channel channel() {
				return channel;
			}
		};
		new MockUp<FailureAnalyzer>() {
			@Mock
			boolean isExchangeDeclarationRecoverable(final IOException e) {
				return true;
			}
		};
		final Collector defaultCollector = defaultCollector();
		new Expectations() {{
			channel.exchangeDeclare(defaultCollector.getExchangeName(), "topic", true);this.result=new IOException("Failure");
		}};
		final CollectorController sut = collectorController(defaultCollector);
		try {
			sut.connect();
		} finally {
			sut.disconnect();
		}
	}

	@Test
	public void testConnect$shouldCleanUpOnExchangeSetupFailure(@Mocked final Channel channel) throws Exception {
		new MockUp<ConnectionManager>() {
			private boolean connected=false;
			@Mock(invocations=1)
			void connect() {
				this.connected=true;
			}
			@Mock(invocations=1)
			void disconnect() {
			}
			@Mock
			boolean isConnected() {
				return this.connected;
			}
			@Mock
			Channel channel() {
				return channel;
			}
		};
		final Collector defaultCollector = defaultCollector();
		new Expectations() {{
			channel.exchangeDeclare(defaultCollector.getExchangeName(), "topic", true);this.result=new IOException("Failure");
		}};
		final CollectorController sut = collectorController(defaultCollector);
		try {
			sut.connect();
			fail("Should not connect on failure");
		} catch (final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not create exchange named '"+defaultCollector.getExchangeName()+"'"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConnect$shouldCleanUpOnQueueDeclarationFailure(@Mocked final Channel channel) throws Exception {
		new MockUp<ConnectionManager>() {
			private boolean connected=false;
			@Mock(invocations=1)
			void connect() {
				this.connected=true;
			}
			@Mock(invocations=1)
			void disconnect() {
			}
			@Mock
			boolean isConnected() {
				return this.connected;
			}
			@Mock
			Channel channel() {
				return channel;
			}
		};
		final Collector defaultCollector = defaultCollector();
		new Expectations() {{
			channel.queueDeclare((String)this.any,true,true,true,(Map<String,Object>)this.any);this.result=new IOException("Failure");
		}};
		final CollectorController sut = collectorController(defaultCollector);
		try {
			sut.connect();
			fail("Should not connect on failure");
		} catch (final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not create queue named 'name'"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConnect$shouldCleanUpOnQueueBindingFailure(@Mocked final Channel channel, @Mocked final Queue.DeclareOk ok) throws Exception {
		new MockUp<ConnectionManager>() {
			private boolean connected=false;
			@Mock(invocations=1)
			void connect() {
				this.connected=true;
			}
			@Mock(invocations=1)
			void disconnect() {
			}
			@Mock
			boolean isConnected() {
				return this.connected;
			}
			@Mock
			Channel channel() {
				return channel;
			}
		};
		final Collector defaultCollector = defaultCollector();
		new Expectations() {{
			channel.queueDeclare((String)this.any,true,true,true,(Map<String,Object>)this.any);this.result=ok;
			ok.getQueue();this.result="actualQueueName";
			channel.queueBind("actualQueueName",defaultCollector.getExchangeName(),Notifications.ROUTING_KEY_PATTERN);this.result=new IOException("Failure");
		}};
		final CollectorController sut = collectorController(defaultCollector);
		try {
			sut.connect();
			fail("Should not connect on failure");
		} catch (final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not bind queue 'actualQueueName' to exchange '"+defaultCollector.getExchangeName()+"' using routing key '"+Notifications.ROUTING_KEY_PATTERN+"'"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConnect$shouldCleanUpOnConsumerRegistrationFailure(@Mocked final Channel channel, @Mocked final Queue.DeclareOk ok) throws Exception {
		new MockUp<ConnectionManager>() {
			private boolean connected=false;
			@Mock(invocations=1)
			void connect() {
				this.connected=true;
			}
			@Mock(invocations=1)
			void disconnect() {
			}
			@Mock
			boolean isConnected() {
				return this.connected;
			}
			@Mock
			Channel channel() {
				return channel;
			}
		};
		final Collector defaultCollector = defaultCollector();
		new Expectations() {{
			channel.queueDeclare((String)this.any,true,true,true,(Map<String,Object>)this.any);this.result=ok;
			ok.getQueue();this.result="actualQueueName";
			channel.basicConsume("actualQueueName",false,(Consumer)this.any);this.result=new IOException("Failure");
		}};
		final CollectorController sut = collectorController(defaultCollector);
		try {
			sut.connect();
			fail("Should not connect on failure");
		} catch (final ControllerException e) {
			assertThat(e.getMessage(),equalTo("Could not register consumer for queue 'actualQueueName'"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testDisconnect$shouldUnlockOnFailure(@Mocked final Channel channel) throws Exception {
		new MockUp<ConnectionManager>() {
			private boolean connected=false;
			@Mock(invocations=1)
			void connect() {
				this.connected=true;
			}
			@Mock(invocations=1)
			void disconnect() {
				throw new RuntimeException("Failure");
			}
			@Mock
			boolean isConnected() {
				return this.connected;
			}
			@Mock
			Channel channel() {
				return channel;
			}
		};
		final CollectorController sut = defaultController();
		sut.connect();
		try {
			sut.disconnect();
			fail("Should not shallow exceptions");
		} catch (final RuntimeException e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testDisconnect$isIdempotent() throws ControllerException, IOException {
		final CollectorController sut = CollectorController.createPublisher(defaultCollector());
		sut.disconnect();
		sut.disconnect();
	}

	@Test
	public void testPublishEvent$requiresBeingConnected() throws ControllerException, IOException {
		final CollectorController sut = CollectorController.createPublisher(defaultCollector());
		try {
			sut.publishEvent("payload","type");
			fail("Should not publishing events without being connected");
		} catch (final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("Not connected"));
		}
	}

	private CollectorController defaultController() {
		final Collector defaultCollector = defaultCollector();
		final CollectorController sut = collectorController(defaultCollector);
		return sut;
	}

	private CollectorController collectorController(final Collector defaultCollector) {
		final LinkedBlockingQueue<SuspendedNotification> queue = new LinkedBlockingQueue<SuspendedNotification>();
		final CollectorController sut = CollectorController.createNamedReceiver(defaultCollector,"name",queue);
		return sut;
	}

}
