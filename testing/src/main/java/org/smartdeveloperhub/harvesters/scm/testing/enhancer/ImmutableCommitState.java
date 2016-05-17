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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-testing-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing.enhancer;

import java.util.Random;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;

final class ImmutableCommitState implements CommitState  {

	private final ImmutableRepositoryState repository;
	private final CommitterState contributor;

	private final String id;
	private final Long authoredDate;
	private final Long committedDate;
	private final Long createdAt;
	private final Integer linesAdded;
	private final Integer linesRemoved;
	private final String message;
	private final String shortId;
	private final String title;

	ImmutableCommitState(final ImmutableRepositoryState repository, final String id, final CommitterState contributor) {
		this.repository = repository;
		this.id=id;
		this.contributor = contributor;
		this.createdAt=System.currentTimeMillis();
		final Random random = new Random();
		this.authoredDate=System.currentTimeMillis()-(random.nextInt(10000000)+1000000);
		this.committedDate=this.authoredDate+(random.nextInt(10000)+10000);
		this.linesAdded=random.nextInt(1000);
		this.linesRemoved=random.nextInt(1000)+(this.linesAdded==0?10:0);
		this.shortId=id.length()>10?id.substring(0,5):id;
		this.message=StateUtil.generateSentences(2,5);
		this.title=StateUtil.generateSentence();
		ActivityTracker.currentTracker().created(this);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getRepositoryId() {
		return this.repository.getId();
	}

	@Override
	public State.Entity getEntity() {
		return State.Entity.COMMIT;
	}

	@Override
	public void accept(final StateVisitor visitor) {
		visitor.visitCommit(this);
	}

	@Override
	public Commit getRepresentation() {
		final Commit commit = new Commit();
		commit.setAuthor(this.contributor.getId());
		commit.setAuthoredDate(this.authoredDate);
		commit.setCommittedDate(this.committedDate);
		commit.setCreatedAt(this.createdAt);
		commit.setId(this.id);
		commit.setLinesAdded(this.linesAdded);
		commit.setLinesRemoved(this.linesRemoved);
		commit.setMessage(this.message);
		commit.setShortId(this.shortId);
		commit.setTitle(this.title);
		return commit;
	}

}