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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.session.WriteSession;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;

@RunWith(JMockit.class)
public class RepositoryContentPublisherTaskTest {

	@Mocked private BackendController controller;
	@Mocked private ApplicationContext context;
	@Mocked private WriteSession session;
	@Mocked private PublisherHelper helper;

	private RepositoryContentPublisherTask sut;

	@Before
	public void setUp() {
		this.sut=new RepositoryContentPublisherTask(this.controller);
	}

	@Test
	public void testProcessFailure() throws Exception {
		final URI target=URI.create("target");
		final List<Integer> repositories = Arrays.asList(1,2);
		new Expectations() {{
			RepositoryContentPublisherTaskTest.this.controller.getTarget();this.result=target;
			RepositoryContentPublisherTaskTest.this.controller.getRepositories();this.result=repositories;
			ApplicationContext.getInstance();this.result=RepositoryContentPublisherTaskTest.this.context;
			RepositoryContentPublisherTaskTest.this.context.createSession();this.result=RepositoryContentPublisherTaskTest.this.session;
			RepositoryContentPublisherTaskTest.this.controller.getRepository(1);this.result=new IOException("Failure");
			PublisherHelper.closeGracefully(RepositoryContentPublisherTaskTest.this.session);
		}};
		this.sut.call();
	}

	@Test
	public void testProcessError() throws Exception {
		final URI target=URI.create("target");
		final List<Integer> repositories = Arrays.asList(1,2);
		new Expectations() {{
			RepositoryContentPublisherTaskTest.this.controller.getTarget();this.result=target;
			RepositoryContentPublisherTaskTest.this.controller.getRepositories();this.result=repositories;
			ApplicationContext.getInstance();this.result=RepositoryContentPublisherTaskTest.this.context;
			RepositoryContentPublisherTaskTest.this.context.createSession();this.result=RepositoryContentPublisherTaskTest.this.session;
			RepositoryContentPublisherTaskTest.this.controller.getRepository(1);this.result=new Error("Failure");
			PublisherHelper.closeGracefully(RepositoryContentPublisherTaskTest.this.session);
		}};
		try {
			this.sut.call();
			fail("Should fail on error");
		} catch (final Error e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testProcessOk(@Mocked final Repository repository) throws Exception {
		final URI target=URI.create("target");
		final List<Integer> repositories = Arrays.asList(1,2);
		new Expectations() {{
			RepositoryContentPublisherTaskTest.this.controller.getTarget();this.result=target;
			RepositoryContentPublisherTaskTest.this.controller.getRepositories();this.result=repositories;
			ApplicationContext.getInstance();this.result=RepositoryContentPublisherTaskTest.this.context;this.times=2;
			RepositoryContentPublisherTaskTest.this.context.createSession();this.result=RepositoryContentPublisherTaskTest.this.session;this.times=2;
			RepositoryContentPublisherTaskTest.this.controller.getRepository(1);this.result=repository;this.times=1;
			RepositoryContentPublisherTaskTest.this.controller.getRepository(2);this.result=repository;this.times=1;
			PublisherHelper.publishRepository(RepositoryContentPublisherTaskTest.this.session, target, repository);this.times=2;
			RepositoryContentPublisherTaskTest.this.session.saveChanges();this.times=2;
			PublisherHelper.closeGracefully(RepositoryContentPublisherTaskTest.this.session);this.times=2;
		}};
		this.sut.call();
	}

}
