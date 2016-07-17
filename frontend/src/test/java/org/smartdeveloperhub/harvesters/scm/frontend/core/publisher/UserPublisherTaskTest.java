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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.publisher;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
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

@RunWith(JMockit.class)
public class UserPublisherTaskTest {

	@Mocked private BackendController controller;
	@Mocked private ApplicationContext context;
	@Mocked private WriteSession session;
	@Mocked private PublisherHelper helper;

	private UserPublisherTask sut;

	@Before
	public void setUp() {
		this.sut=new UserPublisherTask(this.controller);
	}

	@Test
	public void testNoCommitters() throws IOException {
		new Expectations() {{
			UserPublisherTaskTest.this.controller.getCommitters();this.result=Collections.emptyList();
		}};
		this.sut.call();
	}

	@Test
	public void testProcessFailure() throws Exception {
		final URI target=URI.create("target");
		final List<String> committers = Arrays.asList("committer1","committer2");
		new Expectations() {{
			UserPublisherTaskTest.this.controller.getTarget();this.result=target;
			UserPublisherTaskTest.this.controller.getCommitters();this.result=committers;
			ApplicationContext.getInstance();this.result=UserPublisherTaskTest.this.context;
			UserPublisherTaskTest.this.context.createSession();this.result=UserPublisherTaskTest.this.session;
			PublisherHelper.publishUsers(UserPublisherTaskTest.this.session, target, committers);this.result=new IOException("Failure");
			PublisherHelper.closeGracefully(UserPublisherTaskTest.this.session);
		}};
		this.sut.call();
	}

}
