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
package org.smartdeveloperhub.harvesters.scm.frontend.core.commit;

import java.util.Date;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

@Resource(id=CommitHandler.ID)
public class CommitHandler implements ResourceHandler {

	public static final String ID="CommitHandler";

	private final BackendController backendController;

	public CommitHandler(final BackendController backendController) {
		this.backendController = backendController;
	}

	@Override
	public DataSet get(final ResourceSnapshot resource) {
		final CommitKey commitKey=IdentityUtil.commitId(resource);
		try{
			final Commit commit= this.backendController.getCommit(commitKey.getRepoId(), commitKey.getCommitId());
			return maptoDataSet(commit,commitKey);
		} catch(final Exception e){
			 throw new ApplicationRuntimeException(e);
		}
	}

	private DataSet maptoDataSet(final Commit commit, final CommitKey key) {
		final Name<CommitKey> commitName = IdentityUtil.commitName(key);
		final Name<String> userName = IdentityUtil.userName(commit.getAuthor());

		final DataSet dataSet=DataSets.createDataSet(commitName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(commitName, CommitHandler.ID).
				property(CommitVocabulary.TYPE).
					withIndividual(CommitVocabulary.ACTION).
					withIndividual(CommitVocabulary.COMMIT).
				property(CommitVocabulary.COMMIT_ID).
					withLiteral(commit.getId()).
				property(CommitVocabulary.CREATED_ON).
					withLiteral(new Date(commit.getCreatedAt())).
				property(CommitVocabulary.PERFORMED_BY).
					withIndividual(userName, UserHandler.ID );

		return dataSet;
	}

}
