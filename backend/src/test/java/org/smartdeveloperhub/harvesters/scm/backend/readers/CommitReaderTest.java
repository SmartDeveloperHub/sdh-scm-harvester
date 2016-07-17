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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0
 *   Bundle      : scm-harvester-backend-0.3.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Fixture;


public class CommitReaderTest extends ReaderTestHelper {

	private CommitReader sut;

	@Before
	public void setUp() {
		this.sut= new CommitReader();
	}

	@Test
	public void testReadCommit() throws Exception {
		final Commit defaultCommit=Fixture.defaultCommit();

		final Commit readCommit=
			this.sut.
				readCommit(
					serialize(defaultCommit));
		verifyCommit(defaultCommit, readCommit);
	}


	private void verifyCommit(final Commit defaultCommit, final Commit readCommit) {
		assertThat(readCommit.getAdditionalProperties(),equalTo(defaultCommit.getAdditionalProperties()));
		assertThat(readCommit.getAuthor(),equalTo(defaultCommit.getAuthor()));
		assertThat(readCommit.getAuthoredDate(),equalTo(defaultCommit.getAuthoredDate()));
		assertThat(readCommit.getCommittedDate(),equalTo(defaultCommit.getCommittedDate()));
		assertThat(readCommit.getCreatedAt(),equalTo(defaultCommit.getCreatedAt()));
		assertThat(readCommit.getId(),equalTo(defaultCommit.getId()));
		assertThat(readCommit.getLinesAdded(),equalTo(defaultCommit.getLinesAdded()));
		assertThat(readCommit.getLinesRemoved(),equalTo(defaultCommit.getLinesRemoved()));
		assertThat(readCommit.getMessage(),equalTo(defaultCommit.getMessage()));
		assertThat(readCommit.getParentIds(),equalTo(defaultCommit.getParentIds()));
		assertThat(readCommit.getShortId(),equalTo(defaultCommit.getShortId()));
		assertThat(readCommit.getTitle(),equalTo(defaultCommit.getTitle()));
	}

}
