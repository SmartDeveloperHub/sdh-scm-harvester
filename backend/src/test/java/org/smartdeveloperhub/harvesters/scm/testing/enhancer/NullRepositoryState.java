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
package org.smartdeveloperhub.harvesters.scm.testing.enhancer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;

final class NullRepositoryState implements RepositoryState {

	private static final Logger LOGGER=LoggerFactory.getLogger(State.class);

	private final Integer id;

	NullRepositoryState(final Integer repositoryId) {
		this.id = repositoryId;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return "<unknown>";
	}

	@Override
	public Repository toEntity() {
		LOGGER.debug("Unknown repository {}: cannot return representation",this.id);
		return null;
	}

	@Override
	public boolean deleteCommit(final String commitId) {
		LOGGER.debug("Unknown repository {}: cannot delete commit {}",this.id,commitId);
		return false;
	}

	@Override
	public boolean deleteBranch(final String name) {
		LOGGER.debug("Unknown repository {}: cannot delete branch {}",this.id,name);
		return false;
	}

	@Override
	public boolean createCommit(final String commitId, final String contributor) {
		LOGGER.debug("Unknown repository {}: cannot create commit {} by committer {}",this.id,commitId,contributor);
		return false;
	}

	@Override
	public boolean createBranch(final String name) {
		LOGGER.debug("Unknown repository {}: cannot create branch {}",this.id,name);
		return false;
	}

	@Override
	public List<String> commits() {
		return null;
	}

	@Override
	public List<String> branches() {
		return null;
	}

	@Override
	public CommitState commit(final String commitId) {
		return new NullCommitState(this.id, commitId);
	}

	@Override
	public BranchState branch(final String name) {
		return new NullBranchState(this.id, name);
	}
}