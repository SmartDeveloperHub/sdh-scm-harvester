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
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Fixture;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;


public class UserReaderTest extends ReaderTestHelper {

	private UserReader sut;

	@Before
	public void setUp() {
		this.sut= new UserReader();
	}

	@Test
	public void testReadCommit() throws Exception {
		final User defaultUser=Fixture.defaultUser();

		final User readUser=
			this.sut.
				readUser(
					serialize(defaultUser));

		verifyUser(defaultUser, readUser);
	}


	private void verifyUser(final User defaultUser, final User readUser) {
		assertThat(readUser.getAdditionalProperties(),equalTo(defaultUser.getAdditionalProperties()));
		assertThat(readUser.getAvatarUrl(),equalTo(defaultUser.getAvatarUrl()));
		assertThat(readUser.getCreatedAt(),equalTo(defaultUser.getCreatedAt()));
		assertThat(readUser.getEmails(),equalTo(defaultUser.getEmails()));
		assertThat(readUser.isExternal(),equalTo(defaultUser.isExternal()));
		assertThat(readUser.getFirstCommitAt(),equalTo(defaultUser.getFirstCommitAt()));
		assertThat(readUser.getId(),equalTo(defaultUser.getId()));
		assertThat(readUser.getLastCommitAt(),equalTo(defaultUser.getLastCommitAt()));
		assertThat(readUser.getName(),equalTo(defaultUser.getName()));
		assertThat(readUser.getState(),equalTo(defaultUser.getState()));
		assertThat(readUser.getUsername(),equalTo(defaultUser.getUsername()));
	}

}
