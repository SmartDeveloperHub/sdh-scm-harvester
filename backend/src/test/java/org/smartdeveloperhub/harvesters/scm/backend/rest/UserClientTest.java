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
package org.smartdeveloperhub.harvesters.scm.backend.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class UserClientTest {

	@Mocked CloseableHttpClient client;
	@Mocked CloseableHttpResponse response;
	@Mocked StatusLine statusLine;
	@Mocked HttpEntity entity;

	private String responseBody;
	private int retries;

	@Test
	public void testGetUser$happyPath() throws Exception {
		setUpHappyPath("result");
		final UserClient sut = new UserClient("http://www.example.org/api");
		final String result  = sut.getUser("id");
		verifyHappyPath(result,"http://www.example.org/api/users/id");
	}

	@Test
	public void testGetUser$retryOnThrottling() throws Exception {
		setUpThrottling("result");
		final UserClient sut = new UserClient("http://www.example.org/api");
		final String result  = sut.getUser("id");
		verifyRetries(result,"http://www.example.org/api/users/id");
	}

	@Test
	public void testGetUser$retryOnServiceUnavailable() throws Exception {
		setUpServiceUnavailable("result");
		final UserClient sut = new UserClient("http://www.example.org/api");
		final String result  = sut.getUser("id");
		verifyRetries(result,"http://www.example.org/api/users/id");
	}

	@Test
	public void testGetUser$retryOnGatewayTimeout() throws Exception {
		setUpGatewayTimeout("result");
		final UserClient sut = new UserClient("http://www.example.org/api");
		final String result  = sut.getUser("id");
		verifyRetries(result,"http://www.example.org/api/users/id");
	}

	@Test
	public void testGetUser$retryOnNullResponse() throws Exception {
		setUpNullResponse("result");
		final UserClient sut = new UserClient("http://www.example.org/api");
		final String result  = sut.getUser("id");
		verifyRetries(result,"http://www.example.org/api/users/id");
	}

	@Test
	public void testGetUser$failIfUnretriableServiceResponse() throws Exception {
		setUpUnretriableFailure();
		final UserClient sut = new UserClient("http://www.example.org/api");
		try {
			sut.getUser("id");
			fail("Should fail on unretriable service response");
		} catch (final ServiceFailureException e) {
			verifyUnretriableFailure(e,"http://www.example.org/api/users/id");
		}
	}

	@Test
	public void testGetUser$failIfServiceFailure() throws Exception {
		setUpServiceFailure();
		final UserClient sut = new UserClient("http://www.example.org/api");
		try {
			sut.getUser("id");
			fail("Should fail on service failure");
		} catch (final ServiceFailureException e) {
			verifyServiceFailure(e,"http://www.example.org/api/users/id");
		}
	}

	@Test
	public void testGetUser$failIfMaxRetriesExceeded() throws Exception {
		setUpRetries(null,429,503,504,504,200);
		final UserClient sut = new UserClient("http://www.example.org/api");
		try {
			sut.getUser("id");
			fail("Should fail on service failure");
		} catch (final ServiceFailureException e) {
			fail("Should not throw a service failure exception");
		} catch (final IOException e) {
			verifyRetries(null, "http://www.example.org/api/users/id");
		}
	}

	private void setUpHappyPath(final String responseBody) throws IOException {
		this.responseBody=responseBody;
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return UserClientTest.this.client;
			}
		};
		new MockUp<EntityUtils>() {
			@Mock
			String toString(final HttpEntity entity) throws IOException, ParseException {
				assertThat(entity,sameInstance(entity));
				return responseBody;
			}
		};
		new Expectations(){{
			UserClientTest.this.client.execute((HttpGet)this.any);this.result=UserClientTest.this.response;
			UserClientTest.this.response.getStatusLine();this.result=UserClientTest.this.statusLine;
			UserClientTest.this.statusLine.getStatusCode();this.result=200;
			UserClientTest.this.response.getEntity();this.result=UserClientTest.this.entity;
		}};
	}

	private void setUpNullResponse(final String responseBody) throws IOException {
		this.responseBody=responseBody;
		this.retries=2;
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return UserClientTest.this.client;
			}
		};
		new MockUp<EntityUtils>() {
			@Mock
			String toString(final HttpEntity entity) throws IOException, ParseException {
				assertThat(entity,sameInstance(entity));
				return responseBody;
			}
		};
		new Expectations(){{
			UserClientTest.this.client.execute((HttpGet)this.any);this.result=UserClientTest.this.response;
			UserClientTest.this.response.getStatusLine();this.result=UserClientTest.this.statusLine;
			UserClientTest.this.statusLine.getStatusCode();this.result=200;
			UserClientTest.this.response.getEntity();returns(null,UserClientTest.this.entity);
		}};
	}

	private void setUpThrottling(final String responseBody) throws IOException {
		setUpRetries(responseBody,429,429,200);
	}

	private void setUpServiceUnavailable(final String responseBody) throws IOException {
		setUpRetries(responseBody,503,503,200);
	}

	private void setUpGatewayTimeout(final String responseBody) throws IOException {
		setUpRetries(responseBody,504,504,200);
	}

	private void setUpUnretriableFailure() throws IOException {
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return UserClientTest.this.client;
			}
		};
		new Expectations(){{
			UserClientTest.this.client.execute((HttpGet)this.any);this.result=UserClientTest.this.response;
			UserClientTest.this.response.getStatusLine();this.result=UserClientTest.this.statusLine;
			UserClientTest.this.statusLine.getStatusCode();this.result=404;
		}};
	}

	private void setUpServiceFailure() throws IOException {
		setUpHappyPath("false");
	}

	private void setUpRetries(final String responseBody, final int... args) throws IOException {
		this.responseBody=responseBody;
		this.retries=args.length;
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return UserClientTest.this.client;
			}
		};
		new MockUp<EntityUtils>() {
			@Mock
			String toString(final HttpEntity entity) throws IOException, ParseException {
				assertThat(entity,sameInstance(entity));
				return responseBody;
			}
		};
		new Expectations(){{
			UserClientTest.this.client.execute((HttpGet)this.any);this.result=UserClientTest.this.response;
			UserClientTest.this.response.getStatusLine();this.result=UserClientTest.this.statusLine;
			UserClientTest.this.statusLine.getStatusCode();returns(args);
			UserClientTest.this.response.getEntity();this.result=UserClientTest.this.entity;
		}};
	}

	private void verifyHappyPath(final String result, final String resourcePath) throws IOException {
		assertThat(result,equalTo(this.responseBody));
		new Verifications() {{
			HttpGet method;
			UserClientTest.this.client.execute(method=withCapture());
			assertThat(method.getURI().toString(),equalTo(resourcePath));
		}};
	}

	private void verifyUnretriableFailure(final ServiceFailureException e, final String resourcePath) throws IOException {
		verifyFailure(e, resourcePath, 404);
	}

	private void verifyServiceFailure(final ServiceFailureException e, final String resourcePath) throws IOException {
		verifyFailure(e, resourcePath, 200);
	}

	private void verifyFailure(final ServiceFailureException e, final String resourcePath, final int statusCode) throws IOException {
		assertThat(e.getStatus(),equalTo(statusCode));
		assertThat(e.getResource(),equalTo(resourcePath));
		new Verifications() {{
			HttpGet method;
			UserClientTest.this.client.execute(method=withCapture());
			assertThat(method.getURI().toString(),equalTo(resourcePath));
			final Header[] accept = method.getHeaders(HttpHeaders.ACCEPT);
			assertThat(accept.length,equalTo(1));
			assertThat(accept[0].getValue(),equalTo("application/json"));
		}};
	}

	private void verifyRetries(final String result, final String resourcePath) throws IOException {
		assertThat(result,equalTo(this.responseBody));
		for(int i=0;i<UserClientTest.this.retries;i++) {
			new Verifications() {{
					HttpGet method;
					UserClientTest.this.client.execute(method=withCapture());
					assertThat(method.getURI().toString(),equalTo(resourcePath));
			}};
		}
	}

}
