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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0
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

final class RepositoryContentPublisherTask extends PublisherTask {

	private static final Logger LOGGER=LoggerFactory.getLogger(RepositoryContentPublisherTask.class);

	RepositoryContentPublisherTask(final BackendController controller) {
		super("Repo. member publication",controller);
	}

	@Override
	protected final void doPublish() throws IOException {
		for(final String repositoryId:getController().getRepositories()){
			populateRepository(repositoryId);
		}
	}

	private void populateRepository(final String repositoryId) throws IOException {
		LOGGER.info("Populating repository {}...",repositoryId);
		final ApplicationContext ctx = ApplicationContext.getInstance();
		WriteSession session=null;
		try {
			session=ctx.createSession();
			PublisherHelper.
				publishRepository(
					session,
					getController().getTarget(),
					getController().getRepository(repositoryId));
			session.saveChanges();
		} catch (final Exception e) {
			throw new IOException("Could not populate repository "+repositoryId,e);
		} finally {
			PublisherHelper.closeGracefully(session);
		}
	}

}
