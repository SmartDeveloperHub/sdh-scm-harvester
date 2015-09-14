/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015 Center for Open Middleware.
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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.2.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityMap;

public class BackendController {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(BackendController.class);
	
	String scmRestService;
	
	IdentityMap<BranchKey> branchIdentityMap;
	IdentityMap<CommitKey> commitIdentityMap;
	
	public BackendController() {
	  String gitLabEnhancer = System.getenv("GITLAB_ENHANCER");
	  if (gitLabEnhancer==null){
		  scmRestService="http://192.168.0.10:5000/api";
		  LOGGER.info("GITLAB_ENHANCER by default {}",gitLabEnhancer);
	  }
	  else{		  
		  LOGGER.debug("GITLAB_ENHANCER environment variable {}",gitLabEnhancer);
		  scmRestService=gitLabEnhancer;		  
	  }
		  
		
	  branchIdentityMap=new IdentityMap<BranchKey>();
	  commitIdentityMap=new IdentityMap<CommitKey>();
	}
	
	public IdentityMap<BranchKey> getBranchIdentityMap() {
		return branchIdentityMap;
	}

	public void setBranchIdentityMap(IdentityMap<BranchKey> branchIdentityMap) {
		this.branchIdentityMap = branchIdentityMap;
	}

	public IdentityMap<CommitKey> getCommitIdentityMap() {
		return commitIdentityMap;
	}

	public void setCommitIdentityMap(IdentityMap<CommitKey> commitIdentityMap) {
		this.commitIdentityMap = commitIdentityMap;
	}

	public GitLabHarvester getGitLabHarvester(String id) throws Exception {
		
		RepositoryController repoCtl = new RepositoryController(scmRestService);
		Repositories repos = repoCtl.getRepositories();
		
		GitLabHarvester gitLabHarvester = new GitLabHarvester();	
		gitLabHarvester.setId(id);
		for(Integer repoId:repos.getRepositoryIds())
			gitLabHarvester.addRepository(repoId);
		
		return gitLabHarvester;
	}
	
	public ArrayList<String> getUsers() throws Exception{
		HashSet<String> uniqueUsers = new HashSet<String>(); 
		RepositoryController repoCtl = new RepositoryController(scmRestService);
		Repositories repos = repoCtl.getRepositories();
		for(Integer repoId:repos.getRepositoryIds()){
			Repository repo=repoCtl.getRepository(repoId.toString());
			List<Integer> contributors=repo.getContributors();
			for(Integer contributorId:contributors)
				uniqueUsers.add(Integer.toString(contributorId));
		}
		return new ArrayList<String>(uniqueUsers);
	}
	
	public Repository getRepository(String id) throws Exception {
		
		RepositoryController repoCtl = new RepositoryController(scmRestService);
		Repository repo = repoCtl.getRepository(id);				
		return repo;
	}
	
	public User getUser(String id) throws Exception {
		
		UserController userCtl = new UserController(scmRestService);
		User user = userCtl.getUser(id);				
		return user;
	}
		
	public Branch getBranch(String repoId, String branchId) throws Exception {
		BranchController branchCtl=new BranchController(scmRestService);
		Branch branch= branchCtl.getBranch(repoId, branchId);
		return branch;
	}
	

	public Commit getCommit(String repoId, String commitId) throws Exception {
		CommitController commitCtl = new CommitController(scmRestService);
		Commit commit=commitCtl.getCommit(repoId, commitId);
		return commit;
	}
	
	public static void main(String[] args) throws Exception {
		BackendController bkend = new BackendController();
		//GitLabHarvester gitLabHarvester = bkend.getGitLabHarvester("gitlab");
		Repository repo = bkend.getRepository("5");
		System.out.println(repo);
		System.out.println("*"+repo.getBranches());
		System.out.println("**"+repo.getCommits());
		//User user = bkend.getUser("3");
		//Branch branch = bkend.getBranch("5", "6D6173746572");
		//Commit commit  = bkend.getCommit("5", "1e1e34cb9cff911d08938a2b145a6687238d5f66");
		//System.out.println(commit);
	}





}
