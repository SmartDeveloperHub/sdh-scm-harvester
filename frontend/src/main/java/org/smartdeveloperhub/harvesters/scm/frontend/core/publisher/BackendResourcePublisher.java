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

import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.harvester.HarvesterHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

final class BackendResourcePublisher {

	private static final Logger LOGGER=LoggerFactory.getLogger(BackendResourcePublisher.class);

	private final WriteSession session;
	private final BackendController controller;

	BackendResourcePublisher(final WriteSession session, final BackendController controller) {
		this.controller=controller;
		this.session=session;
	}

	void publishHarvesterResources() throws IOException {
		LOGGER.info("Publishing SCM Harvester Resource...");
		final URI target = this.controller.getTarget();

		final Name<URI> harvesterName = IdentityUtil.enhancerName(target);

		final ResourceSnapshot harvesterSnapshot=
			this.session.
				find(
					ResourceSnapshot.class,
					harvesterName,
					HarvesterHandler.class);

		publishAttachedUserContainer(harvesterName, harvesterSnapshot);

		final ContainerSnapshot repositoryContainer =
			publishAttachedRepositoryContainer(harvesterName, harvesterSnapshot);

		createRepositoryResources(repositoryContainer);

		LOGGER.info("Published SCM Harvester Resource");
	}

	private void publishAttachedUserContainer(final Name<URI> harvesterName, final ResourceSnapshot harvesterSnapshot) {
		harvesterSnapshot.
			createAttachedResource(
				ContainerSnapshot.class,
				HarvesterHandler.HARVESTER_COMMITTERS,
				IdentityUtil.userContainerName(),
				UserContainerHandler.class);
		LOGGER.debug("Published user container for service {}", harvesterName);
	}

	private ContainerSnapshot publishAttachedRepositoryContainer(final Name<URI> harvesterName, final ResourceSnapshot harvesterSnapshot) {
		final ContainerSnapshot repositoryContainer =
			harvesterSnapshot.
				createAttachedResource(
					ContainerSnapshot.class,
					HarvesterHandler.HARVESTER_REPOSITORIES,
					harvesterName,
					RepositoryContainerHandler.class);

		LOGGER.debug("Published repository container for service {}", harvesterName);
		return repositoryContainer;
	}

	private void createRepositoryResources(final ContainerSnapshot repositoryContainer) throws IOException {
		for(final Integer repositoryId:this.controller.getRepositories()){
			PublisherHelper.publishRepository(repositoryContainer, repositoryId);
		}
	}

}
