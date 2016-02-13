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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.session.AttachmentSnapshot;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.controller.EnhancerController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branches;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;

import com.google.common.collect.Lists;

@RunWith(JMockit.class)
public class DynamicPublisherTest {

	private static final String ENDPOINT = "http://www.example.org:5000/api";
	private static final URI GITLAB_ENHANCER = URI.create(ENDPOINT);

	private static final Logger LOGGER=LoggerFactory.getLogger(DynamicPublisher.class);

	@SuppressWarnings("unchecked")
	@Test
	public void testLifecycle(
			@Mocked final ApplicationContext context,
			@Mocked final WriteSession session,
			@Mocked final ContainerSnapshot snapshot,
			@Mocked final AttachmentSnapshot attachmentSnapshot) throws Exception {
		new MockUp<EnhancerController>() {
			@Mock(invocations=1)
			void $init(final String target) {
				assertThat(target,equalTo(ENDPOINT));
			}
			@Mock(invocations=1)
			Enhancer getEnhancer() throws IOException {
				final Enhancer enhancer = new Enhancer();
				enhancer.setId(ENDPOINT);
				enhancer.setName("GitLab Enhancer");
				enhancer.setVersion("Unit testing");
				enhancer.setStatus("Ok");
//				final Collector collector = new Collector();
//				enhancer.setCollectors(Arrays.asList(collector));
				return enhancer;
			}
		};
		final BackendController controller=
			new MockUp<BackendController>() {
				private final Random random = new Random(System.nanoTime());
				@Mock(invocations=1)
				URI getTarget() {
					return GITLAB_ENHANCER;
				}
				@Mock
				List<String> getCommitters() {
					return Lists.newArrayList("user1","user2");
				}
				@Mock
				Repository getRepository(final Integer repoId) {
					final Repository repo = createRepository(repoId,1+this.random.nextInt(7), 5+this.random.nextInt(50));
					try {
						TimeUnit.MILLISECONDS.sleep(100+this.random.nextInt(500));
					} catch (final InterruptedException e) {
					}
					return repo;
				}
				@Mock
				List<Integer> getRepositories() {
					return Lists.newArrayList(1,2,3,4,5,6,7,8,9,10);
				}
				private Repository createRepository(final int repoId, final int branchCount, final int commitCount) {
					final Repository repository = new Repository();
					final Branches branches = new Branches();
					branches.setBranchIds(createValues(repoId,"br",branchCount));
					repository.setBranches(branches);
					final Commits commits=new Commits();
					commits.setCommitIds(createValues(repoId,"cm",commitCount));
					repository.setCommits(commits);
					return repository;
				}
				private List<String> createValues(final int repoId, final String tag, final int count) {
					final List<String> values=new ArrayList<>();
					for(int j=0;j<count;j++) {
						values.add("r_"+repoId+"_"+tag+"_"+j);

					}
					return values;
				}
			}.
			getMockInstance();
		new Expectations() {{
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			session.find(ContainerSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			session.find(ResourceSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			snapshot.addMember((Name<?>)this.any);this.result=snapshot;
//			snapshot.createAttachedResource(ContainerSnapshot.class,(String)this.any,(Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			snapshot.attachmentById((String)this.any);this.result=attachmentSnapshot;
			attachmentSnapshot.resource();this.result=snapshot;
		}};
		final DynamicPublisher sut=new DynamicPublisher(controller);
		sut.start();
		try {
			sut.awaitPublicationCompletion();
			LOGGER.info("Detected publication completion...");
		} finally {
			sut.stop();
		}
	}

}
