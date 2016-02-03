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
package org.smartdeveloperhub.harvesters.scm.frontend.core.harvester;

import java.io.IOException;
import java.net.URI;

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
import org.smartdeveloperhub.harvesters.scm.frontend.core.GitLabHarvester;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;

@Resource(
	id=HarvesterHandler.ID,
	attachments={
		@Attachment(
			id=HarvesterHandler.HARVESTER_REPOSITORIES,
			path="repositories/",
			handler=RepositoryContainerHandler.class
		),
		@Attachment(
			id=HarvesterHandler.HARVESTER_COMMITTERS,
			path="committers/",
			handler=UserContainerHandler.class
		)
	}
)
public class HarvesterHandler implements ResourceHandler {

	public static final String ID                     = "HarvesterHandler";
	public static final String HARVESTER_REPOSITORIES = "HarvesterRepositories";
	public static final String HARVESTER_COMMITTERS   = "HarvesterCommitters";

	private static final URI VOCABULARY_PATH = URI.create("#vocabulary");

	private final BackendController backendController;

	public HarvesterHandler(final BackendController backendController) {
		this.backendController = backendController;
	}

	@Override
	public DataSet get(final ResourceSnapshot resource) {
		@SuppressWarnings("unchecked")
		final Name<URI> name = (Name<URI>)resource.name();
		try{
			final GitLabHarvester gitLabHarvester = this.backendController.createGitLabHarvester();
			return maptoDataSet(gitLabHarvester,name);
		} catch(final Exception e){
			throw new ApplicationRuntimeException(e);
		}
	}

	private DataSet maptoDataSet(final GitLabHarvester gitLabHarvester, final Name<URI> harvesterName) throws IOException {
		final DataSet dataSet=DataSets.createDataSet(harvesterName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(harvesterName, ID).
				property(HarvesterVocabulary.TYPE).
					withIndividual(HarvesterVocabulary.DC_TYPE_SERVICE_TYPE).
					withIndividual(HarvesterVocabulary.MICRO_SERVICE_TYPE).
					withIndividual(HarvesterVocabulary.LINKED_DATA_MICRO_SERVICE_TYPE).
					withIndividual(HarvesterVocabulary.HARVESTER).
					withIndividual(HarvesterVocabulary.SCM_HARVESTER).
				property(HarvesterVocabulary.HARVESTER_VOCABULARY).
					withIndividual(harvesterName,HarvesterHandler.ID,VOCABULARY_PATH);


		for(final Integer repositoryId:gitLabHarvester.getRepositories()){
			final Name<String> repositoryName = NamingScheme.getDefault().name(Integer.toString(repositoryId));
			helper.
				managedIndividual(harvesterName, ID).
						property(HarvesterVocabulary.REPOSITORY).
							withIndividual(repositoryName,RepositoryHandler.ID);
		}

		for(final String userId:this.backendController.getCommitters()){
			final Name<String> userName = NamingScheme.getDefault().name(userId);
			helper.
				managedIndividual(harvesterName, ID).
						property(HarvesterVocabulary.COMMITTER).
							withIndividual(userName,UserHandler.ID);
		}

		helper.
			relativeIndividual(harvesterName,HarvesterHandler.ID,VOCABULARY_PATH).
				property(HarvesterVocabulary.TYPE).
					withIndividual(HarvesterVocabulary.SCM_VOCABULARY).
					withIndividual(HarvesterVocabulary.VOCABULARY).
				property(HarvesterVocabulary.SOURCE).
					withLiteral(URI.create(HarvesterVocabulary.SCM_V1_TTL)).
				property(HarvesterVocabulary.DC_TERMS_SOURCE).
					withLiteral(URI.create(HarvesterVocabulary.SCM_V1_TTL)).
				property(HarvesterVocabulary.IMPLEMENTS).
					withIndividual(HarvesterVocabulary.SCM_DOMAIN_TYPE);

		return dataSet;
	}


}
