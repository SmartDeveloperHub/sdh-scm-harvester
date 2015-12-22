/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015 Center for Open Middleware.
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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core;

import java.io.IOException;
import java.net.URI;

import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.Application;
import org.ldp4j.application.ext.ApplicationInitializationException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.frontend.core.Repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.harvester.HarvesterHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendResourcePublisher;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BranchCommitPublisherThread;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.UserPublisherThread;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
//import org.smartdeveloperhub.harvesters.ci.backend.ContinuousIntegrationService;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.BackendControllerManager;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.BackendModelPublisher;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.FrontendSynchronizer;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.build.BuildContainerHandler;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.build.BuildHandler;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.build.SubBuildContainerHandler;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.execution.ExecutionContainerHandler;
//import org.smartdeveloperhub.harvesters.ci.frontend.core.execution.ExecutionHandler;

public final class HarvesterApplication extends Application<HarvesterConfiguration> {

	private static final Logger LOGGER=LoggerFactory.getLogger(HarvesterApplication.class);

	private static final String SERVICE_PATH="service/";

	private URI target;
		

	private BackendController controller;

	@Override
	public void setup(Environment environment, Bootstrap<HarvesterConfiguration> bootstrap){
		LOGGER.info("Starting SCM Harvester Application configuration...");

		HarvesterConfiguration configuration = bootstrap.configuration();

		try {
			LOGGER.info("- Target..: {}",configuration.target());		
			this.target=configuration.target();
		
			controller = new BackendController();
	
			bootstrap.addHandler(new HarvesterHandler(controller));
	 	    bootstrap.addHandler(new RepositoryHandler(controller));
			bootstrap.addHandler(new UserHandler(controller));
			bootstrap.addHandlerClass(UserContainerHandler.class);
			bootstrap.addHandler(new BranchHandler(this.controller));
			bootstrap.addHandlerClass(BranchContainerHandler.class);
			bootstrap.addHandler(new CommitHandler(this.controller));
			bootstrap.addHandlerClass(CommitContainerHandler.class);
				
			environment.
				publishResource(NamingScheme.getDefault().name(target),HarvesterHandler.class, SERVICE_PATH);
//			environment.
//				publishResource(NamingScheme.getDefault().name(UserContainerHandler.NAME), UserContainerHandler.class, UserContainerHandler.path);
		
		LOGGER.info("SCM Harvester Application configuration completed.");
		
		} catch (Exception e) {
			String errorMessage = "SCM Harvester Application Setup failed";
			LOGGER.warn(errorMessage+". Full stacktrace follows: ",e);			
		}		
	}

	@Override
	public void initialize(WriteSession session) throws ApplicationInitializationException {
		LOGGER.info("Initializing SCM Harvester Application...");
		BackendResourcePublisher publisher = new BackendResourcePublisher(session, controller);		
		
		try {
			// publisher.publishUserResources(); moved to the threadePublisher
			publisher.publishHarvesterResources(target);			
			session.saveChanges();
			LOGGER.info("SCM Harvester Application initialization completed.");
			
			LOGGER.info("SCM Harvester: Starting thread for registering branches and commits.");
			BranchCommitPublisherThread branchCommitpublisher = new BranchCommitPublisherThread(controller);
			branchCommitpublisher.start();			
			
			LOGGER.info("SCM Harvester: Starting thread for registering users.");			
			UserPublisherThread userPublisher = new UserPublisherThread(controller);
			userPublisher.start();		
						
		} catch (Exception e) {
			String errorMessage = "SCM Harvester Application initialization failed";
			LOGGER.warn(errorMessage+". Full stacktrace follows: ",e);
			throw new ApplicationInitializationException(e);
		}
	}

	@Override
	public void shutdown() {
		LOGGER.info("Starting *SCM Harvester Application* shutdown...");
//		this.controller.disconnect();
		LOGGER.info("SCM Harvester Application shutdown completed.");
	}

}
