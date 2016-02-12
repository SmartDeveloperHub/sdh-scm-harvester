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
package org.smartdeveloperhub.harvesters.scm.frontend.core;

import java.net.URI;

import org.ldp4j.application.ext.Application;
import org.ldp4j.application.ext.ApplicationInitializationException;
import org.ldp4j.application.ext.ApplicationSetupException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.harvester.HarvesterHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendResourcePublisher;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.HarvesterPublisher;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

public final class HarvesterApplication extends Application<HarvesterConfiguration> {

	private static final Logger LOGGER=LoggerFactory.getLogger(HarvesterApplication.class);

	private static final String SERVICE_PATH="service/";

	private URI target;

	private BackendController controller;

	@Override
	public void setup(final Environment environment, final Bootstrap<HarvesterConfiguration> bootstrap) throws ApplicationSetupException{
		LOGGER.info("Starting SCM Harvester Application configuration...");
		final HarvesterConfiguration configuration = bootstrap.configuration();
		this.target=configuration.target();
		if(this.target==null) {
			LOGGER.error("No target GitLab Enhancer configured");
			throw new ApplicationSetupException("No target GitLab Enhancer configured");
		}
		LOGGER.info("- Target..: {}",configuration.target());
		this.controller = new BackendController(this.target);

		environment.lifecycle().register(HarvesterPublisher.newInstance(this.controller));

		bootstrap.addHandler(new HarvesterHandler());
		bootstrap.addHandler(new RepositoryHandler(this.controller));
		bootstrap.addHandler(new UserHandler(this.controller));
		bootstrap.addHandlerClass(UserContainerHandler.class);
		bootstrap.addHandler(new BranchHandler(this.controller));
		bootstrap.addHandlerClass(BranchContainerHandler.class);
		bootstrap.addHandler(new CommitHandler(this.controller));
		bootstrap.addHandlerClass(CommitContainerHandler.class);

		environment.
			publishResource(
				IdentityUtil.enhancerName(this.target),
				HarvesterHandler.class,
				SERVICE_PATH);

		LOGGER.info("SCM Harvester Application configuration completed.");
	}

	@Override
	public void initialize(final WriteSession session) throws ApplicationInitializationException {
		LOGGER.info("Initializing SCM Harvester Application...");
		final BackendResourcePublisher publisher = new BackendResourcePublisher(session, this.controller);
		try {
			publisher.publishHarvesterResources();
			session.saveChanges();
			LOGGER.info("SCM Harvester Application initialization completed.");
		} catch (final Exception e) {
			final String errorMessage = "SCM Harvester Application initialization failed";
			LOGGER.warn(errorMessage+". Full stacktrace follows: ",e);
			throw new ApplicationInitializationException(e);
		}
	}

	@Override
	public void shutdown() {
		LOGGER.info("Starting *SCM Harvester Application* shutdown...");
		LOGGER.info("SCM Harvester Application shutdown completed.");
	}

}
