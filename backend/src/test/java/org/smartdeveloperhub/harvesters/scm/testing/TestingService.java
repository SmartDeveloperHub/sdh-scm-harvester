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
package org.smartdeveloperhub.harvesters.scm.testing;

import static io.undertow.Handlers.pathTemplate;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.allow;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.consume;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.handleEvents;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.provideEntity;
import io.undertow.Undertow;
import io.undertow.util.Methods;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.notification.GitCollector;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.EntityProvider;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.Parameters;

public class TestingService {

	private static final Logger LOGGER=LoggerFactory.getLogger(TestingService.class);

	public static void main(final String[] args) throws IOException {
		final Collector config = new Collector();
		config.setInstance("http://localhost:8080/collector");
		config.setBrokerHost("localhost");
		config.setExchangeName("git.collector.mock");
		final GitCollector collector=GitCollector.newInstance(config);
		final GitLabEnhancer enhancer=GitLabEnhancer.newInstance(collector);
		final Undertow server =
			Undertow.
				builder().
					addHttpListener(8080, "localhost").
					setHandler(
						pathTemplate(false).
							add("/collector",
								allow(Methods.POST,
									consume("application/psr.sdh.gitcollector+json",
										handleEvents(enhancer)))
							).
							add("/enhancer/api",
								provideEntity(
									new EntityProvider<Enhancer>() {
										@Override
										public Enhancer getEntity(final Parameters parameters) {
											return enhancer.getEnhancer(parameters.requestURL());
										}
									}
								)
							).
							add("/enhancer/api/users",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return enhancer.getCommitters();
										}
									}
								)
							).
							add("/enhancer/api/users/{userId}",
								provideEntity(
									new EntityProvider<User>() {
										@Override
										public User getEntity(final Parameters parameters) {
											return enhancer.getCommitter(parameters.get("userId"));
										}
									}
								)
							).
							add("/enhancer/api/projects",
								provideEntity(
									new EntityProvider<List<Integer>>() {
										@Override
										public List<Integer> getEntity(final Parameters parameters) {
											return enhancer.getRepositories();
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}",
								provideEntity(
									new EntityProvider<Repository>() {
										@Override
										public Repository getEntity(final Parameters parameters) {
											return enhancer.getRepository(parameters.getInteger("rid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/commits",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return enhancer.getRepositoryCommits(parameters.getInteger("rid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/commits/{cid}",
								provideEntity(
									new EntityProvider<Commit>() {
										@Override
										public Commit getEntity(final Parameters parameters) {
											return enhancer.getRepositoryCommit(parameters.getInteger("rid"),parameters.get("cid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/branches",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return enhancer.getRepositoryBranches(parameters.getInteger("rid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/branches/{bid}",
								provideEntity(
									new EntityProvider<Branch>() {
										@Override
										public Branch getEntity(final Parameters parameters) {
											return enhancer.getRepositoryBranch(parameters.getInteger("rid"),parameters.get("bid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/branches/{bid}/commits",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return enhancer.getRepositoryBranchCommits(parameters.getInteger("rid"),parameters.get("bid"));
										}
									}
								)
							)
					).
					build();
		try {
			LOGGER.info("Starting Git Collector Publisher Service...");
			collector.start();
			LOGGER.info("Git Collector Publisher Service started.");
			LOGGER.info("Starting GitLab Enhancer Service...");
			server.start();
			LOGGER.info("GitLab Enhancer Service started.");
			awaitTerminationRequest();
			LOGGER.info("Stopping GitLab Enhancer Service...");
			server.stop();
			LOGGER.info("GitLab Enhancer Service stopped.");
		} finally {
			LOGGER.info("Stopping Git Collector Publisher Service...");
			collector.shutdown();
			LOGGER.info("Git Collector Publisher Service stopped.");
		}

	}

	private static void awaitTerminationRequest() {
		try(final Scanner scanner = new Scanner(System.in)) {
		String readString = scanner.nextLine();
			while(readString != null) {
				if (readString.isEmpty()) {
					break;
				}
				if (scanner.hasNextLine()) {
					readString = scanner.nextLine();
				} else {
					readString = null;
				}
			}
		}
	}

}
