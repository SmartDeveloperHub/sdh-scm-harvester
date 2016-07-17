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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;

@RunWith(JMockit.class)
public class PublisherThreadTest {

	@Mocked	private BackendController controller;

	@Test
	public void testExecutesTask() throws Exception {
		final AtomicBoolean invoked=new AtomicBoolean(false);
		final PublisherThread sut =
			new PublisherThread(
				"name",
				new PublisherTask("taskName",this.controller) {
					@Override
					protected void doPublish() throws Exception {
						invoked.set(true);
					}
				}
			) {};
		sut.start();
		sut.join();
		assertThat(invoked.get(),equalTo(true));
	}

	@Test
	public void testCapturesTaskFailure(@Mocked final LoggerFactory factory, @Mocked final Logger logger) throws Exception {
		final CountDownLatch latch=new CountDownLatch(1);
		final AtomicBoolean invoked=new AtomicBoolean(false);
		final PublisherThread sut =
			new PublisherThread(
				"threadName",
				new PublisherTask("taskName",this.controller) {
					@Override
					protected void doPublish() throws Exception {
						invoked.set(true);
						latch.countDown();
						throw new Error("Failure");
					}
				}
			) {};
		sut.start();
		latch.await();
		Thread.sleep(1000);
		assertThat(invoked.get(),equalTo(true));
		assertThat(sut.isAlive(),equalTo(false));
	}

}
