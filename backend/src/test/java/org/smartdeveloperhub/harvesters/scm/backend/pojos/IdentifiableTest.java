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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.4.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.pojos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Resources;


public class IdentifiableTest {

	private String loadResource(final String resourceName) throws IOException {
		final URL resource = Resources.getResource(resourceName);
		return Resources.toString(resource, Charset.forName("UTF-8"));
	}

	private <T> T deserialize(final String serialization, final Class<? extends T> valueType) throws IOException {
		return this.mapper.readValue(serialization,valueType);
	}

	private String serialize(final Object value) throws IOException {
		return this.mapper.writeValueAsString(value);
	}

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() {
		this.mapper = new ObjectMapper();
		this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Test
	public void testSerialization() throws IOException {
		final Owner original=new Owner();
		original.setId("1");
		original.setType("user");
		original.setAdditionalProperty("strProperty", "value");

		final String serialization = serialize(original);
		System.out.println(serialization);
		final Owner deserialized=
			deserialize(
				serialization,
				Owner.class);

		assertThat(deserialized.getId(),equalTo(original.getId()));
		assertThat(deserialized.getType(),equalTo(original.getType()));
		assertThat(deserialized.getAdditionalProperties(),equalTo(original.getAdditionalProperties()));
	}

	@Test
	public void testDeserialization() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		final Owner readValue=
			deserialize(
				loadResource("owner.json"),
				Owner.class);
		assertThat(readValue.getId(),equalTo("1"));
		assertThat(readValue.getType(),equalTo("user"));
		assertThat(readValue.getAdditionalProperties().size(),equalTo(3));
	}

}