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
package org.smartdeveloperhub.harvesters.scm.backend.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.rest.BranchClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.CommitClient;

@RunWith(JMockit.class)
public class BranchControllerTest extends ControllerTestHelper {

	@Test
	public void testGetBranch$happyPath() throws IOException {
		final String sourceRepoId="repoId";
		final String sourceBranchId="branchId";
		new MockUp<BranchClient>() {
			@Mock
			public String getBranch(final String repoId, final String branchId) throws IOException {
				assertThat(repoId,equalTo(sourceRepoId));
				assertThat(branchId,equalTo(sourceBranchId));
				return loadResponse("branch.json");
			}
		};
		new MockUp<CommitClient>() {
			@Mock
			public String getCommits(final String repoId, final String branchId) throws IOException {
				assertThat(repoId,equalTo(sourceRepoId));
				assertThat(branchId,equalTo(sourceBranchId));
				return loadResponse("branch-commits.json");
			}
		};
		final BranchController sut = new BranchController("baseURL");
		final Branch branch = sut.getBranch(sourceRepoId, sourceBranchId);
		assertThat(branch,notNullValue());
		assertThat(branch.getCommits(),notNullValue());
	}

}
