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
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;

import com.google.common.collect.Iterables;

@RunWith(JMockit.class)
public class CollectorAggregatorTest extends NotificationTestHelper {

	private AtomicReference<CollectorController> setUpController() {
		final AtomicReference<CollectorController> reference=new AtomicReference<>();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Invocation invocation,final Collector aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				reference.set(invocation.<CollectorController>getInvokedInstance());
			}
			@Mock(invocations=1)
			void connect() { }
			@Mock(invocations=1)
			void disconnect() { }
		};
		return reference;
	}

	@Test
	public void testConnect$repeatedInstances(@Mocked final NotificationListener listener) throws Exception {
		final Collector collector = defaultCollector();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Collector aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				assertThat(aCollector,sameInstance(collector));
				assertThat(aName,not(isEmptyString()));
				assertThat(aQueue,notNullValue());
			}
			@Mock(invocations=1)
			void connect() { }
			@Mock(invocations=1)
			void disconnect() { }
		};
		final CollectorAggregator sut = CollectorAggregator.newInstance("example", listener);
		try {
			sut.connect(Arrays.asList(collector,defaultCollector()));
			fail("Should not allow multiple configurations for the same instance");
		} catch (final IllegalArgumentException e) {
			assertThat(e.getMessage(),equalTo("Multiple configurations found for collector "+collector.getInstance()));
		}
	}

	@Test
	public void testConnect$connectionFailure(@Mocked final NotificationListener listener) throws Exception {
		final Collector collector = defaultCollector();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Collector aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				assertThat(aCollector,sameInstance(collector));
				assertThat(aName,not(isEmptyString()));
				assertThat(aQueue,notNullValue());
			}
			@Mock(invocations=1)
			void connect() throws ControllerException {
				throw new ControllerException("Failure",null);
			}
			@Mock(invocations=0)
			void disconnect() { }
		};
		final CollectorAggregator sut = CollectorAggregator.newInstance("example", listener);
		try {
			sut.connect(Arrays.asList(collector));
		} catch (final IOException e) {
			assertThat(e.getCause(),instanceOf(ControllerException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
			assertThat(e.getMessage(),equalTo("Could not connect controller for collector "+collector.getInstance()+" ("+collector+")"));
		}
	}

	@Test
	public void testConnect$multipleInstancesWithSameBroker(@Mocked final NotificationListener listener) throws Exception {
		final AtomicReference<CollectorController> collector = setUpController();
		final CollectorAggregator sut = CollectorAggregator.newInstance("example", listener);
		try {
			sut.connect(Arrays.asList(instanceCollector("oneInstance"),instanceCollector("anotherInstance")));
			assertThat(sut.brokers(),hasSize(1));
			final String brokerId=Iterables.getFirst(sut.brokers(),null);
			assertThat(sut.instances(),hasItems("oneInstance","anotherInstance"));
			assertThat(sut.brokerInstances(brokerId),hasItems("oneInstance","anotherInstance"));
			assertThat(sut.controller("oneInstance"),sameInstance(collector.get()));
			assertThat(sut.controller("anotherInstance"),sameInstance(collector.get()));
		} finally {
			sut.disconnect();
		}
	}

	@Test
	public void testController$nullInstance(@Mocked final NotificationListener listener) throws Exception {
		setUpController();
		final CollectorAggregator sut = CollectorAggregator.newInstance("example", listener);
		try {
			sut.connect(Arrays.asList(instanceCollector("oneInstance"),instanceCollector("anotherInstance")));
			sut.controller(null);
			fail("Should not allow getting the controller of <null>");
		} catch (final NullPointerException e) {
			assertThat(e.getMessage(),equalTo("Instance cannot be null"));
		} finally {
			sut.disconnect();
		}
	}

	@Test
	public void testController$unknownInstance(@Mocked final NotificationListener listener) throws Exception {
		setUpController();
		final CollectorAggregator sut = CollectorAggregator.newInstance("example", listener);
		try {
			sut.connect(Arrays.asList(instanceCollector("oneInstance"),instanceCollector("anotherInstance")));
			sut.controller("unknown");
			fail("Should not allow getting the controller of an unknown instance");
		} catch (final IllegalArgumentException e) {
			assertThat(e.getMessage(),equalTo("Unknown instance 'unknown'"));
		} finally {
			sut.disconnect();
		}
	}

}