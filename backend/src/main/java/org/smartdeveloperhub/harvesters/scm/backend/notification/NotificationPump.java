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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class NotificationPump extends Thread {

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationPump.class);

	private final BlockingQueue<SuspendedNotification> notifications;

	private final NotificationListener listener;

	private volatile boolean stopped=false;

	public NotificationPump(final BlockingQueue<SuspendedNotification> notifications, final NotificationListener listener) {
		this.notifications = notifications;
		this.listener = listener;
	}

	@Override
	public void run() {
		LOGGER.info("Started pumping notifications");
		while(!this.stopped) {
			try {
				final SuspendedNotification notification=this.notifications.poll(1000, TimeUnit.MILLISECONDS);
				if(notification!=null) {
					notification.resume(this.listener);
				}
			} catch (final InterruptedException e) {
				this.stopped=true;
				LOGGER.info("Notification pumping interrupted.");
			}
		}
		final List<SuspendedNotification> discarded=new ArrayList<>();
		this.notifications.drainTo(discarded);
		if(!discarded.isEmpty()) {
			LOGGER.warn("{} notifications were dropped",discarded.size());
		}
		LOGGER.info("Notification pumping finished");
	}

	void shutdown() {
		this.stopped=true;
	}

}
