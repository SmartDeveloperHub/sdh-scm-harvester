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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester-testing-0.4.0-SNAPSHOT.jar
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
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathTemplateHandler;
import io.undertow.util.Methods;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.ActivityListener;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer.UpdateReport;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.EntityProvider;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.APIVersion;
import org.smartdeveloperhub.harvesters.scm.testing.handlers.Parameters;

import com.google.common.collect.Maps;

public final class TestingService {

	public static class Builder {

		private int port;
		private String exchangeName;
		private ActivityListener listener;
		private final Map<String,HttpHandler> endpoints;
		private APIVersion version;
		private String brokerHost;
		private int brokerPort;
		private String virtualHost;
		private String host;

		private Builder() {
			this.endpoints=Maps.newLinkedHashMap();
			this.host="localhost";
			this.version=APIVersion.v1;
			this.port=8080;
			this.brokerHost="localhost";
			this.brokerPort=5672;
			this.exchangeName="git.collector.mock";
			this.virtualHost="/";
		}

		private boolean isPort(final int port) {
			return port>0 && port <65536;
		}

		private Collector createCollectorConfiguration() {
			final Collector result = new Collector();
			result.setBrokerHost(this.brokerHost);
			result.setBrokerPort(this.brokerPort);
			result.setExchangeName(this.exchangeName);
			result.setVirtualHost(this.virtualHost);
			return result;
		}

		public Builder port(final int port) {
			checkArgument(isPort(port), "Port '%s' cannot be used",port);
			this.port=port;
			return this;
		}

		public Builder host(final String host) {
			if(host!=null) {
				checkArgument(!host.isEmpty(), "Git Collector broker host cannot be empty");
				this.host=host;
			}
			return this;
		}

		public Builder brokerHost(final String brokerHost) {
			if(brokerHost!=null) {
				checkArgument(!brokerHost.isEmpty(), "Git Collector broker host cannot be empty");
				this.brokerHost=brokerHost;
			}
			return this;
		}

		public Builder brokerPort(final int port) {
			checkArgument(isPort(port), "Git Collector broker port '%s' cannot be used",port);
			this.brokerPort=port;
			return this;
		}

		public Builder virtualHost(final String virtualHost) {
			if(virtualHost!=null) {
				checkArgument(!virtualHost.isEmpty(), "Git Collector broker virtual host cannot be empty");
				this.virtualHost=virtualHost;
			}
			return this;
		}

		public Builder exchangeName(final String exchangeName) {
			if(exchangeName!=null) {
				checkArgument(!exchangeName.isEmpty(), "Git Collector broker exchange name cannot be empty");
				this.exchangeName=exchangeName;
			}
			return this;
		}

		public Builder apiVersion(final APIVersion version) {
			if(version!=null) {
				this.version=version;
			}
			return this;
		}

		public Builder listener(final ActivityListener listener) {
			checkNotNull(listener, "Activity listener cannot be null");
			this.listener=listener;
			return this;
		}

		public Builder addEndpoint(final String path, final HttpHandler handler) {
			checkNotNull(path,"Path cannot be null");
			checkNotNull(handler,"Handler cannot be null");
			this.endpoints.put(path,handler);
			return this;
		}

