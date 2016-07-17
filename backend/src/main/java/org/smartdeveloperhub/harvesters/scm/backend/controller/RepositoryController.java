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
package org.smartdeveloperhub.harvesters.scm.backend.controller;


import java.io.IOException;
import java.util.List;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.readers.RepositoryReader;
import org.smartdeveloperhub.harvesters.scm.backend.rest.BranchClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.CommitClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.RepositoryClient;

public class RepositoryController {

	private final RepositoryClient repositoryClient;
	private final BranchClient branchClient;
	private final RepositoryReader repositoryReader;
	private final CommitClient commitClient;

	public RepositoryController(final String scmRestService){
		this.repositoryClient = new RepositoryClient(scmRestService);
		this.repositoryReader = new RepositoryReader();
		this.branchClient = new BranchClient(scmRestService);
		this.commitClient = new CommitClient(scmRestService);
	}

	public Repositories getRepositories() throws IOException {
		final String repositoriesIS = this.repositoryClient.getRepositories();
		return this.repositoryReader.readReposistories(repositoriesIS);
	}

	public Repository getRepository(final String repoId) throws IOException {
		final String repositoryIS = this.repositoryClient.getRepository(repoId);
		final String branchesIS = this.branchClient.getBranches(repoId);
		final String commitsIS = this.commitClient.getCommits(repoId);
		return this.repositoryReader.readRepository(repositoryIS, branchesIS, commitsIS);
	}

	public List<String> getRepositoryContributors(final String repoId) throws IOException {
		final String repositoryIS = this.repositoryClient.getRepository(repoId);
		final Repository repository=this.repositoryReader.readRepository(repositoryIS,null,null);
		return repository.getContributors();
	}

}
