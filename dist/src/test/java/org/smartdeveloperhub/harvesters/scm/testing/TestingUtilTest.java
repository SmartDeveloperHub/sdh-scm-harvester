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
package org.smartdeveloperhub.harvesters.scm.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

@RunWith(JMockit.class)
public class TestingUtilTest {

	@Mocked Response response;

	@Test
	public void testAsModelResponseString() throws Exception {
		new Expectations() {{
			TestingUtilTest.this.response.asString();
			this.result=
				"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"+
				"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"+
				"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"+
				"@prefix uri:   <bad\\uri> .";
		}};
		try {
			TestingUtil.asModel(this.response, "http://localhost:8080/iri");
			fail("Should fail on bad data");
		} catch (final AssertionError e) {
			assertThat(e.getMessage(),equalTo("Could not parse response for http://localhost:8080/iri as Turtle RDF data"));
		}
	}

}
