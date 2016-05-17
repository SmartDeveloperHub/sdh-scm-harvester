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
package org.smartdeveloperhub.harvesters.scm.frontend.core.branch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
import org.ldp4j.application.data.Literal;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.Property;
import org.ldp4j.application.data.Value;
import org.ldp4j.application.data.ValueVisitor;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

import com.google.common.collect.Lists;

@RunWith(JMockit.class)
public class BranchHandlerTest {

	@Injectable	private BackendController controller;

	@Mocked private ResourceSnapshot resource;
	@Mocked private IdentityUtil util;
	@Mocked private BranchKey key;
	@Mocked private Branch entity;

	@Tested
	private BranchHandler sut;

	private Name<BranchKey> branchName() {
		return NamingScheme.getDefault().name(this.key);
	}

	private CommitKey commitKey(final String commitId) {
		return new CommitKey(this.key.getRepoId(), commitId);
	}

	private Name<CommitKey> commitName(final String a) {
		return NamingScheme.getDefault().name(commitKey(a));
	}

	private ManagedIndividualId branchId() {
		return ManagedIndividualId.createId(branchName(), BranchHandler.ID);
	}

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IdentityUtil.branchId(BranchHandlerTest.this.resource);this.result=BranchHandlerTest.this.key;
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			BranchHandlerTest.this.key.getBranchId();this.result="branchId";
			BranchHandlerTest.this.key.getRepoId();this.result="1";
			BranchHandlerTest.this.controller.getBranch("1", "branchId");this.result=BranchHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller, this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet$requiresCreatedAt() throws Exception {
		new Expectations() {{
			BranchHandlerTest.this.entity.getId();this.result=BranchHandlerTest.this.key;
			BranchHandlerTest.this.entity.getCreatedAt();this.result=null;
		}};
		try {
			this.sut.toDataSet(this.entity, this.key);
			fail("Should not return a dataset if a mandatory property is not available");
		} catch (final ApplicationRuntimeException e) {
			assertThat(e.getMessage(),equalTo("Could not create date for property createdOn of branch "+this.entity));
		}
	}

	@Test
	public void testToDataSet$hasCommitsIfAvailable() throws Exception {
		final List<String> commits=Lists.newArrayList("1","2","3","4","5");
		final Commits commitIds=new Commits();
		commitIds.setCommitIds(commits);
		new Expectations() {{
			BranchHandlerTest.this.key.getRepoId();this.result=1;
			IdentityUtil.branchName(BranchHandlerTest.this.key);this.result=branchName();
			IdentityUtil.commitName((CommitKey)this.any);returns(commitName("1"),commitName("2"),commitName("3"),commitName("4"),commitName("5"));
			BranchHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
			BranchHandlerTest.this.entity.getCommits();this.result=commitIds;
		}};

		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final Property property =
			dataSet.
				individualOfId(branchId()).
					property(URI.create(BranchVocabulary.HAS_COMMIT));
		assertThat(property.numberOfValues(),equalTo(commits.size()));
		for(final Value value:property) {
			value.accept(new ValueVisitor() {
				@Override
				public void visitLiteral(final Literal<?> value) {
					fail("A literal is not a valid commit");
				}
				@SuppressWarnings("unchecked")
				@Override
				public void visitIndividual(final Individual<?, ?> value) {
					final ManagedIndividualId id = ((ManagedIndividualId)value.id());
					assertThat(id.managerId(),equalTo(CommitHandler.ID));
					assertThat(((Name<CommitKey>)id.name()).id().getCommitId(),isIn(commits));
				}
			});
		}
	}

	@Test
	public void testToDataSet$doesNotHaveCommitsIfUnavailable() throws Exception {
		new Expectations() {{
			IdentityUtil.branchName(BranchHandlerTest.this.key);this.result=branchName();
			BranchHandlerTest.this.entity.getCreatedAt();this.result=System.currentTimeMillis();
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity, this.key);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		final IndividualHelper value =
			helper.
				managedIndividual(IdentityUtil.branchName(this.key), BranchHandler.ID).
					property(BranchVocabulary.HAS_COMMIT).
						firstIndividual();
		assertThat(value,nullValue());
	}


}
