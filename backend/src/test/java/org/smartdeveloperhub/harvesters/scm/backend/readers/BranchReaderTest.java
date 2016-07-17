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
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Fixture;


public class BranchReaderTest extends ReaderTestHelper {

	private BranchReader sut;

	@Before
	public void setUp() {
		this.sut= new BranchReader();
	}

	@Test
	public void testReadBranchCommits() throws Exception {
		final Branch defaultBranch=Fixture.defaultBranch();
		final Commits defaultCommits = Fixture.defaultCommits();

		final Branch readBranch=
			this.sut.
				readBranch(
					serialize(defaultBranch),
					serializeList(defaultCommits.getCommitIds()));

		final Commits readCommits=readBranch.getCommits();

		verifyBranch(defaultBranch,readBranch);
		verifyCommits(defaultCommits,readCommits);
	}

/*
	@Test
	public void testReadBranch$emptyCommits() throws Exception {
		final Branch defaultBranch=defaultBranch();

		final Branch readBranch=
			this.sut.
				readBranch(
					serialize(defaultBranch),
					"");

		verifyBranch(defaultBranch,readBranch);
		assertThat(readBranch.getCommits(),nullValue());
	}

	@Test
	public void testReadBranch$nullCommits() throws Exception {
		final Branch defaultBranch=defaultBranch();

		final Branch readBranch=
			this.sut.
				readBranch(
					serialize(defaultBranch),
					null);

		verifyBranch(defaultBranch,readBranch);
		assertThat(readBranch.getCommits(),nullValue());
	}
*/

	private void verifyBranch(final Branch defaultBranch, final Branch readBranch) {
		assertThat(readBranch.getAdditionalProperties(),equalTo(defaultBranch.getAdditionalProperties()));
		assertThat(readBranch.getContributors(),equalTo(defaultBranch.getContributors()));
		assertThat(readBranch.getCreatedAt(),equalTo(defaultBranch.getCreatedAt()));
		assertThat(readBranch.getLastCommit(),equalTo(defaultBranch.getLastCommit()));
		assertThat(readBranch.getName(),equalTo(defaultBranch.getName()));
		assertThat(readBranch.getProtected(),equalTo(defaultBranch.getProtected()));
	}

}
