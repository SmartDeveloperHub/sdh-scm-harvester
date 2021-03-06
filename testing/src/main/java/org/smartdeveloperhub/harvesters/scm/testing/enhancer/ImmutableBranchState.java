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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.3.0
 *   Bundle      : scm-harvester-testing-0.3.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing.enhancer;

import java.util.List;
import java.util.Set;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

final class ImmutableBranchState implements BranchState {

	private final ImmutableRepositoryState repository;
	private final String id;
	private final String name;
	private final Long createdAt;
	private final Set<String> commits;
	private final Set<String> contributors;

	private Long lastCommit;

	ImmutableBranchState(final ImmutableRepositoryState repository, final String id, final String name) {
		this.repository = repository;
		this.id = id;
		this.name=name;
		this.createdAt=System.currentTimeMillis();
		this.contributors=Sets.newLinkedHashSet();
		this.commits=Sets.newLinkedHashSet();
		ActivityTracker.currentTracker().created(this);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Entity getEntity() {
		return Entity.BRANCH;
	}

	@Override
	public String getRepositoryId() {
		return this.repository.getId();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void accept(final StateVisitor visitor) {
		visitor.visitBranch(this);
	}

	@Override
	public void addContribution(final CommitState commit, final CommitterState contributor) {
		this.lastCommit=System.currentTimeMillis();
		this.commits.add(commit.getId());
		if(this.contributors.add(contributor.getId())) {
			ActivityTracker.currentTracker().log("Committer %s (%s) is now a contributor of branch %s (%s) of repository %s (%s)",contributor.getId(),contributor.getName(),this.id,this.name,this.repository.getId(),this.repository.getName());
		}
		ActivityTracker.currentTracker().updated(this);
	}

	@Override
	public List<String> commits() {
		return Lists.newArrayList(this.commits);
	}

	@Override
	public Branch getRepresentation() {
		final Branch branch = new Branch();
		branch.setId(this.id);
		branch.setName(this.name);
		branch.setCreatedAt(this.createdAt);
		branch.setLastCommit(this.lastCommit);
		branch.setProtected(Boolean.toString(this.name.length()%2==0));
		branch.setContributors(Lists.newArrayList(this.contributors));
		return branch;
	}

}