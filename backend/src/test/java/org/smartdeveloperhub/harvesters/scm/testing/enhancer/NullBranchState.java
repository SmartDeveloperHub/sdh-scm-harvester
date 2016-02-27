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
package org.smartdeveloperhub.harvesters.scm.testing.enhancer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;

final class NullBranchState implements BranchState {

	private static final Logger LOGGER=LoggerFactory.getLogger(State.class);

	private final Integer repositoryId;
	private final String id;

	NullBranchState(final Integer repositoryId,final String id) {
		this.repositoryId = repositoryId;
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Integer getRepositoryId() {
		return this.repositoryId;
	}

	@Override
	public String getName() {
		return "<unknown>";
	}

	@Override
	public Entity getEntity() {
		return Entity.BRANCH;
	}

	@Override
	public void accept(final StateVisitor visitor) {
		visitor.visitBranch(this);
	}

	@Override
	public List<String> commits() {
		LOGGER.debug("Unknown branch <{}>{{}}: cannot return commits",this.repositoryId,this.id);
		return null;
	}

	@Override
	public Branch getRepresentation() {
		LOGGER.debug("Unknown branch <{}>{{}}: cannot return representation",this.repositoryId,this.id);
		return null;
	}

	@Override
	public void addContribution(final CommitState commit, final CommitterState contributor) {
		LOGGER.debug("Unknown branch <{}>{{}}: cannot add commit {} from committer {}",this.repositoryId,this.id,commit.getId(),contributor.getId());
	}
}