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
package org.smartdeveloperhub.harvesters.scm.frontend.core.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isIn;
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.LanguageLiteral;
import org.ldp4j.application.data.Literal;
import org.ldp4j.application.data.LiteralVisitor;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.Property;
import org.ldp4j.application.data.RelativeIndividualId;
import org.ldp4j.application.data.TypedLiteral;
import org.ldp4j.application.data.Value;
import org.ldp4j.application.data.ValueVisitor;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

import com.google.common.collect.Lists;

@RunWith(JMockit.class)
public class UserHandlerTest {

	@Injectable	private BackendController controller;

	@Mocked private ResourceSnapshot resource;
	@Mocked private IdentityUtil util;
	@Mocked private User entity;

	private final String key="key";

	@Tested
	private UserHandler sut;

	private Name<String> userName() {
		return NamingScheme.getDefault().name(this.key);
	}

	private ManagedIndividualId userId() {
		return ManagedIndividualId.createId(userName(), UserHandler.ID);
	}

	private RelativeIndividualId avatarId() {
		return RelativeIndividualId.createId(userId(),URI.create("#img"));
	}

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IdentityUtil.userId(UserHandlerTest.this.resource);this.result=UserHandlerTest.this.key;
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			UserHandlerTest.this.controller.getUser(UserHandlerTest.this.key);this.result=UserHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller, this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet$hasEmailsIfAvailable() throws Exception {
		final List<String> emails=Lists.newArrayList("email1@test.com","email2@test.com");
		new Expectations() {{
			IdentityUtil.userName(UserHandlerTest.this.key);this.result=userName();
			UserHandlerTest.this.entity.getId();this.result=UserHandlerTest.this.key;
			UserHandlerTest.this.entity.getEmails();this.result=emails;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final Property property =
			dataSet.
				individualOfId(userId()).
					property(URI.create(UserVocabulary.MBOX));
		assertThat(property.numberOfValues(),equalTo(emails.size()));
		for(final Value value:property) {
			value.accept(new ValueVisitor() {
				@Override
				public void visitLiteral(final Literal<?> value) {
					value.accept(new LiteralVisitor() {
						@Override
						public void visitTypedLiteral(final TypedLiteral<?> literal) {
							fail("A typed literal is not a valid email");
						}
						@Override
						public void visitLiteral(final Literal<?> literal) {
							assertThat(literal.get(),instanceOf(String.class));
							assertThat((String)literal.get(),isIn(emails));
						}
						@Override
						public void visitLanguageLiteral(final LanguageLiteral literal) {
							fail("A language literal is not a valid email");
						}
					});
				}
				@Override
				public void visitIndividual(final Individual<?, ?> value) {
					fail("An individual is not a valid email");
				}
			});
		}
	}

	@Test
	public void testToDataSet$doesNotHaveEmailsIfUnavailable() throws Exception {
		new Expectations() {{
			IdentityUtil.userName(UserHandlerTest.this.key);this.result=userName();
			UserHandlerTest.this.entity.getId();this.result=UserHandlerTest.this.key;
			UserHandlerTest.this.entity.getEmails();this.result=Lists.newArrayList();
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final Property value =
			dataSet.
				individualOfId(userId()).
					property(URI.create(UserVocabulary.MBOX));
		assertThat(value,nullValue());
	}

	@Test
	public void testToDataSet$hasAvatarIfAvailable() throws Exception {
		final String expected = "http://www.example.org/avatar";
		new Expectations() {{
			IdentityUtil.userName(UserHandlerTest.this.key);this.result=userName();
			UserHandlerTest.this.entity.getId();this.result=UserHandlerTest.this.key;
			UserHandlerTest.this.entity.getAvatarUrl();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final Property depiction =
			dataSet.
				individualOfId(userId()).
					property(URI.create(UserVocabulary.IMG));
		assertThat(depiction.numberOfValues(),equalTo(1));
		depiction.hasIdentifiedIndividual(avatarId());
		final Property image =
			dataSet.
				individualOfId(avatarId()).
					property(URI.create(UserVocabulary.DEPICTS));
		assertThat(image.numberOfValues(),equalTo(1));
		image.hasIdentifiedIndividual(URI.create(expected));
	}

	@Test
	public void testToDataSet$doesNotHaveAvatarIfUnavailable() throws Exception {
		new Expectations() {{
			IdentityUtil.userName(UserHandlerTest.this.key);this.result=userName();
			UserHandlerTest.this.entity.getId();this.result=UserHandlerTest.this.key;
			UserHandlerTest.this.entity.getAvatarUrl();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final IndividualHelper value =
			helper.
				managedIndividual(IdentityUtil.userName(this.key), UserHandler.ID).
					property(UserVocabulary.IMG).
						firstIndividual();
		assertThat(value,nullValue());
	}

}
