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

import java.io.IOException;
import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.BackendController;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.AbstractEntityResourceHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

import com.google.common.base.Optional;


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
public final class RepositoryHandler extends AbstractEntityResourceHandler<Repository,Integer> {

	private static final Logger LOGGER=LoggerFactory.getLogger(RepositoryHandler.class);

	public static final String ID="RepositoryHandler";
	public static final String REPOSITORY_BRANCHES="REPOSITORYBRANCHES";
	public static final String REPOSITORY_COMMITS="REPOSITORYCOMMITS";

	private static final URI DEPICTION_PATH = URI.create("#depiction");

	public RepositoryHandler(final BackendController backendController) {
		super(backendController);
	}

	@Override
	protected Integer getId(final ResourceSnapshot resource) {
		return IdentityUtil.repositoryId(resource);
	}

	@Override
	protected Repository getEntity(final BackendController controller, final Integer key) throws IOException {
		return controller.getRepository(key);
	}

	@Override
	protected DataSet toDataSet(final Repository repository, final Integer repositoryId) {
		final Name<Integer> repoName=IdentityUtil.repositoryName(repositoryId);
		final Name<String> ownerName=IdentityUtil.userName(repository.getOwner().getId());

		final DataSet dataSet=DataSets.createDataSet(repoName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(repoName, RepositoryHandler.ID).
				property(RepositoryVocabulary.TYPE).
					withIndividual(RepositoryVocabulary.SCM_REPOSITORY).
				property(RepositoryVocabulary.LOCATION).
					withLiteral(repository.getHttpUrlToRepo()).
				property(RepositoryVocabulary.CODEBASE).
					withLiteral(repository.getWebUrl()).
				property(RepositoryVocabulary.NAME).
					withLiteral(repository.getName()).
				property(RepositoryVocabulary.CREATED_ON).
					withLiteral(toDate(repository.getCreatedAt(),true,"createdOn",repository).get()).
				property(RepositoryVocabulary.FIRST_COMMIT).
					withLiteral(toDate(repository.getFirstCommitAt(),false,"firstCommitAt",repository).orNull()).
				property(RepositoryVocabulary.LAST_COMMIT).
					withLiteral(toDate(repository.getLastCommitAt(),false,"lastCommitAt",repository).orNull()).
				property(RepositoryVocabulary.ARCHIVED).
					withLiteral(isArchived(repository).orNull()).
				property(RepositoryVocabulary.PUBLIC).
					withLiteral(isPublic(repository).orNull()).
				property(RepositoryVocabulary.OWNER).
					withIndividual(ownerName, UserHandler.ID).
				property(RepositoryVocabulary.REPOSITORY_ID).
					withLiteral(repositoryId.toString()).
				property(RepositoryVocabulary.TAGS).
					withLiteral(repository.getTags());

		for(final String userId:repository.getContributors()) {
			final Name<String> userName = IdentityUtil.userName(userId);
			helper.
				managedIndividual(repoName, RepositoryHandler.ID).
						property(RepositoryVocabulary.DEVELOPER).
							withIndividual(userName,UserHandler.ID);
		}

		if(repository.getAvatarUrl()!=null) {
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

	private Optional<Boolean> isPublic(final Repository repository) {
		final String value = repository.getPublic();
		if(value!=null) {
			return Optional.of(Boolean.parseBoolean(value));
		} else {
			LOGGER.warn("Repository {} ({}) does not declare whether it is public or not",repository.getId(),repository.getHttpUrlToRepo());
			return Optional.absent();
		}
	}

	private Optional<Boolean> isArchived(final Repository repository) {
		final String value = repository.getState();
		if(value!=null) {
			return Optional.of("archived".equalsIgnoreCase(value));
		} else {
			LOGGER.warn("Repository {} ({}) does not declare its state",repository.getId(),repository.getHttpUrlToRepo());
			return Optional.absent();
		}
	}


}