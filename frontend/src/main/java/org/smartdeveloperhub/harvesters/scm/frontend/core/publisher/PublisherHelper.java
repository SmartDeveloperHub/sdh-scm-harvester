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
import java.net.URI;
import java.util.List;

import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryUpdatedEvent;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

final class PublisherHelper {

	private static final Logger LOGGER=LoggerFactory.getLogger(BackendResourcePublisher.class);

	private PublisherHelper() {
	}

	static ResourceSnapshot publishRepository(final ContainerSnapshot repositoryContainer, final Integer repositoryId) {
		LOGGER.debug(
			"Started publishing resource for repository {} @ {} ({})...",
			repositoryId,
			repositoryContainer.name(),
			repositoryContainer.templateId());

		final Name<Integer> repositoryName=
			IdentityUtil.repositoryName(repositoryId);

		final ResourceSnapshot repository =
				repositoryContainer.addMember(repositoryName);

		repository.
			createAttachedResource(
				ContainerSnapshot.class,
				RepositoryHandler.REPOSITORY_BRANCHES,
				repositoryName,
				BranchContainerHandler.class);

		repository.
			createAttachedResource(
				ContainerSnapshot.class,
				RepositoryHandler.REPOSITORY_COMMITS,
				repositoryName,
				CommitContainerHandler.class);

		LOGGER.debug(
			"Published resource for repository {} @ {} ({})",
			repositoryId,
			repositoryContainer.name(),
			repositoryContainer.templateId());

		return repository;
	}

	static void publishUsers(final WriteSession session, final List<String> users) {
		final ContainerSnapshot userContainer=
			session.find(
				ContainerSnapshot.class,
				IdentityUtil.userContainerName(),
				UserContainerHandler.class);
		for(final String userId:users){
			final Name<String> userName = IdentityUtil.userName(userId);
			userContainer.addMember(userName);
		}
	}

	static void unpublishUsers(final WriteSession session, final List<String> users) {
		for(final String userId:users){
			final Name<String> userName = IdentityUtil.userName(userId);
			final ResourceSnapshot userResource = session.find(ResourceSnapshot.class, userName, UserHandler.class);
			if(userResource!=null) {
				session.delete(userResource);
			}
		}
	}

	static void publishRepositories(final WriteSession session, final URI target, final List<Integer> newRepositories) {
		final ContainerSnapshot repositoryContainer=findRepositoryContainer(session, target);
		for(final Integer repositoryId:newRepositories) {
			final Name<Integer> repositoryName = IdentityUtil.repositoryName(repositoryId);
			final ResourceSnapshot repositorySnapshot = session.find(ResourceSnapshot.class,repositoryName,RepositoryHandler.class);
			if(repositorySnapshot==null) {
				PublisherHelper.
					publishRepository(repositoryContainer, repositoryId);
			}
		}

	}

	static void unpublishRepositories(final WriteSession session, final URI target, final List<Integer> deletedRepositories) {
		for(final Integer repositoryId:deletedRepositories) {
			final Name<Integer> repositoryName = IdentityUtil.repositoryName(repositoryId);
			final ResourceSnapshot repositorySnapshot = session.find(ResourceSnapshot.class,repositoryName,RepositoryHandler.class);
			if(repositorySnapshot!=null) {
				session.delete(repositorySnapshot);
			}
		}

	}

	static ContainerSnapshot findRepositoryContainer(final WriteSession session, final URI target) {
		return
			session.
				find(
					ContainerSnapshot.class,
					IdentityUtil.enhancerName(target),
					RepositoryContainerHandler.class);
	}

	static void updateRepository(final WriteSession session, final URI target, final RepositoryUpdatedEvent event) throws IOException {
		final Name<Integer> repositoryName = IdentityUtil.repositoryName(event.getRepository());
		final ResourceSnapshot repositorySnapshot = session.find(ResourceSnapshot.class,repositoryName,RepositoryHandler.class);
		if(repositorySnapshot==null) {
			throw new IOException("Repository "+event.getRepository()+" does not exist");
		}

		publishRepositoryBranches(
			event.getRepository(),
			event.getNewBranches(),
			repositorySnapshot);

		unpublishRepositoryBranches(
			event.getRepository(),
			event.getDeletedBranches(),
			session);

		publishRepositoryCommits(
			event.getRepository(),
			event.getNewCommits(),
			repositorySnapshot);

		unpublishRepositoryCommits(
			event.getRepository(),
			event.getDeletedBranches(),
			session);
	}

	private static void publishRepositoryBranches(final Integer repositoryId, final List<String> branches, final ResourceSnapshot repositorySnapshot) throws IOException {
		if(branches.isEmpty()) {
			LOGGER.info("No new branches added to repository {}",repositoryId);
			return;
		}
		final ContainerSnapshot branchContainer=
			getAttachedContainer(
				repositorySnapshot,
				RepositoryHandler.REPOSITORY_BRANCHES);
		try {
			for(final String branchId:branches){
				branchContainer.
					addMember(
						IdentityUtil.
							branchName(
								new BranchKey(repositoryId,branchId)));
			}
		} catch(final Exception e) {
			throw new IOException("Could not add new branches to repository "+repositoryId,e);
		}
	}

	private static void unpublishRepositoryBranches(final Integer repositoryId, final List<String> branches, final WriteSession session) throws IOException {
		if(branches.isEmpty()) {
			LOGGER.info("No branches removed from repository {}",repositoryId);
			return;
		}
		try {
			for (final String branchId:branches){
				final ResourceSnapshot branchResource =
					session.
						find(
							ResourceSnapshot.class,
							IdentityUtil.
								branchName(new BranchKey(repositoryId,branchId)),
							BranchHandler.class);
				if(branchResource!=null) {
					session.delete(branchResource);
				}
			}
		} catch(final Exception e) {
			throw new IOException("Could not remove branches from repository "+repositoryId,e);
		}
	}

	private static void publishRepositoryCommits(final Integer repositoryId, final List<String> commits, final ResourceSnapshot repositorySnapshot) throws IOException {
		if(commits.isEmpty()) {
			LOGGER.info("No new commits added to repository {}",repositoryId);
			return;
		}
		final ContainerSnapshot commitContainer=
			getAttachedContainer(
				repositorySnapshot,
				RepositoryHandler.REPOSITORY_COMMITS);
		try {
			for(final String commitId:commits){
				commitContainer.
					addMember(
						IdentityUtil.
							commitName(
								new CommitKey(repositoryId,commitId)));
			}
		} catch(final Exception e) {
			throw new IOException("Could not add new commits to repository "+repositoryId,e);
		}
	}

	private static void unpublishRepositoryCommits(final Integer repositoryId, final List<String> commits, final WriteSession session) throws IOException {
		if(commits.isEmpty()) {
			LOGGER.info("No commits removed from repository {}",repositoryId);
			return;
		}
		try {
			for (final String commitId:commits){
				final ResourceSnapshot commitResource =
					session.
						find(
							ResourceSnapshot.class,
							IdentityUtil.
								commitName(new CommitKey(repositoryId,commitId)),
							CommitHandler.class);
				if(commitResource!=null) {
					session.delete(commitResource);
				}
			}
		} catch(final Exception e) {
			throw new IOException("Could not remove commits from repository "+repositoryId,e);
		}
	}

	private static ContainerSnapshot getAttachedContainer(final ResourceSnapshot resource, final String attachmentId) {
		return (ContainerSnapshot)resource.attachmentById(attachmentId).resource();
	}

}
