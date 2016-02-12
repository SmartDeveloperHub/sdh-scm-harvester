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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;

final class StaticHarvesterPublisher extends HarvesterPublisher {

	private static final Logger LOGGER=LoggerFactory.getLogger(StaticHarvesterPublisher.class);

	private final BranchCommitPublisherThread branchCommitpublisher;
	private final UserPublisherThread userPublisher;

	StaticHarvesterPublisher(final BackendController controller) {
		this.branchCommitpublisher = new BranchCommitPublisherThread(controller);
		this.userPublisher = new UserPublisherThread(controller);
	}

	@Override
	public void start() throws Exception {
		LOGGER.info("SCM Harvester: Starting thread for registering branches and commits.");
		this.branchCommitpublisher.start();

		LOGGER.info("SCM Harvester: Starting thread for registering users.");
		this.userPublisher.start();
	}

	@Override
	public void stop() throws Exception {
		LOGGER.info("SCM Harvester: Awaiting termination of the thread for registering users.");
		this.userPublisher.join();

		LOGGER.info("SCM Harvester: Awaiting termination of the thread for registering branches and commits.");
		this.branchCommitpublisher.join();
	}

}