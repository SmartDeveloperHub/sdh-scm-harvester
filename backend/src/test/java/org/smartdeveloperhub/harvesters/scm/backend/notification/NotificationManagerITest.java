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
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.controller.EnhancerController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class NotificationManagerITest {

	private static final String GITLAB_ENHANCER = "http://russell.dia.fi.upm.es:5000/api";

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationManagerITest.class);

	private ExecutorService pool;

	@Before
	public void setUp() {
		this.pool =
			Executors.
				newFixedThreadPool(
					5,
					new ThreadFactoryBuilder().
						setNameFormat("Collector-Agent-%d").
						setUncaughtExceptionHandler(
							new UncaughtExceptionHandler() {
								@Override
								public void uncaughtException(final Thread t, final Throwable e) {
									LOGGER.error("{} thread died unexpectedly: ",t.getName(),e);
								}
							}
						).
						build());
	}

	@After
	public void tearDown() {
		if(!this.pool.isShutdown()) {
			shutdownPool();
		}
	}

	@Test
	public void testMultipleCollectors() throws IOException {
		final List<Collector> collectors=
			ImmutableList.
				<Collector>builder().
					add(collector("http://www.example.org:5000/collector/1", "exchange1")).
					add(collector("http://www.example.org:5000/collector/2", "exchange1")).
					add(collector("http://www.example.org:5000/collector/3", "exchange2")).
					build();
		new MockUp<EnhancerController>() {
			@Mock(invocations=1)
			void $init(final String target) {
				assertThat(target,equalTo(GITLAB_ENHANCER));
			}
			@Mock(invocations=1)
			Enhancer getEnhancer() throws IOException {
				final Enhancer enhancer = new Enhancer();
				enhancer.setId(GITLAB_ENHANCER);
				enhancer.setCollectors(collectors);
				return enhancer;
			}
		};
		final int rounds = 10;
		final CountDownLatch expectedNotifications=new CountDownLatch(collectors.size()*rounds*8);
		final CountingNotificationListener listener = new CountingNotificationListener(expectedNotifications);
		final NotificationManager sut = NotificationManager.newInstance(URI.create(GITLAB_ENHANCER),listener);
		LOGGER.info("Starting Notitication Manager...");
		sut.start();
		try {
			LOGGER.info("Sending notifications...");
			final CollectorAggregator aggregator=Deencapsulation.getField(sut,"aggregator");
			for(int i=0;i<rounds;i++) {
				for(final Collector collector:collectors) {
					publishEvent(aggregator, createCommitters(collector, "cc1"+i,"cc2"+i));
					publishEvent(aggregator, deleteCommitters(collector, "dc1"+i,"dc2"+i));
					publishEvent(aggregator, createRepositories(collector, i+1,i+2));
					publishEvent(aggregator, deleteRepositories(collector, i*rounds+1,i*rounds+2));
					publishEvent(aggregator, createRepositoryBranches(collector, i*rounds+1,"cbr1"+i,"cbr2"+i));
					publishEvent(aggregator, deleteRepositoryBranches(collector, i*rounds+2,"dbr1"+i,"dbr2"+i));
					publishEvent(aggregator, createRepositoryCommits(collector, i*rounds+3,"ccommit1"+i,"ccommit2"+i));
					publishEvent(aggregator, deleteRepositoryCommits(collector, i*rounds+4,"dcommit"+i,"dcommit2"+i));
				}
			}
			shutdownPool();
			LOGGER.info("Notifications sent");
			expectedNotifications.await();
			LOGGER.info("Notifications received");
		} catch (final InterruptedException e) {
			LOGGER.warn("Interrupted while awaiting for the reception of all the notifications sent");
		} finally {
			sut.shutdown();
		}
		LOGGER.info("Summary of received notifications:");
		for(final Collector collector:collectors) {
			LOGGER.info(" + {}:",collector.getInstance());
			for(final String event:listener.events(collector.getInstance())) {
				LOGGER.info("  - {} : {}",event,listener.eventCount(collector.getInstance(),event));
			}
		}
	}

	private void publishEvent(final CollectorAggregator aggregator, final Event event) {
		this.pool.execute(new EventPublisher(event, aggregator));
	}

	private void shutdownPool() {
		this.pool.shutdown();
		while(!this.pool.isTerminated()) {
			try {
				this.pool.awaitTermination(100,TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
				LOGGER.warn("Interrupted while awaiting for the emission of all the notifications");
			}
		}
	}

	private CommitterCreatedEvent createCommitters(final Collector collector, final String... values) {
		final CommitterCreatedEvent event = new CommitterCreatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setNewCommitters(Arrays.asList(values));
		return event;
	}

	private CommitterDeletedEvent deleteCommitters(final Collector collector, final String... values) {
		final CommitterDeletedEvent event = new CommitterDeletedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setDeletedCommitters(Arrays.asList(values));
		return event;
	}

	private RepositoryCreatedEvent createRepositories(final Collector collector, final Integer... values) {
		final RepositoryCreatedEvent event = new RepositoryCreatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setNewRepositories(Arrays.asList(values));
		return event;
	}

	private RepositoryDeletedEvent deleteRepositories(final Collector collector, final Integer... values) {
		final RepositoryDeletedEvent event = new RepositoryDeletedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setDeletedRepositories(Arrays.asList(values));
		return event;
	}

	private RepositoryUpdatedEvent createRepositoryBranches(final Collector collector, final int id, final String... values) {
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setRepository(id);
		event.setNewBranches(Arrays.asList(values));
		return event;
	}

	private RepositoryUpdatedEvent deleteRepositoryBranches(final Collector collector, final int id, final String... values) {
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setRepository(id);
		event.setDeletedBranches(Arrays.asList(values));
		return event;
	}

	private RepositoryUpdatedEvent createRepositoryCommits(final Collector collector, final int id, final String... values) {
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setRepository(id);
		event.setNewCommits(Arrays.asList(values));
		return event;
	}

	private RepositoryUpdatedEvent deleteRepositoryCommits(final Collector collector, final int id, final String... values) {
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setRepository(id);
		event.setDeletedCommits(Arrays.asList(values));
		return event;
	}

	private Collector collector(final String instance, final String exchangeName) {
		final Collector collector=new Collector();
		collector.setInstance(instance);
		collector.setBrokerHost("localhost");
		collector.setBrokerPort(5672);
		collector.setVirtualHost("/");
		collector.setExchangeName(exchangeName);
		return collector;
	}

}
