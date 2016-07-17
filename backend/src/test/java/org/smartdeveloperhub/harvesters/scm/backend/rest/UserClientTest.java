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
package org.smartdeveloperhub.harvesters.scm.backend.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.SocketTimeoutException;

import mockit.integration.junit4.JMockit;

import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class UserClientTest extends ClientTestHelper {

	private static final String BASE = "http://www.example.org/api";

	private UserClient sut;

	@Before
	public void setUp() {
		this.sut = new UserClient(BASE);
	}

	@Test
	public void testGetUsers$happyPath() throws Exception {
		setUpHappyPath("result");
		final String result  = this.sut.getUsers();
		verifyHappyPath(result,BASE+"/users");
	}

	@Test
	public void testGetUser$happyPath() throws Exception {
		setUpHappyPath("result");
		final String result  = this.sut.getUser("id");
		verifyHappyPath(result,BASE+"/users/id");
	}

	@Test
	public void testGetUser$retryOnThrottling() throws Exception {
		setUpThrottling("result");
		final String result  = this.sut.getUser("id");
		verifyRetries(result,BASE+"/users/id");
	}

	@Test
	public void testGetUser$retryOnServiceUnavailable() throws Exception {
		setUpServiceUnavailable("result");
		final String result  = this.sut.getUser("id");
		verifyRetries(result,BASE+"/users/id");
	}

	@Test
	public void testGetUser$retryOnGatewayTimeout() throws Exception {
		setUpGatewayTimeout("result");
		final String result  = this.sut.getUser("id");
		verifyRetries(result,BASE+"/users/id");
	}

	@Test
	public void testGetUser$retryOnNullResponse() throws Exception {
		setUpNullResponse("result");
		final String result  = this.sut.getUser("id");
		verifyRetries(result,BASE+"/users/id");
	}

	@Test
	public void testGetUser$failIfUnretriableServiceResponse() throws Exception {
		setUpUnretriableFailure();
		try {
			this.sut.getUser("id");
			fail("Should fail on unretriable service response");
		} catch (final ServiceFailureException e) {
			verifyUnretriableFailure(e,BASE+"/users/id");
		}
	}

	@Test
	public void testGetUser$failIfServiceFailure() throws Exception {
		setUpServiceFailure();
		try {
			this.sut.getUser("id");
			fail("Should fail on service failure");
		} catch (final ServiceFailureException e) {
			verifyServiceFailure(e,BASE+"/users/id");
		}
	}

	@Test
	public void testGetUser$failIfMaxRetriesExceeded() throws Exception {
		setUpRetries(null,429,503,504,504,200);
		try {
			this.sut.getUser("id");
			fail("Should fail on service failure");
		} catch (final ServiceFailureException e) {
			fail("Should not throw a service failure exception");
		} catch (final IOException e) {
			verifyRetries(null, BASE+"/users/id");
		}
	}

	@Test
	public void testGetUser$failIfConnectionTimesOut() throws Exception {
		setUpConnectionTimeOut();
		try {
			this.sut.getUser("id");
			fail("Should fail on connection timeout");
		} catch (final ConnectionFailedException e) {
			verifyConnectionFailure(e,BASE+"/users/id");
			assertThat(e.getCause(),instanceOf(ConnectTimeoutException.class));
		}
	}

	@Test
	public void testGetUser$failIfSocketTimesOut() throws Exception {
		setUpReadTimeOut();
		try {
			this.sut.getUser("id");
			fail("Should fail on socket read time out");
		} catch (final ConnectionFailedException e) {
			verifyConnectionFailure(e,BASE+"/users/id");
			assertThat(e.getCause(),instanceOf(SocketTimeoutException.class));
		}
	}
}
