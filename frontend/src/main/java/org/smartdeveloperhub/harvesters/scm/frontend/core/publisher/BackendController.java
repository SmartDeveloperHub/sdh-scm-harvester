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
package org.smartdeveloperhub.harvesters.scm.frontend.core.publisher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.controller.BranchController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.CommitController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.RepositoryController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.UserController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;
import org.smartdeveloperhub.harvesters.scm.frontend.core.GitLabHarvester;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityMap;

public class BackendController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BackendController.class);

	private final String scmRestService;

	private IdentityMap<BranchKey> branchIdentityMap;
	private IdentityMap<CommitKey> commitIdentityMap;

	private GitLabHarvester gitLabHarvester;

	public BackendController() {
		final String gitLabEnhancer = System.getenv("GITLAB_ENHANCER");
		if(gitLabEnhancer==null) {
			this.scmRestService = "http://192.168.0.10:5000/api";
			LOGGER.info("GITLAB_ENHANCER by default {}", this.scmRestService);
		} else {
			LOGGER.info("GITLAB_ENHANCER environment variable {}",gitLabEnhancer);
			this.scmRestService = gitLabEnhancer;
		}
		this.branchIdentityMap = new IdentityMap<BranchKey>();
		this.commitIdentityMap = new IdentityMap<CommitKey>();
	}

	public IdentityMap<BranchKey> getBranchIdentityMap() {
		return this.branchIdentityMap;
	}

	public void setBranchIdentityMap(final IdentityMap<BranchKey> branchIdentityMap) {
		this.branchIdentityMap = branchIdentityMap;
	}

	public IdentityMap<CommitKey> getCommitIdentityMap() {
		return this.commitIdentityMap;
	}

	public void setCommitIdentityMap(final IdentityMap<CommitKey> commitIdentityMap) {
		this.commitIdentityMap = commitIdentityMap;
	}

	public GitLabHarvester createGitLabHarvester(final String id) throws Exception {
		final RepositoryController repoCtl = new RepositoryController(this.scmRestService);
		final Repositories repos = repoCtl.getRepositories();

		this.gitLabHarvester = new GitLabHarvester();
		this.gitLabHarvester.setId(id);
		for(final Integer repoId : repos.getRepositoryIds()) {
			this.gitLabHarvester.addRepository(repoId);
		}

		return this.gitLabHarvester;
	}

	public GitLabHarvester getGitLabHarvester() throws Exception {
		return this.gitLabHarvester;
	}

	public List<String> getUsers() throws Exception {
		final Set<String> uniqueUsers = new HashSet<String>();
		final RepositoryController repoCtl = new RepositoryController(this.scmRestService);
		final Repositories repos = repoCtl.getRepositories();

		for(final Integer repoId : repos.getRepositoryIds()) {
			final Repository repo = repoCtl.getRepositoryWithoutBranchCommit(repoId.toString());
			final List<String> contributors = repo.getContributors();
			for(final String contributorId : contributors) {
				uniqueUsers.add(contributorId);
			}
		}

		return new ArrayList<String>(uniqueUsers);
	}

	public Repository getRepository(final String id) throws Exception {
		final RepositoryController repoCtl = new RepositoryController(this.scmRestService);
		return repoCtl.getRepository(id);
	}

	public User getUser(final String id) throws Exception {
		final UserController userCtl = new UserController(this.scmRestService);
		return userCtl.getUser(id);
	}

	public Branch getBranch(final String repoId, final String branchId) throws Exception {
		final BranchController branchCtl = new BranchController(this.scmRestService);
		return branchCtl.getBranch(repoId, branchId);
	}

	public Commit getCommit(final String repoId, final String commitId) throws Exception {
		final CommitController commitCtl = new CommitController(this.scmRestService);
		return commitCtl.getCommit(repoId, commitId);
	}

	public static void main(final String[] args) throws Exception {
		final BackendController bkend = new BackendController();
		final Repository repo = bkend.getRepository("5");
		LOGGER.info("{}",repo);
		LOGGER.info("* {}",repo.getBranches());
		LOGGER.info("** {} ",repo.getCommits());
	}

}
