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
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ReaderTestHelper {

	private ObjectMapper mapper;

	private boolean enableLog=false;

	protected final void showSerializations(final boolean enable) {
		this.enableLog = enable;
	}

	@Before
	public void setUpMapper() {
		this.mapper=new ObjectMapper();
		this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	protected final String serialize(final Object modelObject) throws IOException {
		final String serialization = this.mapper.writeValueAsString(modelObject);
		if(this.enableLog) {
			System.out.println(serialization);
		}
		return serialization;
	}

	protected final <T> String serializeList(final List<T> values) throws IOException {
		final String serialization = this.mapper.writeValueAsString(values);
		if(this.enableLog) {
			System.out.println(serialization);
		}
		return serialization;
	}

	protected final void verifyCommits(final Commits defaultCommits, final Commits readCommits) {
		assertThat(readCommits.getCommitIds(),equalTo(defaultCommits.getCommitIds()));
	}

}