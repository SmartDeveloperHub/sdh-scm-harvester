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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.4.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.rest.EnhancerClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.RepositoryClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.UserClient;

@RunWith(JMockit.class)
public class EnhancerControllerTest extends ControllerTestHelper {

	private EnhancerController sut;

	@Before
	public void setUp() {
		this.sut= new EnhancerController("baseURL");
	}

	@Test
	public void testGetEnhancer$happyPath() throws IOException {
		new MockUp<EnhancerClient>() {
			@Mock
			public String getEnhancer() throws IOException {
				return loadResponse("enhancer.json");
			}
		};
		new MockUp<RepositoryClient>() {
			@Mock
			public String getRepositories() throws IOException {
				return loadResponse("repositories.json");
			}
		};
		new MockUp<UserClient>() {
			@Mock
			public String getUsers() throws IOException {
				return loadResponse("users.json");
			}
		};
		final Enhancer repository = this.sut.getEnhancer();
		assertThat(repository,notNullValue());
		assertThat(repository.getId(),equalTo("baseURL"));
		assertThat(repository.getRepositories(),notNullValue());
		assertThat(repository.getUsers(),notNullValue());
	}

}
