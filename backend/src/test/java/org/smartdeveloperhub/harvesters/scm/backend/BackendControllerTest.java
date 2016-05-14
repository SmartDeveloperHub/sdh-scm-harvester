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
package org.smartdeveloperhub.harvesters.scm.backend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;
import org.smartdeveloperhub.harvesters.scm.backend.controller.BranchController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.CommitController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.EnhancerController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.RepositoryController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.UserController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;

import com.google.common.collect.Lists;

public class BackendControllerTest {

	private static final String strURI="http://www.example.org/api";
	private static final URI    uri=URI.create(strURI);

	private BackendController sut;

	@Before
	public void setUp() {
		this.sut=new BackendController(uri);
	}

	@Test
	public void testGetTarget() throws Exception {
		assertThat(this.sut.getTarget(),sameInstance(uri));
	}

	@Test
	public void testGetRepositories() throws Exception {
		new MockUp<RepositoryController>() {
			@Mock
			void $init(final String location) {
				assertThat(location,equalTo(strURI));
			}
			@Mock
			Repositories getRepositories() {
				final Repositories result=new Repositories();
				result.setRepositoryIds(Lists.newArrayList("1","2","3"));
				return result;
			}
		};
		final List<String> repositories = this.sut.getRepositories();
		assertThat(repositories,hasSize(3));
		assertThat(repositories,hasItems("1","2","3"));
	}

	@Test
	public void testGetCommitters() throws Exception {
		new MockUp<EnhancerController>() {
			@Mock
			void $init(final String location) {
				assertThat(location,equalTo(strURI));
			}
			@Mock
			Enhancer getEnhancer() {
				final Enhancer result = new Enhancer();
				result.getRepositories().add("1");
				result.getUsers().add("user1");
				return result;
			}
		};
		new MockUp<RepositoryController>() {
			@Mock
			void $init(final String location) {
				assertThat(location,equalTo(strURI));
			}
			@Mock
			List<String> getRepositoryContributors(final String id) {
				return
					Lists.
						newArrayList("user1","contributor1","contributor2");
			}
		};
		final List<String> committers = this.sut.getCommitters();
		assertThat(committers,hasSize(3));
		assertThat(committers,hasItems("user1","contributor1","contributor2"));
	}

	@Test
	public void testGetRepository(@Mocked final Repository repository) throws Exception {
		new MockUp<RepositoryController>() {
			@Mock
			void $init(final String location) {
				assertThat(location,equalTo(strURI));
			}
			@Mock
			Repository getRepository(final String id) {
				assertThat(id,equalTo("1"));
				return repository;
			}
		};
		assertThat(this.sut.getRepository("1"),sameInstance(repository));
	}

	@Test
	public void testGetBranch(@Mocked final Branch expectation) throws Exception {
		final String expectedRepoId = "1";
		final String expectedBranchId = "id";
		new MockUp<BranchController>() {
			@Mock
			void $init(final String location) {
				assertThat(location,equalTo(strURI));
			}
			@Mock
			Branch getBranch(final String repoId,final String branchId) {
				assertThat(repoId,equalTo(expectedRepoId));
				assertThat(branchId,equalTo(expectedBranchId));
				return expectation;
			}
		};
		assertThat(this.sut.getBranch(expectedRepoId,expectedBranchId),sameInstance(expectation));
	}

	@Test
	public void testGetCommit(@Mocked final Commit expectation) throws Exception {
		final String expectedRepoId = "1";
		final String expectedCommitId = "id";
		new MockUp<CommitController>() {
			@Mock
			void $init(final String location) {
				assertThat(location,equalTo(strURI));
			}
			@Mock
			Commit getCommit(final String repoId,final String branchId) {
				assertThat(repoId,equalTo(expectedRepoId));
				assertThat(branchId,equalTo(expectedCommitId));
				return expectation;
			}
		};
		assertThat(this.sut.getCommit(expectedRepoId,expectedCommitId),sameInstance(expectation));
	}

	@Test
	public void testGetUser(@Mocked final User expectation) throws Exception {
		final String expectedUserId = "id";
		new MockUp<UserController>() {
			@Mock
			void $init(final String location) {
				assertThat(location,equalTo(strURI));
			}
			@Mock
			User getUser(final String userId) {
				assertThat(userId,equalTo(expectedUserId));
				return expectation;
			}
		};
		assertThat(this.sut.getUser(expectedUserId),sameInstance(expectation));
	}

}
