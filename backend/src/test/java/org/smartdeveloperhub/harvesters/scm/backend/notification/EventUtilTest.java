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
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;


public class EventUtilTest {

	@Test
	public void verifyIsUtilityClass() {
		assertThat(Utils.isUtilityClass(EventUtil.class),equalTo(true));
	}

	@Test
	public void testReadCommitterCreatedEvent() throws IOException {
		final CommitterCreatedEvent originalEvent = new CommitterCreatedEvent();
		originalEvent.setNewCommitters(Arrays.asList("23","24"));
		fillInBasicEvent(originalEvent);

		final CommitterCreatedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					CommitterCreatedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getNewCommitters(),equalTo(originalEvent.getNewCommitters()));
	}

	@Test
	public void testReadCommitterDeletedEvent() throws IOException {
		final CommitterDeletedEvent originalEvent = new CommitterDeletedEvent();
		originalEvent.setDeletedCommitters(Arrays.asList("3","4"));
		fillInBasicEvent(originalEvent);

		final CommitterDeletedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					CommitterDeletedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getDeletedCommitters(),equalTo(originalEvent.getDeletedCommitters()));
	}

	@Test
	public void testReadRepositoryCreatedEvent() throws IOException {
		final RepositoryCreatedEvent originalEvent = new RepositoryCreatedEvent();
		originalEvent.setNewRepositories(Arrays.asList(1,2,3));
		fillInBasicEvent(originalEvent);

		final RepositoryCreatedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					RepositoryCreatedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getNewRepositories(),equalTo(originalEvent.getNewRepositories()));
	}

	@Test
	public void testReadUpdatedCreatedEvent() throws IOException {
		final RepositoryUpdatedEvent originalEvent = new RepositoryUpdatedEvent();
		originalEvent.setRepository(19);
		originalEvent.setNewBranches(Arrays.asList("release/0.1.0"));
		originalEvent.setDeletedBranches(Arrays.asList("feature/new-issue","feature/enhancement"));
		originalEvent.setNewCommits(Arrays.asList("2ae1d88a46004f5c3c6aa3ddba2cf719452ea005", "afcea4f8ba9cddba9d7a1370d63f0a78601f3973"));
		originalEvent.setDeletedCommits(Arrays.asList("98f22db350ed82f43ddf2645375d0fabd096aaee", "3fc4bba13432d1f1bebc13ebe54ccafd9fde1224"));
		originalEvent.setContributors(Arrays.asList("12", "22"));
		fillInBasicEvent(originalEvent);

		final RepositoryUpdatedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					RepositoryUpdatedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getRepository(),equalTo(originalEvent.getRepository()));
		assertThat(readEvent.getNewBranches(),equalTo(originalEvent.getNewBranches()));
		assertThat(readEvent.getDeletedBranches(),equalTo(originalEvent.getDeletedBranches()));
		assertThat(readEvent.getNewCommits(),equalTo(originalEvent.getNewCommits()));
		assertThat(readEvent.getDeletedCommits(),equalTo(originalEvent.getDeletedCommits()));
		assertThat(readEvent.getContributors(),equalTo(originalEvent.getContributors()));
	}

	@Test
	public void testReadRepositoryDeletedEvent() throws IOException {
		final RepositoryDeletedEvent originalEvent = new RepositoryDeletedEvent();
		originalEvent.setDeletedRepositories(Arrays.asList(1,2,3));
		fillInBasicEvent(originalEvent);

		final RepositoryDeletedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					RepositoryDeletedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getDeletedRepositories(),equalTo(originalEvent.getDeletedRepositories()));
	}

	private void fillInBasicEvent(final Event event) {
		event.setInstance("http://russell.dia.fi.upm.es:5000/api");
		event.setTimestamp(System.currentTimeMillis());
	}

	private void verifyBasicEvent(final Event expectedEvent, final Event actualEvent) {
		assertThat(actualEvent.getInstance(),equalTo(expectedEvent.getInstance()));
		assertThat(actualEvent.getTimestamp(),equalTo(expectedEvent.getTimestamp()));
	}

}