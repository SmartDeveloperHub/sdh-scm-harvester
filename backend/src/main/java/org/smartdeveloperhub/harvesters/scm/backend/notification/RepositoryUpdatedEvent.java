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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Event.INSTANCE,
	Event.TIMESTAMP,
	RepositoryUpdatedEvent.REPOSITORY,
	RepositoryUpdatedEvent.NEW_BRANCHES,
	RepositoryUpdatedEvent.DELETED_BRANCHES,
	RepositoryUpdatedEvent.NEW_COMMITS,
	RepositoryUpdatedEvent.DELETED_COMMITS,
	RepositoryUpdatedEvent.CONTRIBUTORS
})
public class RepositoryUpdatedEvent extends Event {

	static final String REPOSITORY       = "repository";
	static final String NEW_BRANCHES     = "newBranches";
	static final String NEW_COMMITS      = "newCommits";
	static final String DELETED_BRANCHES = "deletedBranches";
	static final String DELETED_COMMITS  = "deletedCommits";
	static final String CONTRIBUTORS     = "contributors";

	@JsonProperty(REPOSITORY)
	private Integer repository;

	@JsonProperty(NEW_BRANCHES)
	private List<String> newBranches = new ArrayList<>();

	@JsonProperty(DELETED_BRANCHES)
	private List<String> deletedBranches = new ArrayList<>();

	@JsonProperty(NEW_COMMITS)
	private List<String> newCommits = new ArrayList<>();

	@JsonProperty(DELETED_COMMITS)
	private List<String> deletedCommits = new ArrayList<>();

	@JsonProperty(CONTRIBUTORS)
	private List<String> contributors = new ArrayList<>();

	/**
	 * Get the identifier of the updated repository
	 *
	 * @return The repository identifier
	 */
	@JsonProperty(REPOSITORY)
	public Integer getRepository() {
		return this.repository;
	}

	/**
	 * Set the identifier of the updated repository
	 *
	 * @param repository
	 *            The identifier of the repository
	 */
	@JsonProperty(REPOSITORY)
	public void setRepository(final Integer repository) {
		this.repository = repository;
	}

	/**
	 * Get the identifiers of the new branches added to the repository
	 *
	 * @return The identifiers of the new branches
	 */
	@JsonProperty(NEW_BRANCHES)
	public List<String> getNewBranches() {
		return this.newBranches;
	}

	/**
	 * Set the identifiers of the new branches added to the repository
	 *
	 * @param branchIds
	 *            The identifiers of the branches
	 */
	@JsonProperty(NEW_BRANCHES)
	public void setNewBranches(final List<String> branchIds) {
		this.newBranches = branchIds;
	}

	/**
	 * Get the identifiers of the branches deleted from the repository
	 *
	 * @return The identifiers of the new branches
	 */
	@JsonProperty(DELETED_BRANCHES)
	public List<String> getDeletedBranches() {
		return this.deletedBranches;
	}

	/**
	 * Set the identifiers of the branches deleted from the repository
	 *
	 * @param branchIds
	 *            The identifiers of the branches
	 */
	@JsonProperty(DELETED_BRANCHES)
	public void setDeletedBranches(final List<String> branchIds) {
		this.deletedBranches = branchIds;
	}

	/**
	 * Get the identifiers of the new commits added to the repository
	 *
	 * @return The identifiers of the new commits
	 */
	@JsonProperty(NEW_COMMITS)
	public List<String> getNewCommits() {
		return this.newCommits;
	}

	/**
	 * Set the identifiers of the new commits added to the repository
	 *
	 * @param commitIds
	 *            The identifiers of the commits
	 */
	@JsonProperty(NEW_COMMITS)
	public void setNewCommits(final List<String> commitIds) {
		this.newCommits = commitIds;
	}

	/**
	 * Get the identifiers of the commits deleted from the repository
	 *
	 * @return The identifiers of the new commits
	 */
	@JsonProperty(DELETED_COMMITS)
	public List<String> getDeletedCommits() {
		return this.deletedCommits;
	}

	/**
	 * Set the identifiers of the commits deleted from the repository
	 *
	 * @param commitIds
	 *            The identifiers of the commits
	 */
	@JsonProperty(DELETED_COMMITS)
	public void setDeletedCommits(final List<String> commitIds) {
		this.deletedCommits= commitIds;
	}

	/**
	 * Get the identifiers of the committers that contributed to the repository
	 * update
	 *
	 * @return The identifiers of the contributors
	 */
	@JsonProperty(CONTRIBUTORS)
	public List<String> getContributors() {
		return this.contributors;
	}

	/**
	 * Set the identifiers of the committers that contributed to the repository
	 * update
	 *
	 * @param commiterIds
	 *            The identifiers of the contributors
	 */
	@JsonProperty(CONTRIBUTORS)
	public void setContributors(final List<String> commiterIds) {
		this.contributors = commiterIds;
	}

}
