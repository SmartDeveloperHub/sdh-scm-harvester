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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static io.undertow.Handlers.pathTemplate;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.allow;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.consume;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.handleEvents;
import static org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.provideEntity;
import io.undertow.Undertow;
import io.undertow.util.Methods;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Event;
import org.smartdeveloperhub.harvesters.scm.backend.notification.GitCollector;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer.UpdateReport;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.EntityProvider;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.Parameters;

public final class TestingService {

	public static class Builder {

		private int port=8080;
		private String exchangeName="git.collector.mock";

		private Builder() {
		}

		public Builder port(final int port) {
			checkArgument(port>0 && port <65536, "Port '%s' cannot be used",port);
			this.port=port;
			return this;
		}

		public Builder exchangeName(final String exchangeName) {
			checkNotNull(exchangeName, "Git Collector broker exchange name cannot be null");
			checkArgument(exchangeName.isEmpty(), "Git Collector broker exchange name cannot be empty");
			this.exchangeName=exchangeName;
			return this;
		}

		public TestingService build() {
			return new TestingService(this.port, this.exchangeName);
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(TestingService.class);

	private final Collector config;

	private final GitCollector collector;

	private final GitLabEnhancer enhancer;

	private final Undertow server;

	private boolean collectorStarted;

	private boolean serverStarted;

	private TestingService(final int port, final String exchangeName) {
		this.config=createControllerConfiguration(port, exchangeName);
		this.collector = GitCollector.newInstance(this.config);
		this.enhancer = GitLabEnhancer.newInstance(this.collector);
		this.server =
			Undertow.
				builder().
					addHttpListener(port, "localhost").
					setHandler(
						pathTemplate(false).
							add("/collector",
								allow(Methods.POST,
									consume("application/psr.sdh.gitcollector+json",
										handleEvents(this.enhancer)))
							).
							add("/enhancer/api",
								provideEntity(
									new EntityProvider<Enhancer>() {
										@Override
										public Enhancer getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getEnhancer(parameters.requestURL());
										}
									}
								)
							).
							add("/enhancer/api/users",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getCommitters();
										}
									}
								)
							).
							add("/enhancer/api/users/{userId}",
								provideEntity(
									new EntityProvider<User>() {
										@Override
										public User getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getCommitter(parameters.get("userId"));
										}
									}
								)
							).
							add("/enhancer/api/projects",
								provideEntity(
									new EntityProvider<List<Integer>>() {
										@Override
										public List<Integer> getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getRepositories();
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}",
								provideEntity(
									new EntityProvider<Repository>() {
										@Override
										public Repository getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getRepository(parameters.getInteger("rid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/commits",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getRepositoryCommits(parameters.getInteger("rid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/commits/{cid}",
								provideEntity(
									new EntityProvider<Commit>() {
										@Override
										public Commit getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getRepositoryCommit(parameters.getInteger("rid"),parameters.get("cid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/branches",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getRepositoryBranches(parameters.getInteger("rid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/branches/{bid}",
								provideEntity(
									new EntityProvider<Branch>() {
										@Override
										public Branch getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getRepositoryBranch(parameters.getInteger("rid"),parameters.get("bid"));
										}
									}
								)
							).
							add("/enhancer/api/projects/{rid}/branches/{bid}/commits",
								provideEntity(
									new EntityProvider<List<String>>() {
										@Override
										public List<String> getEntity(final Parameters parameters) {
											return TestingService.this.enhancer.getRepositoryBranchCommits(parameters.getInteger("rid"),parameters.get("bid"));
										}
									}
								)
							)
					).
					build();
		}

	public TestingService start() throws IOException {
		LOGGER.info("Starting Git Collector Publisher Service...");
		this.collector.start();
		LOGGER.info("Git Collector Publisher Service started.");
		this.collectorStarted=true;
		LOGGER.info("Starting GitLab Enhancer Service...");
		this.server.start();
		LOGGER.info("GitLab Enhancer Service started.");
		this.serverStarted=true;
		return this;
	}

	public UpdateReport update(final Event event) {
		checkState(this.serverStarted,"Testing service not started");
		return this.enhancer.update(event);
	}

	public TestingService shutdown() {
		if(this.serverStarted) {
			LOGGER.info("Stopping GitLab Enhancer Service...");
			this.server.stop();
			LOGGER.info("GitLab Enhancer Service stopped.");
			this.serverStarted=false;
		}
		if(this.collectorStarted) {
			LOGGER.info("Stopping Git Collector Publisher Service...");
			this.collector.shutdown();
			LOGGER.info("Git Collector Publisher Service stopped.");
			this.collectorStarted=false;
		}
		return this;
	}

	public static Builder builder() {
		return new Builder();
	}

	private static Collector createControllerConfiguration(final int port, final String exchangeName) {
		final Collector config = new Collector();
		config.setInstance("http://localhost:"+port+"/collector");
		config.setBrokerHost("localhost");
		config.setExchangeName(exchangeName);
		return config;
	}

}
