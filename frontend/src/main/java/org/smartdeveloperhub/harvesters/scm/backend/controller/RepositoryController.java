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
package org.smartdeveloperhub.harvesters.scm.backend.controller;


import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.readers.RepositoryReader;
import org.smartdeveloperhub.harvesters.scm.backend.rest.BranchClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.CommitClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.RepositoryClient;

public class RepositoryController {
	RepositoryClient repositoryClient;
	BranchClient branchClient;
	RepositoryReader repositoryReader;
	CommitClient commitClient;
	
	public RepositoryController(String scmRestService){
		repositoryClient = new RepositoryClient(scmRestService);
		repositoryReader = new RepositoryReader();
		branchClient = new BranchClient(scmRestService);
		commitClient = new CommitClient(scmRestService);
	}
	
	public Repositories getRepositories() throws Exception{
		
		String repositoriesIS = repositoryClient.getRepositories();
		return repositoryReader.readReposistories(repositoriesIS);			
	}
	
	public Repository getRepository(String repoId) throws Exception{
		String repositoryIS = repositoryClient.getRepository(repoId);
		String branchesIS = branchClient.getBranches(repoId);
		String commitsIS = commitClient.getCommits(repoId);
		return repositoryReader.readRepository(repositoryIS, branchesIS, commitsIS);			
	}
	
	
	public Repository getRepositoryWithoutBranchCommit(String repoId) throws Exception{
		String repositoryIS = repositoryClient.getRepository(repoId);
		String branchesIS="";
		String commitsIS="";
		return repositoryReader.readRepository(repositoryIS, branchesIS, commitsIS);
	}
	
//	public Commits getCommits(String repoId) throws Exception{
//		String commitsIS = commitClient.getCommits(repoId);
//		return repositoryReader.readCommits(commitsIS);
//	}

	
}
