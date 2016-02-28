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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.testing.LDPUtil;
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

	private static final String SERVICE = "ldp4j/api/service/";

	private static TestingService service;

	@Rule
	public TestName test=new TestName();

	@Deployment(name="default",testable=false)
	@TargetsContainer("tomcat")
	public static WebArchive createDeployment() throws Exception {
		service=startMockService();
		return SmokeTest.createWebArchive("default-harvester.war");
	}

	private static TestingService startMockService() throws IOException {
		final String property = System.getProperty("undertow.http.port","8080");
		return
			TestingService.
				builder().
					port(Integer.parseInt(property)).
					build().
						start();
	}

	@AfterClass
	public static void tearDown() {
		if(service!=null) {
			service.shutdown();
		}
	}

	@Test
	@OperateOnDeployment("default")
	public void testCommitterCreation(@ArquillianResource final URL contextURL) throws Exception {
		final List<String> originalCommitters = getCommitters(contextURL);
		final CommitterCreatedEvent event = new CommitterCreatedEvent();
		final String id = this.test.getMethodName();
		event.getNewCommitters().add(id);
		final UpdateReport report = service.update(event);
		assumeThat(report.notificationSent(),equalTo(true));
		System.out.println("Created committer "+id+". Awaiting frontend update");
		TimeUnit.SECONDS.sleep(5);
		System.out.println("Verifying committer id...");
		final List<String> newCommitters = Lists.newArrayList(getCommitters(contextURL));
		newCommitters.removeAll(originalCommitters);
		assertThat(newCommitters,hasSize(1));
		commiterHasIdentifier(newCommitters.get(0),id);
	}

	@Test
	@OperateOnDeployment("default")
	public void testRepositoryCreation(@ArquillianResource final URL contextURL) throws Exception {
		final CommitterCreatedEvent event = new CommitterCreatedEvent();
		event.getNewCommitters().add(this.test.getMethodName());
		final UpdateReport report = service.update(event);
		assumeThat(report.notificationSent(),equalTo(true));

		final List<String> originalRepositories = getRepositories(contextURL);
		final RepositoryCreatedEvent rEvent=new RepositoryCreatedEvent();
		rEvent.getNewRepositories().add(1);
		final UpdateReport rReport = service.update(rEvent);
		assumeThat(rReport.notificationSent(),equalTo(true));

		System.out.println("Created repository 1. Awaiting frontend update");
		TimeUnit.SECONDS.sleep(5);
		System.out.println("Verifying repository id...");

		final List<String> newRepositories = Lists.newArrayList(getRepositories(contextURL));
		newRepositories.removeAll(originalRepositories);
		assertThat(newRepositories,hasSize(1));
		repositoryHasIdentifier(newRepositories.get(0),1);
	}

	private void commiterHasIdentifier(final String committer, final String id) {
		final Response response = LDPUtil.assertIsAccessible(committer);
		final Model model = TestingUtil.asModel(response,committer);
		assertThat(
			model,
			hasTriple(
				uriRef(committer),
				property("http://www.smartdeveloperhub.org/vocabulary/scm#committerId"),
				typedLiteral(id,"http://www.w3.org/2001/XMLSchema#string")));
	}

	private void repositoryHasIdentifier(final String resource, final Integer id) {
		final Response response = LDPUtil.assertIsAccessible(resource);
		final Model model = TestingUtil.asModel(response,resource);
		assertThat(
			model,
			hasTriple(
				uriRef(resource),
				property("http://www.smartdeveloperhub.org/vocabulary/scm#repositoryId"),
				typedLiteral(id.toString(),"http://www.w3.org/2001/XMLSchema#string")));
	}

	private static final List<String> getCommitters(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,SERVICE), "queries/committers.sparql", "committer");
	}

	private static final List<String> getRepositories(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,SERVICE), "queries/repositories.sparql", "repository");
	}

	private static List<String> queryResourceVariable(final String resource, final String query, final String variable) throws IOException {
		return
			QueryHelper.
				newInstance().
					withModel(
						TestingUtil.
							asModel(
								LDPUtil.assertIsAccessible(resource),
								resource)).
					withQuery().
						fromResource(query).
						withURIRefParam("service",resource).
					select(
						new ResultProcessor<List<String>>() {
							private final List<String> bindings=Lists.newArrayList();
							@Override
							protected void processSolution() {
								this.bindings.add(resource(variable).getURI());
							}
							@Override
							public List<String> getResult() {
								return ImmutableList.copyOf(this.bindings);
							}
						}
					);
	}

}