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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;

import com.google.common.base.Stopwatch;

abstract class PublisherThread extends Thread {

	private static final Logger LOGGER=LoggerFactory.getLogger(PublisherThread.class);

	private final BackendController controller;

	public PublisherThread(final String threadName, final BackendController controller) {
		super();
		setName(threadName+"Publisher");
		setUncaughtExceptionHandler(
			new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(final Thread t, final Throwable e) {
					LOGGER.error("{} Publisher thread died unexpectedly. Full stacktrace follows",e);
				}
			});
		this.controller = controller;
	}

	@Override
	public final void run(){
		LOGGER.info("Running {}...",getName());
		final Stopwatch watch = Stopwatch.createStarted();
		try {
			doPublish();
		} finally {
			watch.stop();
			LOGGER.info("{} Elapsed time (ms): {}",getName(),watch.elapsed(TimeUnit.MILLISECONDS));
		}
	}

	@Override
	public final void start() {
		LOGGER.info("Starting {}...",getName());
		super.start();
	}

	protected abstract void doPublish();

	protected final BackendController getController() {
		return this.controller;
	}


}
