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

import java.util.Date;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendController;

@Resource(id=BranchHandler.ID)
public class BranchHandler implements ResourceHandler {

	public static final String ID="BranchHandler";

	private final BackendController backendController;

	public BranchHandler(final BackendController backendController) {
		this.backendController = backendController;
	}

	@Override
	public DataSet get(final ResourceSnapshot resource) throws UnknownResourceException, ApplicationRuntimeException {
		@SuppressWarnings("unchecked")
		final Name<String> name = (Name<String>)resource.name();

		final BranchKey branchKey=this.backendController.getBranchIdentityMap().getKey(name);

		try{
			final Branch branch= this.backendController.getBranch(branchKey.getRepoId(), branchKey.getBranchId());
			return maptoDataSet(branchKey.getRepoId(), branch,name);
		} catch(final Exception e){
			 throw new ApplicationRuntimeException(e);
		}
	}

	private DataSet maptoDataSet(final String repositoryId, final Branch branch, final Name<String> branchName) {
		final DataSet dataSet=DataSets.createDataSet(branchName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(branchName, BranchHandler.ID).
				property(BranchVocabulary.TYPE).
					withIndividual(BranchVocabulary.BRANCHTYPE).
				property(BranchVocabulary.NAME).
					withLiteral(branch.getName()).
				property(BranchVocabulary.CREATEDON).
					withLiteral(new Date(branch.getCreatedAt()));

		for (final String commitId:branch.getCommits().getCommitIds()){
			final Name<String> commitName = NamingScheme.getDefault().name(repositoryId,commitId);
			helper.
				managedIndividual(branchName, BranchHandler.ID).
					property(BranchVocabulary.HASCOMMIT).
						withIndividual(commitName,CommitHandler.ID).
					property(BranchVocabulary.ISTARGETOF).
						withIndividual(commitName,CommitHandler.ID);
		}

		return dataSet;
	}

}