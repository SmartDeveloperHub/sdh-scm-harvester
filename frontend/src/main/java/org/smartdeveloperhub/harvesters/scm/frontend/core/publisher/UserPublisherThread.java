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
import java.util.concurrent.TimeUnit;

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;

import com.google.common.base.Stopwatch;

public class UserPublisherThread extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPublisherThread.class);
	private static final String THREAD_NAME = "UserPublisherThread";

	private final BackendController controller;

	public UserPublisherThread(final BackendController controller) {
		super();
		setName(THREAD_NAME);
		setUncaughtExceptionHandler(
			new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(final Thread t, final Throwable e) {
					LOGGER.error("User Publisher thread died unexpectedly. Full stacktrace follows",e);
				}
			});
		this.controller = controller;
	}

	@Override
	public void run() {
		LOGGER.info("Running {}...",THREAD_NAME);
		final Stopwatch watch = Stopwatch.createStarted();
		try {
			publishUserResources(ApplicationContext.getInstance());
		} catch (final Exception e) {
			LOGGER.error("Could not populate users", e);
		} finally {
			LOGGER.info("Finalized repository users update process");
		}
		watch.stop();
		LOGGER.info("{} Elapsed time (ms): {}",THREAD_NAME,watch.elapsed(TimeUnit.MILLISECONDS));
	}

	@Override
	public void start() {
		LOGGER.info("Starting {}",THREAD_NAME);
		super.start();
	}

	private void publishUserResources(final ApplicationContext ctx) throws IOException {
		final List<String> users = this.controller.getCommitters();
		if(users.isEmpty()) {
			LOGGER.info("No committers available");
			return;
		}
		try(WriteSession session = ctx.createSession()){
			final Name<String> userContainerName = NamingScheme.getDefault().name(UserContainerHandler.NAME);
			final ContainerSnapshot userContainerSnapshot = session.find(ContainerSnapshot.class,userContainerName,UserContainerHandler.class);
			if(userContainerSnapshot==null) {
				LOGGER.error("User container does not exist");
				return;
			}
			for(final String userId:users){
				final Name<String> userName = NamingScheme.getDefault().name(userId);
				userContainerSnapshot.addMember(userName);
			}
			session.modify(userContainerSnapshot);
			session.saveChanges();
		} catch(final Exception e) {
			throw new IOException("Could not publish member resources",e);
		}
	}

}
