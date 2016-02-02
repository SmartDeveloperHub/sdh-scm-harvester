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
package org.smartdeveloperhub.harvesters.scm.backend.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ControllersITest {

	private static final Logger LOGGER=LoggerFactory.getLogger(ControllersITest.class);

	private static final String GITLAB_ENHANCER = "http://russell.dia.fi.upm.es:5000/api";

	private ObjectMapper mapper;

	@Before
	public void setUpMapper() {
		this.mapper=new ObjectMapper();
		this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Test
	public void testConnectivity() {
		assumeThat(isReachable(GITLAB_ENHANCER), equalTo(true));
		try {
			final Enhancer enhancer = discoverEnhancer();
			exploreRepositories(enhancer);
			exploreUsers(enhancer);
		} catch(final IOException e) {
			fail("Should not fail exploration");
		}
	}

	private String serialize(final Object modelObject) throws IOException {
		final String serialization = this.mapper.writeValueAsString(modelObject);
		LOGGER.trace(serialization);
		return serialization;
	}

	private Enhancer discoverEnhancer() throws IOException {
		System.out.printf("Discovering enhancer %s...%n",GITLAB_ENHANCER);
		final EnhancerController eController=new EnhancerController(GITLAB_ENHANCER);
		final Enhancer enhancer=eController.getEnhancer();
		LOGGER.info("Enhancer[{}]: {}",GITLAB_ENHANCER,enhancer);
		serialize(enhancer);
		return enhancer;
	}

	private void exploreRepositories(final Enhancer enhancer) throws IOException {
		System.out.printf("Exploring repositories...%n");
		final RepositoryController rController=new RepositoryController(GITLAB_ENHANCER);
		for(final Integer repositoryId:enhancer.getRepositories()) {
			final Repository repository = rController.getRepository(Integer.toString(repositoryId));
			traverseRepository(repository);
		}
	}

	private void exploreUsers(final Enhancer enhancer) throws IOException {
		System.out.printf("Exploring users...%n");
		final UserController uController=new UserController(GITLAB_ENHANCER);
		for(final String userId:enhancer.getUsers()) {
			final User user = uController.getUser(userId);
			System.out.printf("Exploring user %s (%s)...%n",user.getId(),user.getName());
			LOGGER.info("User[{}]: {}",userId,user);
			serialize(user);
		}
	}

	private void traverseRepository(final Repository repository) throws IOException {
		System.out.printf("Exploring repository %s (%s)...%n",repository.getId(),repository.getName());
		LOGGER.info("Repository[{}]: {}",repository.getId(),repository);
		serialize(repository);
		final BranchController bController=new BranchController(GITLAB_ENHANCER);
		for(final String branchId:repository.getBranches().getBranchIds()) {
			final Branch branch = bController.getBranch(Integer.toString(repository.getId()),branchId);
			LOGGER.info("Branch[{}:{}]: {}",repository.getId(),branchId,branch);
			serialize(branch);
		}
		final CommitController cController=new CommitController(GITLAB_ENHANCER);
		int i=3;
		for(final String commitId:repository.getCommits().getCommitIds()) {
			final Commit commit = cController.getCommit(Integer.toString(repository.getId()),commitId);
			LOGGER.info("Commit[{}:{}]: {}",repository.getId(),commitId,commit);
			serialize(commit);
			if(--i==0) {
				break;
			}
		}
	}

	private boolean isReachable(final String endpoint) {
		try(CloseableHttpClient client = HttpClients.createDefault()) {
			final HttpGet method = new HttpGet(endpoint);
			method.addHeader(HttpHeaders.ACCEPT,"application/json");
			try(CloseableHttpResponse response = client.execute(method)) {
				return
					response.getStatusLine().getStatusCode()==200 &&
					response.getEntity().getContentLength()>0 &&
					!EntityUtils.toString(response.getEntity()).equals("false");
			}
		} catch(final IOException failure) {
			LOGGER.warn("Could not connect to {}. Fullstack trace follow",failure);
			return false;
		}
	}


}
