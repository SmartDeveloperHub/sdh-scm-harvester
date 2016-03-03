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
package org.smartdeveloperhub.harvesters.scm.frontend.core.branch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import org.junit.Test;


public class BranchKeyTest {

	private static final String BRANCH_ID = "branchId1";
	private static final String ALTERNATIVE_COMMIT_ID = "branchId2";

	private static final int REPO_ID = 1;
	private static final int ALTERNATIVE_REPO_ID = 2;

	@Test
	public void testKeepsRepositoryId() throws Exception {
		assertThat(defaultKey().getRepoId(),equalTo(REPO_ID));
	}

	@Test
	public void testKeepsBranchId() throws Exception {
		assertThat(defaultKey().getBranchId(),equalTo(BRANCH_ID));
	}

	@Test
	public void verifyHasCustomStringRepresentation() {
		assertThat(defaultKey().toString(),equalTo("BranchKey{repoId=1, branchId=branchId1}"));
	}

	@Test
	public void testEquals$differentType() {
		assertThat((Object)defaultKey(),not(equalTo((Object)"string")));
	}

	@Test
	public void testEquals$equalInstance() {
		final BranchKey one=defaultKey();
		final BranchKey other=defaultKey();
		assertThat(one,equalTo(other));
	}

	@Test
	public void testEquals$differentBranchId() {
		final BranchKey one=defaultKey();
		final BranchKey other=keyWithDifferentBranchIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testEquals$differentRepoId() {
		final BranchKey one=defaultKey();
		final BranchKey other=keyWithDifferentRepositoryIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testHashCode$equalInstance() {
		final BranchKey one=defaultKey();
		final BranchKey other=defaultKey();
		assertThat(one.hashCode(),equalTo(other.hashCode()));
	}

	@Test
	public void testHashCode$differentBranchId() {
		final BranchKey one=defaultKey();
		final BranchKey other=keyWithDifferentBranchIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$differentRepoId() {
		final BranchKey one=defaultKey();
		final BranchKey other=keyWithDifferentRepositoryIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$different() {
		final BranchKey one=defaultKey();
		final BranchKey other=alternativeKey();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testCompare$equal() {
		final BranchKey one=defaultKey();
		final BranchKey other=defaultKey();
		assertThat(one.compareTo(other),equalTo(0));
	}

	@Test
	public void testCompare$differentRepositoryId() {
		final BranchKey one=defaultKey();
		final BranchKey other=keyWithDifferentRepositoryIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	@Test
	public void testCompare$differentBranchId() {
		final BranchKey one=defaultKey();
		final BranchKey other=keyWithDifferentBranchIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	private BranchKey defaultKey() {
		return new BranchKey(REPO_ID,BRANCH_ID);
	}

	private BranchKey keyWithDifferentRepositoryIdentifier() {
		return new BranchKey(ALTERNATIVE_REPO_ID,BRANCH_ID);
	}

	private BranchKey keyWithDifferentBranchIdentifier() {
		return new BranchKey(REPO_ID,ALTERNATIVE_COMMIT_ID);
	}

	private BranchKey alternativeKey() {
		return new BranchKey(ALTERNATIVE_REPO_ID,ALTERNATIVE_COMMIT_ID);
	}
}
