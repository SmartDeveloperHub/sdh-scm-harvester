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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.List;

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
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Literal;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.Property;
import org.ldp4j.application.data.RelativeIndividualId;
import org.ldp4j.application.data.Value;
import org.ldp4j.application.data.ValueVisitor;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

import com.google.common.collect.Lists;

@RunWith(JMockit.class)
public class RepositoryHandlerTest {

	@Injectable	private BackendController controller;

	@Mocked private ResourceSnapshot resource;
	@Mocked private IdentityUtil util;
	@Mocked private Repository entity;

	private final String key="1";

	@Tested
	private RepositoryHandler sut;

	private Name<String> repositoryName() {
		return NamingScheme.getDefault().name(RepositoryHandlerTest.this.key);
	}

	private ManagedIndividualId repositoryId() {
		return ManagedIndividualId.createId(repositoryName(), RepositoryHandler.ID);
	}

	private RelativeIndividualId avatarId() {
		return RelativeIndividualId.createId(repositoryId(),URI.create("#depiction"));
	}

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IdentityUtil.repositoryId(RepositoryHandlerTest.this.resource);this.result=RepositoryHandlerTest.this.key;
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.controller.getRepository(RepositoryHandlerTest.this.key);this.result=RepositoryHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller, this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet$requiresCreatedAt() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=null;
		}};
		try {
			this.sut.toDataSet(this.entity, this.key);
			fail("Should not return a dataset if a mandatory property is not available");
		} catch (final ApplicationRuntimeException e) {
			assertThat(e.getMessage(),equalTo("Could not create date for property createdOn of repository "+this.entity));
		}
	}

	@Test
	public void testToDataSet$hasFirstCommitAtIfAvailable() throws Exception {
		final long expected = System.currentTimeMillis();
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=expected;
			RepositoryHandlerTest.this.entity.getFirstCommitAt();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final DateTime value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.FIRST_COMMIT).
						firstValue(DateTime.class);
		assertThat(value,notNullValue());
		assertThat(value.toDate().getTime(),equalTo(expected));
	}

	@Test
	public void testToDataSet$doesNotHaveFirstCommitAtIfUnavailable() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getFirstCommitAt();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final DateTime value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.FIRST_COMMIT).
						firstValue(DateTime.class);
		assertThat(value,nullValue());
	}

	@Test
	public void testToDataSet$hasLastCommitAtIfAvailable() throws Exception {
		final long expected = System.currentTimeMillis();
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=expected;
			RepositoryHandlerTest.this.entity.getLastCommitAt();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final DateTime value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.LAST_COMMIT).
						firstValue(DateTime.class);
		assertThat(value,notNullValue());
		assertThat(value.toDate().getTime(),equalTo(expected));
	}

	@Test
	public void testToDataSet$doesNotHaveLastCommitAtIfUnavailable() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getLastCommitAt();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final DateTime value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.LAST_COMMIT).
						firstValue(DateTime.class);
		assertThat(value,nullValue());
	}

	@Test
	public void testToDataSet$hasIsPublicIfAvailable() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getPublic();this.result=Boolean.TRUE.toString();
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final Boolean value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.PUBLIC).
						firstValue(Boolean.class);
		assertThat(value,notNullValue());
		assertThat(value,equalTo(true));
	}

	@Test
	public void testToDataSet$doesNotHaveIsPublicIfUnavailable() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getPublic();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final Boolean value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.PUBLIC).
						firstValue(Boolean.class);
		assertThat(value,nullValue());
	}

	@Test
	public void testToDataSet$hasIsArchivedIfAvailable$positive() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getState();this.result="archived";
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final Boolean value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.ARCHIVED).
						firstValue(Boolean.class);
		assertThat(value,notNullValue());
		assertThat(value,equalTo(true));
	}

	@Test
	public void testToDataSet$hasIsArchivedIfAvailable$negative() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getState();this.result="active";
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final Boolean value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.ARCHIVED).
						firstValue(Boolean.class);
		assertThat(value,notNullValue());
		assertThat(value,equalTo(false));
	}

	@Test
	public void testToDataSet$doesNotHaveIsArchivedIfUnavailable() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getState();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final Boolean value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.ARCHIVED).
						firstValue(Boolean.class);
		assertThat(value,nullValue());
	}

	@Test
	public void testToDataSet$hasContributorsIfAvailable() throws Exception {
		final List<String> contributors=Lists.newArrayList("1","2","3","4","5");
		new Expectations() {{
			IdentityUtil.repositoryName(RepositoryHandlerTest.this.key);this.result=repositoryName();
			IdentityUtil.userName("1");this.result=NamingScheme.getDefault().name("1");
			IdentityUtil.userName("2");this.result=NamingScheme.getDefault().name("2");
			IdentityUtil.userName("3");this.result=NamingScheme.getDefault().name("3");
			IdentityUtil.userName("4");this.result=NamingScheme.getDefault().name("4");
			IdentityUtil.userName("5");this.result=NamingScheme.getDefault().name("5");
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getContributors();this.result=contributors;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final Property property =
			dataSet.
				individualOfId(repositoryId()).
					property(URI.create(RepositoryVocabulary.DEVELOPER));
		assertThat(property.numberOfValues(),equalTo(contributors.size()));
		for(final Value value:property) {
			value.accept(new ValueVisitor() {
				@Override
				public void visitLiteral(final Literal<?> value) {
					fail("A literal is not a valid developer");
				}
				@Override
				public void visitIndividual(final Individual<?, ?> value) {
					final ManagedIndividualId id = ((ManagedIndividualId)value.id());
					assertThat(id.managerId(),equalTo(UserHandler.ID));
					assertThat((String)id.name().id(),isIn(contributors));
				}
			});
		}
	}

	@Test
	public void testToDataSet$doesNotHaveContributorsIfUnavailable() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getContributors();this.result=Lists.newArrayList();
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final IndividualHelper value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.DEVELOPER).
						firstIndividual();
		assertThat(value,nullValue());
	}

	@Test
	public void testToDataSet$hasAvatarIfAvailable() throws Exception {
		final String expected = "http://www.example.org/avatar";
		new Expectations() {{
			IdentityUtil.repositoryName(RepositoryHandlerTest.this.key);this.result=repositoryName();
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getAvatarUrl();this.result=expected;
		}};

		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final Property depiction =
			dataSet.
				individualOfId(repositoryId()).
					property(URI.create(RepositoryVocabulary.DEPICTION));
		assertThat(depiction.numberOfValues(),equalTo(1));
		depiction.hasIdentifiedIndividual(avatarId());
		final Property image =
			dataSet.
				individualOfId(avatarId()).
					property(URI.create(RepositoryVocabulary.DEPICTS));
		assertThat(image.numberOfValues(),equalTo(1));
		image.hasIdentifiedIndividual(URI.create(expected));
	}

	@Test
	public void testToDataSet$doesNotHaveAvatarIfUnavailable() throws Exception {
		new Expectations() {{
			RepositoryHandlerTest.this.entity.getId();this.result=RepositoryHandlerTest.this.key;
			RepositoryHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			RepositoryHandlerTest.this.entity.getAvatarUrl();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final IndividualHelper value =
			helper.
				managedIndividual(IdentityUtil.repositoryName(this.key), RepositoryHandler.ID).
					property(RepositoryVocabulary.DEPICTION).
						firstIndividual();
		assertThat(value,nullValue());
	}

}
