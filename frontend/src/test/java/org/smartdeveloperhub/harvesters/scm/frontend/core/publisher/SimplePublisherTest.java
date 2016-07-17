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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.publisher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.session.WriteSession;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;

@RunWith(JMockit.class)
public class SimplePublisherTest {

	private static final String ENDPOINT = "http://www.example.org:5000/api";
	private static final URI GITLAB_ENHANCER = URI.create(ENDPOINT);

	@Mocked private BackendController controller;
	@Mocked private WriteSession session;

	private SimplePublisher sut;

	@Test
	public void testInitialize(@Mocked final PublisherHelper helper) throws Exception {
		new MockUp<BranchCommitPublisherThread>() {
			@Mock(invocations=1)
			void $init(final BackendController aController) {
				assertThat(aController,sameInstance(SimplePublisherTest.this.controller));
			}
		};
		new MockUp<UserPublisherThread>() {
			@Mock(invocations=1)
			void $init(final BackendController aController) {
				assertThat(aController,sameInstance(SimplePublisherTest.this.controller));
			}
		};
		this.sut=new SimplePublisher(this.controller);
		final List<String> repositories=Arrays.asList("1","2");
		new Expectations() {{
			SimplePublisherTest.this.controller.getTarget();this.result=GITLAB_ENHANCER;
			SimplePublisherTest.this.controller.getRepositories();this.result=repositories;
			PublisherHelper.publishHarvester(SimplePublisherTest.this.session, GITLAB_ENHANCER, repositories);
		}};
		this.sut.initialize(this.session);
	}

	@Test
	public void testLifecycle(@Mocked final PublisherHelper helper) throws Exception {
		final AtomicInteger count=new AtomicInteger();
		new MockUp<RepositoryContentPublisherTask>() {
			@Mock(invocations=1)
			void $init(final BackendController aController) {
				assertThat(aController,sameInstance(SimplePublisherTest.this.controller));
			}
			@Mock(invocations=1)
			Boolean call() {
				count.incrementAndGet();
				return true;
			}
		};
		new MockUp<UserPublisherTask>() {
			@Mock(invocations=1)
			void $init(final BackendController aController) {
				assertThat(aController,sameInstance(SimplePublisherTest.this.controller));
			}
			@Mock(invocations=1)
			Boolean call() {
				count.incrementAndGet();
				return true;
			}
		};
		this.sut=new SimplePublisher(this.controller);
		this.sut.start();
		this.sut.stop();
		assertThat(count.get(),equalTo(2));
	}

}
