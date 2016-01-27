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
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import java.io.IOException;
import java.util.List;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branches;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.rest.RepositoryClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class RepositoryReader {
	
	ObjectMapper mapper = new ObjectMapper();
	
	RepositoryClient repoClient;
	Repositories repositories; //pojo
	Repository repository; //pojo
	
	BranchReader branchReader;
	CommitReader commitReader;

	private Branches branches;

	private Commits commits;
	
	public Repositories readReposistories(String repositoriesIS) throws JsonParseException, JsonMappingException, IOException {
		List<Integer> list = mapper.readValue(repositoriesIS, 
				  TypeFactory.defaultInstance().constructCollectionType(List.class, Integer.class));
		//repositories = mapper.readValue(repositoriesIS, Repositories.class);		
		repositories = new Repositories();
		repositories.setRepositoryIds(list);
		return repositories;
	}
	
	public Repository readRepository(String repositoryIS, String branchesIS) throws JsonParseException, JsonMappingException, IOException{
		repository=readRepository(repositoryIS);
		branchReader= new BranchReader();
		branches=branchReader.readBranches(branchesIS);
		repository.setBranches(branches);
		return repository;		
	}

	private Repository readRepository(String repositoryIS) throws JsonParseException, JsonMappingException, IOException {						 
    		repository=mapper.readValue(repositoryIS, Repository.class);
    		return repository;
	}

	
	public Repository readRepository(String repositoryIS,
			String branchesIS, String commitsIS) throws JsonParseException, JsonMappingException, IOException {
		repository=readRepository(repositoryIS);
		if (!branchesIS.isEmpty()){
			branchReader= new BranchReader();
			branches=branchReader.readBranches(branchesIS);
			repository.setBranches(branches);
		}
		if (!commitsIS.isEmpty()){
			commitReader = new CommitReader();
			commits=commitReader.readCommits(commitsIS);
			repository.setCommits(commits);
		}
		return repository;
	}
	
	
	

}
