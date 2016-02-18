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
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branches;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Fixture;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;


public class RepositoryReaderTest extends ReaderTestHelper {

	private RepositoryReader sut;

	@Before
	public void setUp() {
		this.sut= new RepositoryReader();
	}

	@Test
	public void testReadRepositories() throws Exception {
		final Repositories defaultRepos = Fixture.defaultRepositories();

		final Repositories readRepos=
			this.sut.
				readReposistories(
					serializeList(defaultRepos.getRepositoryIds()));

		assertThat(readRepos.getRepositoryIds(),equalTo(defaultRepos.getRepositoryIds()));
	}

	@Test
	public void testReadRepository$repoBranchesAndCommits() throws Exception {
		final Repository defaultRepo = Fixture.defaultRepository();
		final Commits defaultCommits = Fixture.defaultCommits();
		final Branches defaultBranches = Fixture.defaultBranches();

		final Repository readRepo =
			this.sut.
				readRepository(
					serialize(defaultRepo),
					serializeList(defaultBranches.getBranchIds()),
					serializeList(defaultCommits.getCommitIds()));

		final Commits readCommits = readRepo.getCommits();
		final Branches readBranches = readRepo.getBranches();

		verifyRepository(defaultRepo, readRepo);
		verifyCommits(defaultCommits, readCommits);
		verifyBranches(defaultBranches, readBranches);
	}

	@Test
	public void testReadRepository$repoBranchesAndEmptyCommits() throws Exception {
		final Repository defaultRepo = Fixture.defaultRepository();
		final Branches defaultBranches = Fixture.defaultBranches();

		final Repository readRepo =
			this.sut.
				readRepository(
					serialize(defaultRepo),
					serializeList(defaultBranches.getBranchIds()),
					"");

		final Commits readCommits = readRepo.getCommits();
		final Branches readBranches = readRepo.getBranches();

		verifyRepository(defaultRepo, readRepo);
		assertThat(readCommits,nullValue());
		verifyBranches(defaultBranches, readBranches);
	}

	@Test
	public void testReadRepository$repoBranchesAndNullCommits() throws Exception {
		final Repository defaultRepo = Fixture.defaultRepository();
		final Branches defaultBranches = Fixture.defaultBranches();

		final Repository readRepo =
			this.sut.
				readRepository(
					serialize(defaultRepo),
					serializeList(defaultBranches.getBranchIds()),
					null);

		final Commits readCommits = readRepo.getCommits();
		final Branches readBranches = readRepo.getBranches();

		verifyRepository(defaultRepo, readRepo);
		assertThat(readCommits,nullValue());
		verifyBranches(defaultBranches, readBranches);
	}

	@Test
	public void testReadRepository$repoCommitsAndEmptyBranches() throws Exception {
		final Repository defaultRepo = Fixture.defaultRepository();
		final Commits defaultCommits = Fixture.defaultCommits();

		final Repository readRepo =
			this.sut.
				readRepository(
					serialize(defaultRepo),
					"",
					serializeList(defaultCommits.getCommitIds()));

		final Commits readCommits = readRepo.getCommits();
		final Branches readBranches = readRepo.getBranches();

		verifyRepository(defaultRepo, readRepo);
		verifyCommits(defaultCommits, readCommits);
		assertThat(readBranches,nullValue());
	}

	@Test
	public void testReadRepository$repoCommitsAndNullBranches() throws Exception {
		final Repository defaultRepo = Fixture.defaultRepository();
		final Commits defaultCommits = Fixture.defaultCommits();

		final Repository readRepo =
			this.sut.
				readRepository(
					serialize(defaultRepo),
					null,
					serializeList(defaultCommits.getCommitIds()));

		final Commits readCommits = readRepo.getCommits();
		final Branches readBranches = readRepo.getBranches();

		verifyRepository(defaultRepo, readRepo);
		verifyCommits(defaultCommits, readCommits);
		assertThat(readBranches,nullValue());
	}

	private void verifyBranches(final Branches defaultBranches, final Branches readBranches) {
		assertThat(readBranches.getBranchIds(),equalTo(defaultBranches.getBranchIds()));
	}

	private void verifyRepository(final Repository defaultRepo, final Repository readRepo) {
		assertThat(readRepo.getState(),equalTo(defaultRepo.getState()));
		assertThat(readRepo.getAvatarUrl(),equalTo(defaultRepo.getAvatarUrl()));
		assertThat(readRepo.getContributors(),equalTo(defaultRepo.getContributors()));
		assertThat(readRepo.getCreatedAt(),equalTo(defaultRepo.getCreatedAt()));
		assertThat(readRepo.getDefaultBranch(),equalTo(defaultRepo.getDefaultBranch()));
		assertThat(readRepo.getDescription(),equalTo(defaultRepo.getDescription()));
		assertThat(readRepo.getFirstCommitAt(),equalTo(defaultRepo.getFirstCommitAt()));
		assertThat(readRepo.getHttpUrlToRepo(),equalTo(defaultRepo.getHttpUrlToRepo()));
		assertThat(readRepo.getId(),equalTo(defaultRepo.getId()));
		assertThat(readRepo.getLastActivityAt(),equalTo(defaultRepo.getLastActivityAt()));
		assertThat(readRepo.getLastCommitAt(),equalTo(defaultRepo.getLastCommitAt()));
		assertThat(readRepo.getName(),equalTo(defaultRepo.getName()));
		assertThat(readRepo.getOwner().getId(),equalTo(defaultRepo.getOwner().getId()));
		assertThat(readRepo.getOwner().getType(),equalTo(defaultRepo.getOwner().getType()));
		assertThat(readRepo.getOwner().getAdditionalProperties(),equalTo(defaultRepo.getOwner().getAdditionalProperties()));
		assertThat(readRepo.getPublic(),equalTo(defaultRepo.getPublic()));
		assertThat(readRepo.getTags(),equalTo(defaultRepo.getTags()));
		assertThat(readRepo.getWebUrl(),equalTo(defaultRepo.getWebUrl()));
		assertThat(readRepo.getAdditionalProperties(),equalTo(defaultRepo.getAdditionalProperties()));
	}

}
