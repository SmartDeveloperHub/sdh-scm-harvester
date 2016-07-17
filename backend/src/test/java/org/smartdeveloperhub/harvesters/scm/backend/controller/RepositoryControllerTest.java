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
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.readers.RepositoryReader;
import org.smartdeveloperhub.harvesters.scm.backend.rest.BranchClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.CommitClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.RepositoryClient;

@RunWith(JMockit.class)
public class RepositoryControllerTest extends ControllerTestHelper {

	private RepositoryController sut;

	@Before
	public void setUp() {
		this.sut= new RepositoryController("baseURL");
	}

	@Test
	public void testGetRepositories$happyPath() throws IOException {
		final String response = loadResponse("repositories.json");
		new MockUp<RepositoryClient>() {
			@Mock
			public String getRepositories() throws IOException {
				return response;
			}
		};

		final Repositories processedRepos = this.sut.getRepositories();
		final Repositories retrievedRepos =
			new RepositoryReader().
				readReposistories(response);

		assertThat(processedRepos,notNullValue());
		assertThat(
			processedRepos.getRepositoryIds(),
			equalTo(retrievedRepos.getRepositoryIds()));
	}

	@Test
	public void testGetRepository$happyPath() throws IOException {
		final String source="1";
		new MockUp<RepositoryClient>() {
			@Mock
			public String getRepository(final String repoId) throws IOException {
				assertThat(repoId,equalTo(source));
				return loadResponse("repository.json");
			}
		};
		new MockUp<BranchClient>() {
			@Mock
			public String getBranches(final String repoId) throws IOException {
				assertThat(repoId,equalTo(source));
				return loadResponse("repository-branches.json");
			}
		};
		new MockUp<CommitClient>() {
			@Mock
			public String getCommits(final String repoId) throws IOException {
				assertThat(repoId,equalTo(source));
				return loadResponse("repository-commits.json");
			}
		};
		final Repository repository = this.sut.getRepository(source);
		assertThat(repository,notNullValue());
		assertThat(repository.getBranches(),notNullValue());
		assertThat(repository.getCommits(),notNullValue());
	}

	@Test
	public void testGetRepositoryContributors$happyPath() throws IOException {
		final String source="2";
		new MockUp<RepositoryClient>() {
			@Mock
			public String getRepository(final String repoId) throws IOException {
				assertThat(repoId,equalTo(source));
				return loadResponse("repository.json");
			}
		};
		final List<String> repository = this.sut.getRepositoryContributors(source);
		assertThat(repository,hasItems("6","4"));
	}

}
