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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.ApplicationContextException;
import org.ldp4j.application.session.SessionTerminationException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Notification;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryUpdatedEvent;

@RunWith(JMockit.class)
public class PublishingNotificationListenerTest {

	private static final URI TARGET=URI.create("target");


	@Mocked private Notification notification;

	private PublishingNotificationListener sut;

	@Test
	public void testOnCommitterCreation(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final CommitterCreatedEvent event = new CommitterCreatedEvent();
		event.setInstance(TARGET.toString());
		final List<String> committers = Arrays.asList("committer1","committer2");
		event.setNewCommitters(committers);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.publishUsers(session, TARGET, committers);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onCommitterCreation(this.notification, event);
	}

	@Test
	public void testOnCommitterDeletion(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final CommitterDeletedEvent event = new CommitterDeletedEvent();
		event.setInstance(TARGET.toString());
		final List<String> committers = Arrays.asList("committer1","committer2");
		event.setDeletedCommitters(committers);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.unpublishUsers(session,committers);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onCommitterDeletion(this.notification, event);
	}

	@Test
	public void testOnRepositoryCreation(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final RepositoryCreatedEvent event = new RepositoryCreatedEvent();
		event.setInstance(TARGET.toString());
		final List<Integer> repositories = Arrays.asList(1,2);
		event.setNewRepositories(repositories);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.publishRepositories(session, TARGET, repositories);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onRepositoryCreation(this.notification, event);
	}

	@Test
	public void testOnRepositoryDeletion(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final RepositoryDeletedEvent event = new RepositoryDeletedEvent();
		event.setInstance(TARGET.toString());
		final List<Integer> repositories = Arrays.asList(1,2);
		event.setDeletedRepositories(repositories);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.unpublishRepositories(session,repositories);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onRepositoryDeletion(this.notification, event);
	}

	@Test
	public void testOnRepositoryUpdate(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(TARGET.toString());
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateRepository(session, event);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onRepositoryUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnPublisherHelperFailure(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(TARGET.toString());
		final IOException failure = new IOException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateRepository(session, event);this.result=failure;
			session.discardChanges();
			PublishingNotificationListenerTest.this.notification.discard(failure);
		}};
		this.sut.onRepositoryUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnWriteSessionException(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(TARGET.toString());
		final WriteSessionException failure = new WriteSessionException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateRepository(session, event);this.result=failure;
			PublishingNotificationListenerTest.this.notification.discard(failure);
		}};
		this.sut.onRepositoryUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnApplicationContextException(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(TARGET.toString());
		final ApplicationContextException failure = new ApplicationContextException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=failure;
			PublishingNotificationListenerTest.this.notification.discard(failure);
		}};
		this.sut.onRepositoryUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnSessionTerminationException(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper,@Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(TARGET.toString());
		final SessionTerminationException failure = new SessionTerminationException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateRepository(session, event);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
			session.close();this.result=failure;
		}};
		this.sut.onRepositoryUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnInterruptionWhileWaiting(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper) throws Exception {
		final CountDownLatch latch=new CountDownLatch(1);
		this.sut=new PublishingNotificationListener(latch,TARGET);
		final RepositoryUpdatedEvent event = new RepositoryUpdatedEvent();
		event.setInstance(TARGET.toString());
		new Expectations() {{
			PublishingNotificationListenerTest.this.notification.discard((Exception)this.any);
		}};
		final CountDownLatch l=new CountDownLatch(1);
		final Thread t1 = new Thread() {
			@Override
			public void run() {
				l.countDown();
				PublishingNotificationListenerTest.this.sut.onRepositoryUpdate(PublishingNotificationListenerTest.this.notification,event);
				assertThat(Thread.interrupted(),equalTo(true));
			}

		};
		t1.start();
		final Thread t2 = new Thread() {
			@Override
			public void run() {
				t1.interrupt();
			}
		};
		l.await();
		t2.start();
		t2.join();
		t1.join();
	}

}
