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

import java.io.IOException;

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

final class RepositoryContentPublisherTask extends PublisherTask {

	private static final Logger LOGGER=LoggerFactory.getLogger(RepositoryContentPublisherTask.class);

	RepositoryContentPublisherTask(final BackendController controller) {
		super("Repo. member publication",controller);
	}

	@Override
	protected final void doPublish() throws IOException {
		for(final Integer repositoryId:getController().getRepositories()){
			populateRepository(repositoryId);
		}
	}

	private void populateRepository(final Integer repositoryId) {
		LOGGER.info("Populating repository {}...",repositoryId);
		final ApplicationContext ctx = ApplicationContext.getInstance();
		try(WriteSession session=ctx.createSession()) {
			final Repository repository=getController().getRepository(repositoryId);
			final ResourceSnapshot repositorySnapshot = findRepositoryResource(session, repositoryId);
			publishRepositoryBranches(
				repository,
				getAttachedContainer(repositorySnapshot,RepositoryHandler.REPOSITORY_BRANCHES));
			publishRepositoryCommits(
				repository,
				getAttachedContainer(repositorySnapshot,RepositoryHandler.REPOSITORY_COMMITS));
			session.saveChanges();
		} catch (final Exception e) {
			LOGGER.error("Could not populate repository {}",repositoryId,e);
		}
	}

	private ResourceSnapshot findRepositoryResource(final WriteSession session, final Integer repositoryId) {
		final Name<Integer> repositoryName = IdentityUtil.repositoryName(repositoryId);
		ResourceSnapshot repositorySnapshot = session.find(ResourceSnapshot.class,repositoryName,RepositoryHandler.class);
		if(repositorySnapshot==null) {
			LOGGER.warn("Could not find resource for repository {}",repositoryId);
			repositorySnapshot=
				PublisherHelper.
					publishRepository(
						PublisherHelper.
							findRepositoryContainer(session,getController().getTarget()),
						repositoryId);
		}
		return repositorySnapshot;
	}

	private void publishRepositoryBranches(final Repository repository, final ContainerSnapshot branchContainer) throws IOException {
		if(repository.getBranches().getBranchIds().isEmpty()) {
			LOGGER.info("No branches available for repository {}",repository.getId());
			return;
		}
		try {
			for (final String branchId:repository.getBranches().getBranchIds()){
				branchContainer.
					addMember(
						IdentityUtil.
							branchName(
								new BranchKey(repository.getId(),branchId)));
			}
		} catch(final Exception e) {
			throw new IOException("Could not add branches to repository "+repository.getId(),e);
		}
	}

	private void publishRepositoryCommits(final Repository repository, final ContainerSnapshot commitContainer) throws IOException {
		if(repository.getCommits().getCommitIds().isEmpty()) {
			LOGGER.info("No commits available for repository {}",repository.getId());
			return;
		}
		try {
			for(final String commitId:repository.getCommits().getCommitIds()){
				commitContainer.
					addMember(
						IdentityUtil.
							commitName(
								new CommitKey(repository.getId(),commitId)));
			}
		} catch(final Exception e) {
			throw new IOException("Could not add commits to repository "+repository.getId(),e);
		}
	}

	private ContainerSnapshot getAttachedContainer(final ResourceSnapshot resource, final String attachmentId) {
		return (ContainerSnapshot)resource.attachmentById(attachmentId).resource();
	}

}