		public TestingService build() {
			return
				new TestingService(
					this.host,
					this.port,
					createCollectorConfiguration(),
					this.version,
					this.listener,
					this.endpoints);
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(TestingService.class);

	private final String host;

	private final int port;

	private final Collector config;

	private final GitCollector collector;

	private final GitLabEnhancer enhancer;

	private final Undertow server;

	private boolean collectorStarted;

	private boolean serverStarted;

	private final APIVersion version;


	private TestingService(
			final String host,
			final int port,
			final Collector config,
			final APIVersion version,
			final ActivityListener listener,
			final Map<String, HttpHandler> endpoints) {
		this.host = host;
		this.port=port;
		this.version=version;
		this.config=config;
		this.config.setInstance("http://"+host+":"+port+"/collector");
		this.collector=GitCollector.newInstance(this.config);
		this.enhancer=GitLabEnhancer.newInstance(this.collector,URI.create("http://"+host+":"+port+"/enhancer/api"));
		if(listener!=null) {
			this.enhancer.registerListener(listener);
		}
		final PathTemplateHandler handler=
			pathTemplate(false).
				add("/collector",
					allow(Methods.POST,
						consume("application/psr.sdh.gitcollector+json",
							handleEvents(this.enhancer)))
				).
				add("/enhancer/api",
					provideEntity(
						this.version,
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
							this.version,
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
						this.version,
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
						this.version,
						new EntityProvider<List<String>>() {
							@Override
							public List<String> getEntity(final Parameters parameters) {
								return TestingService.this.enhancer.getRepositories();
							}
						}
					)
				).
				add("/enhancer/api/projects/{rid}",
					provideEntity(
						this.version,
						new EntityProvider<Repository>() {
							@Override
							public Repository getEntity(final Parameters parameters) {
								return TestingService.this.enhancer.getRepository(parameters.get("rid"));
							}
						}
					)
				).
				add("/enhancer/api/projects/{rid}/commits",
					provideEntity(
						this.version,
						new EntityProvider<List<String>>() {
							@Override
							public List<String> getEntity(final Parameters parameters) {
								return TestingService.this.enhancer.getRepositoryCommits(parameters.get("rid"));
							}
						}
					)
				).
				add("/enhancer/api/projects/{rid}/commits/{cid}",
					provideEntity(
						this.version,
						new EntityProvider<Commit>() {
							@Override
							public Commit getEntity(final Parameters parameters) {
								return TestingService.this.enhancer.getRepositoryCommit(parameters.get("rid"),parameters.get("cid"));
							}
						}
					)
				).
				add("/enhancer/api/projects/{rid}/branches",
					provideEntity(
						this.version,
						new EntityProvider<List<String>>() {
							@Override
							public List<String> getEntity(final Parameters parameters) {
								return TestingService.this.enhancer.getRepositoryBranches(parameters.get("rid"));
							}
						}
					)
				).
				add("/enhancer/api/projects/{rid}/branches/{bid}",
					provideEntity(
						this.version,
						new EntityProvider<Branch>() {
							@Override
							public Branch getEntity(final Parameters parameters) {
								return TestingService.this.enhancer.getRepositoryBranch(parameters.get("rid"),parameters.get("bid"));
							}
						}
					)
				).
				add("/enhancer/api/projects/{rid}/branches/{bid}/commits",
					provideEntity(
						this.version,
						new EntityProvider<List<String>>() {
							@Override
							public List<String> getEntity(final Parameters parameters) {
								return TestingService.this.enhancer.getRepositoryBranchCommits(parameters.get("rid"),parameters.get("bid"));
							}
						}
					)
				);
		for(final Entry<String,HttpHandler> entry:endpoints.entrySet()) {
			handler.add(entry.getKey(), entry.getValue());
		}
		this.server =
			Undertow.
				builder().
					addHttpListener(port, host).
					setHandler(handler).
					build();
		}

	public TestingService start() throws IOException {
		LOGGER.info("Starting Git Collector Publisher Service...");
		this.collector.start();
		LOGGER.info(
			"Git Collector Publisher Service started. Using broker {}:{}, virtual host '{}' and exchange name '{}'.",
			this.config.getBrokerHost(),
			this.config.getBrokerPort(),
			this.config.getVirtualHost(),
			this.config.getExchangeName());
		this.collectorStarted=true;
		LOGGER.info("Starting GitLab Enhancer Service ({}) ...",this.version);
		this.server.start();
		LOGGER.info(
			"GitLab Enhancer Service started. Service available at {}:{}.",
			this.host,
			this.port);
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

	public Branch getBranch(final String repositoryId, final String branchId) {
		return this.enhancer.getRepositoryBranch(repositoryId, branchId);
	}

}
