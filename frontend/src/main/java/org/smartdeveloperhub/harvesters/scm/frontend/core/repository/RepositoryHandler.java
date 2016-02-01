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

import java.net.URI;
import java.util.Date;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;


@Resource(
	id=RepositoryHandler.ID,
	attachments={
		@Attachment(
			id=RepositoryHandler.REPOSITORY_BRANCHES,
			path="branches/",
			handler=BranchContainerHandler.class
		),
		@Attachment(
			id=RepositoryHandler.REPOSITORY_COMMITS,
			path="commits/",
			handler=CommitContainerHandler.class
		)
	}
)
public class RepositoryHandler implements ResourceHandler {

	public static final String ID="RepositoryHandler";
	public static final String REPOSITORY_BRANCHES="REPOSITORYBRANCHES";
	public static final String REPOSITORY_COMMITS="REPOSITORYCOMMITS";

	private static final URI DEPICTION_PATH = URI.create("#depiction");

	private final BackendController backendController;

	public RepositoryHandler(final BackendController backendController) {
		this.backendController = backendController;
	}

	@Override
	public DataSet get(final ResourceSnapshot resource) {
		@SuppressWarnings("unchecked")
		final Name<String> name = (Name<String>)resource.name();
		try{
			final Repository repository = this.backendController.getRepository(name.id().toString());
			return maptoDataSet(repository,name);
		} catch(final Exception e){
			 throw new ApplicationRuntimeException(e);
		}
	}

	private DataSet maptoDataSet(final Repository repository, final Name<String> repoName) {
		final DataSet dataSet=DataSets.createDataSet(repoName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		final Name<String> ownerName = NamingScheme.getDefault().name(repository.getOwner().getId());

		helper.
			managedIndividual(repoName, RepositoryHandler.ID).
				property(RepositoryVocabulary.TYPE).
					withIndividual(RepositoryVocabulary.SCM_REPOSITORY).
				property(RepositoryVocabulary.LOCATION).
					withLiteral(repository.getHttpUrlToRepo()).
				property(RepositoryVocabulary.NAME).
					withLiteral(repository.getName()).
				property(RepositoryVocabulary.CREATED_ON).
					withLiteral(new Date(repository.getCreatedAt())).
				property(RepositoryVocabulary.FIRST_COMMIT).
					withLiteral(new Date(repository.getFirstCommitAt())).
				property(RepositoryVocabulary.LAST_COMMIT).
					withLiteral(new Date(repository.getLastCommitAt())).
				property(RepositoryVocabulary.ARCHIVED).
					withLiteral(new Boolean(repository.getArchived())).
				property(RepositoryVocabulary.PUBLIC).
					withLiteral(new Boolean(repository.getPublic())).
				property(RepositoryVocabulary.OWNER).
					withIndividual(ownerName, UserHandler.ID).
				property(RepositoryVocabulary.REPOSITORY_ID).
					withLiteral(repository.getId().toString()).
				property(RepositoryVocabulary.TAGS).
					withLiteral(repository.getTags());

		for (final String userId:repository.getContributors()){
			final Name<String> userName = NamingScheme.getDefault().name(userId);
			helper.
				managedIndividual(repoName, RepositoryHandler.ID).
						property(RepositoryVocabulary.DEVELOPER).
							withIndividual(userName,UserHandler.ID);
		}

		if(repository.getAvatarUrl() !=null) {
			helper.
				managedIndividual(repoName, RepositoryHandler.ID).
					property(RepositoryVocabulary.DEPICTION).
						withIndividual(repoName, RepositoryHandler.ID,DEPICTION_PATH);
			helper.
				relativeIndividual(repoName,RepositoryHandler.ID,DEPICTION_PATH).
					property(RepositoryVocabulary.TYPE).
						withIndividual(RepositoryVocabulary.IMAGE).
					property(RepositoryVocabulary.DEPICTS).
						withIndividual(repository.getAvatarUrl());
		}

		return dataSet;
	}

}
