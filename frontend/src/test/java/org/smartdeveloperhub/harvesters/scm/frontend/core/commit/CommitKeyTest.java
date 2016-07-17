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
package org.smartdeveloperhub.harvesters.scm.frontend.core.commit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import org.junit.Test;


public class CommitKeyTest {

	private static final String COMMIT_ID = "commitId1";
	private static final String ALTERNATIVE_COMMIT_ID = "commitId2";

	private static final String REPO_ID = "1";
	private static final String ALTERNATIVE_REPO_ID = "2";

	@Test
	public void testKeepsRepositoryId() throws Exception {
		assertThat(defaultKey().getRepoId(),equalTo(REPO_ID));
	}

	@Test
	public void testKeepsCommitId() throws Exception {
		assertThat(defaultKey().getCommitId(),equalTo(COMMIT_ID));
	}

	@Test
	public void verifyHasCustomStringRepresentation() {
		assertThat(defaultKey().toString(),equalTo("CommitKey{repoId=1, commitId=commitId1}"));
	}

	@Test
	public void testEquals$differentType() {
		assertThat((Object)defaultKey(),not(equalTo((Object)"string")));
	}

	@Test
	public void testEquals$equalInstance() {
		final CommitKey one=defaultKey();
		final CommitKey other=defaultKey();
		assertThat(one,equalTo(other));
	}

	@Test
	public void testEquals$differentCommitId() {
		final CommitKey one=defaultKey();
		final CommitKey other=keyWithDifferentCommitIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testEquals$differentRepoId() {
		final CommitKey one=defaultKey();
		final CommitKey other=keyWithDifferentRepositoryIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testHashCode$equalInstance() {
		final CommitKey one=defaultKey();
		final CommitKey other=defaultKey();
		assertThat(one.hashCode(),equalTo(other.hashCode()));
	}

	@Test
	public void testHashCode$differentCommitId() {
		final CommitKey one=defaultKey();
		final CommitKey other=keyWithDifferentCommitIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$differentRepoId() {
		final CommitKey one=defaultKey();
		final CommitKey other=keyWithDifferentRepositoryIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$different() {
		final CommitKey one=defaultKey();
		final CommitKey other=alternativeKey();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testCompare$equal() {
		final CommitKey one=defaultKey();
		final CommitKey other=defaultKey();
		assertThat(one.compareTo(other),equalTo(0));
	}

	@Test
	public void testCompare$differentRepositoryId() {
		final CommitKey one=defaultKey();
		final CommitKey other=keyWithDifferentRepositoryIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	@Test
	public void testCompare$differentCommitId() {
		final CommitKey one=defaultKey();
		final CommitKey other=keyWithDifferentCommitIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	private CommitKey defaultKey() {
		return new CommitKey(REPO_ID,COMMIT_ID);
	}

	private CommitKey keyWithDifferentRepositoryIdentifier() {
		return new CommitKey(ALTERNATIVE_REPO_ID,COMMIT_ID);
	}

	private CommitKey keyWithDifferentCommitIdentifier() {
		return new CommitKey(REPO_ID,ALTERNATIVE_COMMIT_ID);
	}

	private CommitKey alternativeKey() {
		return new CommitKey(ALTERNATIVE_REPO_ID,ALTERNATIVE_COMMIT_ID);
	}
}
