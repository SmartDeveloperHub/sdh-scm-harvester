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

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.frontend.core.GitLabHarvester;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;

public class BranchCommitPublisherThread extends Thread {

	private static final Logger LOGGER=LoggerFactory.getLogger(BranchCommitPublisherThread.class);

	private Thread t;
	private final String threadName = "BranchCommitPublisher";
	ContainerSnapshot branchContainerSnapshot;
	ContainerSnapshot commitContainerSnapshot;

	Repository repository;
	BackendController controller;
	GitLabHarvester gitLabHarvester;

	public BranchCommitPublisherThread(final BackendController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void run(){
		LOGGER.info(this.threadName +" is running...");
		final long startTime = System.currentTimeMillis();

		final ApplicationContext ctx = ApplicationContext.getInstance();

		try {
			final GitLabHarvester gitLabHarvester=this.controller.getGitLabHarvester();
			for (final Integer repositoryId:gitLabHarvester.getRepositories()){
				final Repository repository=this.controller.getRepository(Integer.toString(repositoryId));
				addBranchMemberstToRepository(ctx, repositoryId, repository);
				addCommitMembersToRepository(ctx, repositoryId, repository);
			}
		} catch (final Exception e) {
			LOGGER.error("Could not update repository information resource",e);
		} finally {
			LOGGER.debug("Finalized update repository process");
		}

		final long stopTime = System.currentTimeMillis();
		final long elapsedTime = stopTime - startTime;
		LOGGER.info("- thread elapsed time (ms)..: {}",elapsedTime);
	}

	@Override
	public void start() {
		LOGGER.info("Starting " + this.threadName);
		if (this.t == null) {
			this.t = new Thread(this, this.threadName);
			this.t.start();
		}
	}

	public void addBranchMemberstToRepository(final ApplicationContext ctx, final Integer repositoryId, final Repository repository) throws Exception{
		try( WriteSession session = ctx.createSession() ) {
			final Name<String> repositoryName = NamingScheme.getDefault().name(Integer.toString(repositoryId));

			//ResourceSnapshot repositorySnapshot = session.find(ResourceSnapshot.class,repositoryName,RepositoryHandler.class);

			this.branchContainerSnapshot = session.find(ContainerSnapshot.class,repositoryName,BranchContainerHandler.class);
			//This is an alternative:
			//ContainerSnapshot ContainerSnapshot = (ContainerSnapshot)repositorySnapshot.attachmentById(RepositoryHandler.REPOSITORY_BRANCHES).resource();

			if (this.branchContainerSnapshot!=null){
				for (final String branchId:repository.getBranches().getBranchIds()){
					final Name<String> branchName = NamingScheme.getDefault().name(Integer.toString(repository.getId()),branchId);
					//keeptrack of the branch key and resource name
					this.controller.getBranchIdentityMap().addKey(new BranchKey(Integer.toString(repository.getId()),branchId), branchName);
					@SuppressWarnings("unused")
					final ResourceSnapshot branchSnapshot = this.branchContainerSnapshot.addMember(branchName);
				}
			}
			session.modify(this.branchContainerSnapshot);
			session.saveChanges();
		}
	 }

	 public void addCommitMembersToRepository(final ApplicationContext ctx, final Integer repositoryId, final Repository repository) throws Exception{
		 try( WriteSession session = ctx.createSession() ) {
			final Name<String> repositoryName = NamingScheme.getDefault().name(Integer.toString(repositoryId));

			this.commitContainerSnapshot = session.find(ContainerSnapshot.class,repositoryName,CommitContainerHandler.class);

			if (this.commitContainerSnapshot!=null){

				for (final String commitId:repository.getCommits().getCommitIds()){
					final Name<String> commitName = NamingScheme.getDefault().name(Integer.toString(repository.getId()),commitId);
					//keeptrack of the branch key and resource name
					this.controller.getCommitIdentityMap().addKey(new CommitKey(Integer.toString(repository.getId()),commitId), commitName);
					@SuppressWarnings("unused")
					final ResourceSnapshot commitSnapshot = this.commitContainerSnapshot.addMember(commitName);
				}
			}

			session.modify(this.commitContainerSnapshot);
			session.saveChanges();
		}
	}

}
