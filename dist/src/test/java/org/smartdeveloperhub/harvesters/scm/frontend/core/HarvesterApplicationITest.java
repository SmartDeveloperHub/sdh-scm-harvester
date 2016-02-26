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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-dist:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-dist-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assume.assumeThat;
import static org.smartdeveloperhub.testing.hamcrest.RDFMatchers.hasTriple;
import static org.smartdeveloperhub.testing.hamcrest.References.property;
import static org.smartdeveloperhub.testing.hamcrest.References.typedLiteral;
import static org.smartdeveloperhub.testing.hamcrest.References.uriRef;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.testing.QueryHelper;
import org.smartdeveloperhub.harvesters.scm.testing.QueryHelper.ResultProcessor;
import org.smartdeveloperhub.harvesters.scm.testing.SmokeTest;
import org.smartdeveloperhub.harvesters.scm.testing.TestingService;
import org.smartdeveloperhub.harvesters.scm.testing.TestingUtil;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.GitLabEnhancer.UpdateReport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.jayway.restassured.response.Response;

@RunWith(Arquillian.class)
public class HarvesterApplicationITest {

	private static TestingService service;

	@Deployment(name="default",testable=false)
	@TargetsContainer("tomcat")
	public static WebArchive createDeployment() throws Exception {
		startMockService();
		return SmokeTest.createWebArchive("default-harvester.war");
	}

	private static void startMockService() throws IOException {
		final String property = System.getProperty("undertow.http.port","8080");
		final int port = Integer.parseInt(property);
		System.out.println("Publishing mock GitLab Enhancer Service at port "+port);
		service =
			TestingService.
				builder().
					port(port).
					build().
						start();
	}

	@AfterClass
	public static void tearDown() {
		service.shutdown();
	}

	@Test
	@OperateOnDeployment("default")
	public void testCommitterCreation(@ArquillianResource final URL contextURL) throws Exception {
		final List<String> originalCommitters = getCommitters(contextURL);
		final CommitterCreatedEvent event = new CommitterCreatedEvent();
		final String id = "1234";
		event.getNewCommitters().add(id);
		final UpdateReport report = service.update(event);
		assumeThat(report.notificationSent(),equalTo(true));
		System.out.println("Created committer 1234. Awaiting frontend update");
		TimeUnit.SECONDS.sleep(10);
		System.out.println("Verifying id...");
		final List<String> newCommitters = Lists.newArrayList(getCommitters(contextURL));
		newCommitters.removeAll(originalCommitters);
		assertThat(newCommitters,hasSize(1));
		commiterHasIdentifier(newCommitters.get(0),id);
	}

	private void commiterHasIdentifier(final String committer, final String id) {
		final Response response=
				given().
					accept(TEXT_TURTLE).
					baseUri(committer).
				expect().
					statusCode(OK).
					contentType(TEXT_TURTLE).
				when().
					get();
		final Model model = TestingUtil.asModel(response,committer);
		assertThat(
			model,
			hasTriple(
				uriRef(committer),
				property("http://www.smartdeveloperhub.org/vocabulary/scm#committerId"),
				typedLiteral(id,"http://www.w3.org/2001/XMLSchema#string")));
	}

	protected static final String TEXT_TURTLE = "text/turtle";
	protected static final int    OK          = 200;

	protected static final String SERVICE     = "ldp4j/api/service/";

	protected final List<String> getCommitters(final URL contextURL) throws IOException {
		final Response response=
			given().
				accept(TEXT_TURTLE).
				baseUri(contextURL.toString()).
			expect().
				statusCode(OK).
				contentType(TEXT_TURTLE).
			when().
				get(SERVICE);

		return
			QueryHelper.
				newInstance().
					withModel(
						TestingUtil.
							asModel(response,contextURL,SERVICE)).
					withQuery().
						fromResource("queries/committers.sparql").
						withURIRefParam("service",TestingUtil.resolve(contextURL,SERVICE)).
					select(
						new ResultProcessor<List<String>>() {
							private final List<String> builds=Lists.newArrayList();
							@Override
							protected void processSolution() {
								this.builds.add(resource("committer").getURI());
							}
							@Override
							public List<String> getResult() {
								return ImmutableList.copyOf(this.builds);
							}
						}
					);
	}

}