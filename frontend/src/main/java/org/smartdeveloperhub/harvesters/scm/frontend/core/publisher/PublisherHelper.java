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

import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

final class PublisherHelper {

	private static final Logger LOGGER=LoggerFactory.getLogger(BackendResourcePublisher.class);

	private PublisherHelper() {
	}

	static final ResourceSnapshot publishRepository(final ContainerSnapshot repositoryContainer, final Integer repositoryId) {
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

}
