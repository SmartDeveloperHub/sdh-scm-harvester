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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.commit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

@RunWith(JMockit.class)
public class CommitHandlerTest {

	@Injectable	private BackendController controller;

	@Mocked private ResourceSnapshot resource;
	@Mocked private IdentityUtil util;
	@Mocked private CommitKey key;
	@Mocked private Commit entity;

	@Tested
	private CommitHandler sut;

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IdentityUtil.commitId(CommitHandlerTest.this.resource);this.result=CommitHandlerTest.this.key;
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			CommitHandlerTest.this.key.getCommitId();this.result="commitId";
			CommitHandlerTest.this.key.getRepoId();this.result="1";
			CommitHandlerTest.this.controller.getCommit("1", "commitId");this.result=CommitHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller, this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet$requiresCreatedAt() throws Exception {
		new Expectations() {{
			CommitHandlerTest.this.entity.getId();this.result=CommitHandlerTest.this.key;
			CommitHandlerTest.this.entity.getCreatedAt();this.result=null;
		}};
		try {
			this.sut.toDataSet(this.entity, this.key);
			fail("Should not return a dataset if a mandatory property is not available");
		} catch (final ApplicationRuntimeException e) {
			assertThat(e.getMessage(),equalTo("Could not create date for property createdOn of commit "+this.entity));
		}
	}

	@Test
	public void testToDataSet$hasCreatedOnIfAvailable() throws Exception {
		final long expected = System.currentTimeMillis();
		new Expectations() {{
			CommitHandlerTest.this.entity.getId();this.result=CommitHandlerTest.this.key;
			CommitHandlerTest.this.entity.getCreatedAt();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final DateTime value =
			helper.
				managedIndividual(IdentityUtil.commitName(this.key), CommitHandler.ID).
					property(CommitVocabulary.CREATED_ON).
						firstValue(DateTime.class);
		assertThat(value,notNullValue());
		assertThat(value.toDate().getTime(),equalTo(expected));
	}


}
