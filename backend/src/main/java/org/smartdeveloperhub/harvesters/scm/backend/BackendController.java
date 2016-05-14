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
package org.smartdeveloperhub.harvesters.scm.backend;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.smartdeveloperhub.harvesters.scm.backend.controller.BranchController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.CommitController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.EnhancerController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.RepositoryController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.UserController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class BackendController {

	private final URI target;
	private final String scmRestService;

	public BackendController(final URI uri) {
		this.target = uri;
		this.scmRestService=uri.toString();
	}

	public URI getTarget() {
		return this.target;
	}

	public List<String> getRepositories() throws IOException {
		final RepositoryController repoCtl = new RepositoryController(this.scmRestService);
		return repoCtl.getRepositories().getRepositoryIds();
	}

	public List<String> getCommitters() throws IOException {
		final Set<String> uniqueUsers = Sets.newHashSet();
		final EnhancerController enhancerCtl = new EnhancerController(this.scmRestService);
		final Enhancer enhancer = enhancerCtl.getEnhancer();
		uniqueUsers.addAll(enhancer.getUsers());
		final RepositoryController repoCtl = new RepositoryController(this.scmRestService);
		for(final String repoId : enhancer.getRepositories()) {
			final List<String> contributors = repoCtl.getRepositoryContributors(repoId);
			for(final String contributorId : contributors) {
				uniqueUsers.add(contributorId);
			}
		}
		return ImmutableList.copyOf(uniqueUsers);
	}

	public Repository getRepository(final String id) throws IOException {
		final RepositoryController repoCtl = new RepositoryController(this.scmRestService);
		return repoCtl.getRepository(id);
	}

	public User getUser(final String id) throws IOException {
		final UserController userCtl = new UserController(this.scmRestService);
		return userCtl.getUser(id);
	}

	public Branch getBranch(final String repoId, final String branchId) throws IOException {
		final BranchController branchCtl = new BranchController(this.scmRestService);
		return branchCtl.getBranch(repoId, branchId);
	}

	public Commit getCommit(final String repoId, final String commitId) throws IOException {
		final CommitController commitCtl = new CommitController(this.scmRestService);
		return commitCtl.getCommit(repoId, commitId);
	}

}
