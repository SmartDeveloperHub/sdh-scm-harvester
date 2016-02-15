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
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;

final class BranchCommitPublisherThread extends PublisherThread {

	private static final Logger LOGGER=LoggerFactory.getLogger(BranchCommitPublisherThread.class);

	private static final String THREAD_NAME = "BranchCommit";

	BranchCommitPublisherThread(final BackendController controller) {
		super(THREAD_NAME,controller);
	}

	@Override
	protected final void doPublish(){
		LOGGER.info("Started repository population process...");
		try {
			for(final Integer repositoryId:getController().getRepositories()){
				populateRepository(repositoryId);
			}
		} catch (final IOException e) {
			LOGGER.error("Repository population failure",e);
		} finally {
			LOGGER.info("Finalized repository population process");
		}
	}

	private void populateRepository(final Integer repositoryId) {
		LOGGER.info("Populating repository {}...",repositoryId);
		final ApplicationContext ctx = ApplicationContext.getInstance();
		try(WriteSession session=ctx.createSession()) {
			PublisherHelper.
				publishRepository(
					session,
					getController().getTarget(),
					getController().getRepository(repositoryId));
			session.saveChanges();
		} catch (final Exception e) {
			LOGGER.error("Could not populate repository {}",repositoryId,e);
		}
	}

}
