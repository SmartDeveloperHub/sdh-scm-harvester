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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0
 *   Bundle      : scm-harvester-backend-0.3.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.controller;


import java.io.IOException;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.readers.EnhancerReader;
import org.smartdeveloperhub.harvesters.scm.backend.rest.EnhancerClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.RepositoryClient;
import org.smartdeveloperhub.harvesters.scm.backend.rest.UserClient;

public class EnhancerController {

	private final EnhancerClient enhancerClient;
	private final EnhancerReader enhancerReader;

	private final RepositoryClient repositoryClient;

	private final UserClient userClient;

	private final String enhancerEndpoint;

	public EnhancerController(final String enhancerEndpoint){
		this.enhancerEndpoint = enhancerEndpoint;
		this.enhancerClient = new EnhancerClient(enhancerEndpoint);
		this.enhancerReader = new EnhancerReader();
		this.repositoryClient = new RepositoryClient(enhancerEndpoint);
		this.userClient = new UserClient(enhancerEndpoint);
	}

	public Enhancer getEnhancer() throws IOException {
		final String enhancerIS = this.enhancerClient.getEnhancer();
		final String repositoriesIS = this.repositoryClient.getRepositories();
		final String usersIS = this.userClient.getUsers();
		return this.enhancerReader.readEnhancer(this.enhancerEndpoint,enhancerIS,repositoriesIS,usersIS);
	}

}
