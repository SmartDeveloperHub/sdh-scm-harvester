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
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import java.io.IOException;
import java.util.List;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class EnhancerReader {

	private final ObjectMapper mapper = new ObjectMapper();

	private final RepositoryReader repositoryReader;
	private final UserReader userReader;

	public EnhancerReader() {
		this.repositoryReader = new RepositoryReader();
		this.userReader = new UserReader();
	}

	public Enhancer readEnhancer(final String enhancerId, final String enhancerIS, final String repositoriesIS, final String usersIS) throws IOException {
		final Enhancer enhancer = this.mapper.readValue(enhancerIS, Enhancer.class);
		final Repositories reposistories = this.repositoryReader.readReposistories(repositoriesIS);
		final List<String> userIds = this.userReader.readUsers(usersIS);
		enhancer.setId(enhancerId);
		enhancer.setRepositories(reposistories.getRepositoryIds());
		enhancer.setUsers(userIds);
		return enhancer;
	}

}
