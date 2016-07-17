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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.branch;

import java.io.IOException;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.AbstractEntityResourceHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

@Resource(id=BranchHandler.ID)
public final class BranchHandler extends AbstractEntityResourceHandler<Branch,BranchKey> {

	public static final String ID="BranchHandler";

	public BranchHandler(final BackendController backendController) {
		super(backendController);
	}

	@Override
	protected BranchKey getId(final ResourceSnapshot resource) {
		return IdentityUtil.branchId(resource);
	}

	@Override
	protected Branch getEntity(final BackendController controller, final BranchKey key) throws IOException {
		return controller.getBranch(key.getRepoId(),key.getBranchId());
	}

	@Override
	protected DataSet toDataSet(final Branch branch, final BranchKey key) {
		final Name<BranchKey> branchName=IdentityUtil.branchName(key);

		final DataSet dataSet=DataSets.createDataSet(branchName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(branchName, BranchHandler.ID).
				property(BranchVocabulary.TYPE).
					withIndividual(BranchVocabulary.BRANCH_TYPE).
				property(BranchVocabulary.NAME).
					withLiteral(branch.getName()).
				property(BranchVocabulary.CREATED_ON).
					withLiteral(toDate(branch.getCreatedAt(),true,"createdOn",branch).get());

		for (final String commitId:branch.getCommits().getCommitIds()){
			final Name<CommitKey> commitName = IdentityUtil.commitName(new CommitKey(key.getRepoId(),commitId));
			helper.
				managedIndividual(branchName, BranchHandler.ID).
					property(BranchVocabulary.HAS_COMMIT).
						withIndividual(commitName,CommitHandler.ID).
					property(BranchVocabulary.IS_TARGET_OF).
						withIndividual(commitName,CommitHandler.ID);
		}

		return dataSet;
	}

}