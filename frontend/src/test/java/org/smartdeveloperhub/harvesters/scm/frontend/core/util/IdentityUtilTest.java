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
package org.smartdeveloperhub.harvesters.scm.frontend.core.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.Serializable;
import java.net.URI;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;

@RunWith(JMockit.class)
public class IdentityUtilTest {

	private static final URI ENHANCER_URI = URI.create("http://www.example.org:5000/api");

	@Mocked ResourceSnapshot resource;
	@Mocked Name<?> name;

	private void setUpMock(final Serializable id) {
		new Expectations() {{
			IdentityUtilTest.this.resource.name();this.result=IdentityUtilTest.this.name;
			IdentityUtilTest.this.name.id();this.result=id;
		}};
	}

	@Test
	public void verifyIsUtilityClass() {
		assertThat(Utils.isUtilityClass(IdentityUtil.class),equalTo(true));
	}

	@Test
	public void testEnhancerName() throws Exception {
		assertThat(IdentityUtil.enhancerName(ENHANCER_URI).id(),equalTo(ENHANCER_URI));
	}

	@Test
	public void testRepositoryName() throws Exception {
		assertThat(IdentityUtil.repositoryName(1).id(),equalTo(1));
	}

	@Test
	public void testRepositoryId() throws Exception {
		final Integer id = 1;
		setUpMock(id);
		assertThat(IdentityUtil.repositoryId(this.resource),equalTo(id));
	}

	@Test
	public void testUserContainerName() throws Exception {
		assertThat(IdentityUtil.userContainerName().id(),equalTo(UserContainerHandler.NAME));
	}

	@Test
	public void testUserName() throws Exception {
		assertThat(IdentityUtil.userName("userId").id(),equalTo("userId"));
	}

	@Test
	public void testUserId() throws Exception {
		final String id = "userId";
		setUpMock(id);
		assertThat(IdentityUtil.userId(this.resource),equalTo(id));
	}

	@Test
	public void testBranchName() throws Exception {
		final BranchKey key = new BranchKey(1, "branchId");
		assertThat(IdentityUtil.branchName(key).id(),equalTo(key));
	}

	@Test
	public void testBranchId() throws Exception {
		final BranchKey key = new BranchKey(1, "branchId");
		setUpMock(key);
		assertThat(IdentityUtil.branchId(this.resource),equalTo(key));
	}

	@Test
	public void testCommitName() throws Exception {
		final CommitKey key = new CommitKey(1, "commitId");
		assertThat(IdentityUtil.commitName(key).id(),equalTo(key));
	}

	@Test
	public void testCommitId() throws Exception {
		final CommitKey key = new CommitKey(1, "commitId");
		setUpMock(key);
		assertThat(IdentityUtil.commitId(this.resource),equalTo(key));
	}

}
