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
package org.smartdeveloperhub.harvesters.scm.frontend.core.publisher;

import java.net.URI;
import java.util.ArrayList;

import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.frontend.core.GitLabHarvester;
import org.smartdeveloperhub.harvesters.scm.frontend.core.HarvesterApplication;
import org.smartdeveloperhub.harvesters.scm.frontend.core.Repository.RepositoryContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.Repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.harvester.HarvesterHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;

public class BackendResourcePublisher {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(BackendResourcePublisher.class);
	
	WriteSession session;
	BackendController controller;
	
	public BackendResourcePublisher(WriteSession session, BackendController controller) {
		this.controller=controller;
		this.session=session;
	}
	
	public void publishHarvesterResources(URI target) throws Exception{
		LOGGER.info("Publishing SCM Harvester Resource...");
		
		Name<URI> harvesterName = NamingScheme.getDefault().name(target);
		ResourceSnapshot harvesterSnapshot=session.find(ResourceSnapshot.class, harvesterName, HarvesterHandler.class);
		
		ContainerSnapshot repositoryContainerSnapshot = harvesterSnapshot.createAttachedResource( ContainerSnapshot.class, HarvesterHandler.HARVESTER_REPOSITORIES,
												  harvesterName, RepositoryContainerHandler.class);
		LOGGER.debug("Published repository container for service {}", harvesterName);
		
		//only add the repository to the container (Does not include branch or commit information)
		addRepositoryMembersToHarvester(target, repositoryContainerSnapshot);				
				
	}
	
	private void addRepositoryMembersToHarvester(URI target, ContainerSnapshot repositoryContainerSnapshot) throws Exception{
		GitLabHarvester gitLabHarvester = controller.createGitLabHarvester(target.toString());	
		for (Integer repositoryId:gitLabHarvester.getRepositories()){
			LOGGER.debug("Starting to publish resource for repository {} @ {} ({})",repositoryId, repositoryContainerSnapshot.name(),repositoryContainerSnapshot.templateId());
			
			Name<String> repositoryName = NamingScheme.getDefault().name(Integer.toString(repositoryId));	
			
			ResourceSnapshot repositorySnapshot = repositoryContainerSnapshot.addMember(repositoryName);
			
			ContainerSnapshot branchContainerSnapshot = repositorySnapshot.createAttachedResource( ContainerSnapshot.class, RepositoryHandler.REPOSITORY_BRANCHES,
					repositoryName, BranchContainerHandler.class);
			
			//Repository repo=controller.getRepository(Integer.toString(repositoryId));
			
//			addBranchMemberstToRepository(repo, branchContainerSnapshot);
			
			ContainerSnapshot commitContainerSnapshot = repositorySnapshot.createAttachedResource( ContainerSnapshot.class, RepositoryHandler.REPOSITORY_COMMITS,
					repositoryName, CommitContainerHandler.class);
			
//			ThreadedPublisher threadedPublisher = new ThreadedPublisher(branchContainerSnapshot,commitContainerSnapshot, repo, controller);
//			threadedPublisher.start();
//			addCommitMembersToRepository(repo, commitContainerSnapshot);
			
			LOGGER.debug("Published resource for repository {} @ {} ({})",repositoryId, repositoryContainerSnapshot.name(),repositoryContainerSnapshot.templateId());
		}
	}
	
//	private void addBranchMemberstToRepository(Repository repository, ContainerSnapshot branchContainerSnapshot) throws Exception{		
//		for (String branchId:repository.getBranches().getBranchIds()){
//			Name<String> branchName = NamingScheme.getDefault().name(Integer.toString(repository.getId()),branchId);			
//			//keeptrack of the branch key and resource name
//			controller.getBranchIdentityMap().addKey(new BranchKey(Integer.toString(repository.getId()),branchId), branchName);
//			ResourceSnapshot branchSnapshot = branchContainerSnapshot.addMember(branchName);			
//		}		
//	}
//	
//	private void addCommitMembersToRepository(Repository repository,
//			ContainerSnapshot commitContainerSnapshot) throws Exception {
//		for (String commitId:repository.getCommits().getCommitIds()){
//			Name<String> commitName = NamingScheme.getDefault().name(Integer.toString(repository.getId()),commitId);			
//			//keeptrack of the branch key and resource name
//			controller.getCommitIdentityMap().addKey(new CommitKey(Integer.toString(repository.getId()),commitId), commitName);
//			ResourceSnapshot commitSnapshot = commitContainerSnapshot.addMember(commitName);			
//		}
//		
//	}
	
	public void publishUserResources() throws Exception{
		Name<String> userContainerName = NamingScheme.getDefault().name(UserContainerHandler.NAME);
		ContainerSnapshot userContainerSnapshot = session.find(ContainerSnapshot.class, userContainerName ,UserContainerHandler.class);			
		if(userContainerSnapshot==null) {
			LOGGER.warn("User Container does not exits");
			return;
		}
		
		ArrayList<String> userIds = controller.getUsers();	
		for (String userId:userIds){			
			Name<String> userName = NamingScheme.getDefault().name(userId);			
			ResourceSnapshot userSnapshot = userContainerSnapshot.addMember(userName);
			LOGGER.debug("Published resource for user {} @ {} ({})",userId, userSnapshot.name(),userSnapshot.templateId());
		}
		
	}

}
