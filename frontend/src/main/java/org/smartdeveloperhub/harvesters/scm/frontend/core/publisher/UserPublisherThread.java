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
import java.util.List;

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

public class UserPublisherThread extends PublisherThread {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPublisherThread.class);

	public UserPublisherThread(final BackendController controller) {
		super("User",controller);
	}

	@Override
	protected final void doPublish() {
		LOGGER.info("Started user population process...");
		try {
			final List<String> users = getController().getCommitters();
			if(users.isEmpty()) {
				LOGGER.info("No committers available");
				return;
			}
			publishUserResources(users);
		} catch (final Exception e) {
			LOGGER.error("Could not populate users", e);
		} finally {
			LOGGER.info("Finalized user population process");
		}
	}

	private void publishUserResources(final List<String> users) throws IOException {
		final ApplicationContext ctx = ApplicationContext.getInstance();
		try(WriteSession session = ctx.createSession()){
			final ContainerSnapshot userContainerSnapshot=
				session.find(
					ContainerSnapshot.class,
					IdentityUtil.userContainerName(),
					UserContainerHandler.class);
			for(final String userId:users){
				final Name<String> userName = IdentityUtil.userName(userId);
				userContainerSnapshot.addMember(userName);
			}
			session.saveChanges();
		} catch(final Exception e) {
			throw new IOException("Could not publish user resources",e);
		}
	}

}
