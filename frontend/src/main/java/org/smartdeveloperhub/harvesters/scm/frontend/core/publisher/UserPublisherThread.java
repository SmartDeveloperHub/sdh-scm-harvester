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
import org.ldp4j.application.ApplicationContextException;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.SessionTerminationException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;

public class UserPublisherThread extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPublisherThread.class);
	private static final String THREAD_NAME = "UserPublisherThread";

	private final BackendController controller;

	public UserPublisherThread(final BackendController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void run() {
		LOGGER.info("Running {}...",THREAD_NAME);
		final long startTime = System.currentTimeMillis();

		try {
			final ApplicationContext ctx = ApplicationContext.getInstance();
			publishUserResources(ctx);
		} catch (final Exception e) {
			LOGGER.error("Could not update repository information resource", e);
		} finally {
			LOGGER.debug("Finalized update repository process");
		}

		final long stopTime = System.currentTimeMillis();
		final long elapsedTime = stopTime - startTime;
		LOGGER.info("{} Elapsed time (ms): {}",THREAD_NAME,elapsedTime);
	}

	@Override
	public void start() {
		LOGGER.info("Starting {}",THREAD_NAME);
	}

	public void publishUserResources(final ApplicationContext ctx) throws IOException, SessionTerminationException, ApplicationContextException, WriteSessionException {
		try(WriteSession session = ctx.createSession()){
			final Name<String> userContainerName = NamingScheme.getDefault().name(UserContainerHandler.NAME);
			final ContainerSnapshot userContainerSnapshot = session.find(ContainerSnapshot.class, userContainerName ,UserContainerHandler.class);
			if(userContainerSnapshot==null) {
				LOGGER.warn("User Container does not exist");
				return;
			}

			for(final String userId:this.controller.getUsers()){
				final Name<String> userName = NamingScheme.getDefault().name(userId);
				userContainerSnapshot.addMember(userName);
			}

			session.modify(userContainerSnapshot);
			session.saveChanges();
		}
	}

}
