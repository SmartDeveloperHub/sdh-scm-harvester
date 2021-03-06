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
import org.smartdeveloperhub.harvesters.scm.backend.rest.ConnectionFailedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ControllersITest {

	private static final Logger LOGGER=LoggerFactory.getLogger(ControllersITest.class);

	private static final String GITLAB_ENHANCER = "http://infra3.dia.fi.upm.es:5000/api";

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
		} catch(final ConnectionFailedException e) {
			LOGGER.info("Suspending exploration due to connection failures. Full stacktrace follows",e);
		} catch(final IOException e) {
			LOGGER.error("Exploration failed without reason. Full stacktrace follows",e);
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
		LOGGER.debug("Enhancer[{}]: {}",GITLAB_ENHANCER,enhancer);
		serialize(enhancer);
		return enhancer;
	}

	private void exploreRepositories(final Enhancer enhancer) throws IOException {
		System.out.printf("Exploring repositories...%n");
		final RepositoryController rController=new RepositoryController(GITLAB_ENHANCER);
		for(final String repositoryId:enhancer.getRepositories()) {
			final Repository repository = rController.getRepository(repositoryId);
			traverseRepository(repository);
		}
	}

	private void exploreUsers(final Enhancer enhancer) throws IOException {
		System.out.printf("Exploring users...%n");
		final UserController uController=new UserController(GITLAB_ENHANCER);
		for(final String userId:enhancer.getUsers()) {
			final User user = uController.getUser(userId);
			System.out.printf(" - User %s (%s)...%n",user.getId(),user.getName());
			LOGGER.debug("User[{}]: {}",userId,user);
			serialize(user);
		}
	}

	private void traverseRepository(final Repository repository) throws IOException {
		System.out.printf(" - Exploring repository %s (%s)...%n",repository.getId(),repository.getName());
		LOGGER.debug("Repository[{}]: {}",repository.getId(),repository);
		serialize(repository);
		final BranchController bController=new BranchController(GITLAB_ENHANCER);
		for(final String branchId:repository.getBranches().getBranchIds()) {
			final Branch branch = bController.getBranch(repository.getId(),branchId);
			System.out.printf("   + Branch %s (%s)%n",branchId,branch.getName());
			LOGGER.debug("Branch[{}:{}]: {}",repository.getId(),branchId,branch);
			serialize(branch);
		}
		final CommitController cController=new CommitController(GITLAB_ENHANCER);
		int i=3;
		for(final String commitId:repository.getCommits().getCommitIds()) {
			final Commit commit = cController.getCommit(repository.getId(),commitId);
			System.out.printf("   + Commit %s : %s%n",commitId,firstLine(commit.getTitle()));
			LOGGER.debug("Commit[{}:{}]: {}",repository.getId(),commitId,commit);
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

	private static String firstLine(final String description) {
		if(description==null) {
			return "<NO DESCRIPTION AVAILABLE>";
		}
		final String[] split = description.split("\\n(\\r)?|\\r(\\n)?");
		return split[0];
	}


}
