/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015 Center for Open Middleware.
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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.2.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.Repository;

//import org.joda.time.DateTime;
import java.net.URI;

import org.joda.time.DateTime;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.publisher.BackendController;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.Mapper;


@Resource(id=RepositoryHandler.ID,
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
public class RepositoryHandler implements ResourceHandler, RepositoryVocabulary{
	
	public static final String ID="RepositoryHandler";
	public static final String REPOSITORY_BRANCHES="REPOSITORYBRANCHES";
	public static final String REPOSITORY_COMMITS="REPOSITORYCOMMITS";
	BackendController backendController;
	
	private static final URI DEPICTION_PATH = URI.create("#depiction");
	
	public RepositoryHandler(BackendController backendController) {	
		this.backendController = backendController;
	}

	public DataSet get(ResourceSnapshot resource)
			throws UnknownResourceException, ApplicationRuntimeException {		
		
		Name<String> name = (Name<String>)resource.name();						
		try{
			Repository repository = backendController.getRepository(name.id().toString());		
			return maptoDataSet(repository,name);	
		}
		catch(Exception e){
			 throw new ApplicationRuntimeException(e);
		}				
	}

	private DataSet maptoDataSet(Repository repository, Name<String> repoName) {
					
		DataSet dataSet=DataSets.createDataSet(repoName);
		DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		

		Name<String> ownerName = NamingScheme.getDefault().name(repository.getOwner().getId());	

		helper.
		managedIndividual(repoName, RepositoryHandler.ID).
			property(TYPE).
				withIndividual(SCMREPOSITORY).				
//			property(LOCATION).
//				withIndividual(repository.getHttpUrlToRepo()).
			property(LOCATION).
				withLiteral(repository.getHttpUrlToRepo()).
			property(NAME).
				withLiteral(repository.getName()).
			property(CREATEDON).
				withLiteral(Mapper.toLiteral(new DateTime(repository.getCreatedAt()).toDate())).
			property(FIRSTCOMMIT).
				withLiteral(Mapper.toLiteral(new DateTime(repository.getFirstCommitAt()).toDate())).
			property(LASTCOMMIT).
				withLiteral(Mapper.toLiteral(new DateTime(repository.getLastCommitAt()).toDate())).
			property(ARCHIVED).
				withLiteral(new Boolean(repository.getArchived())).
			property(PUBLIC).
				withLiteral(new Boolean(repository.getPublic())).
			property(OWNER).
				withIndividual(ownerName, UserHandler.ID).
			property(REPOSITORYID).
				withLiteral(repository.getId().toString()).
			property(TAGS).
				withLiteral(repository.getTags());		
//				property(DEFAULTBRANCH).
//				withIndividual(repository.getDefaultBranch());
		
		for (String userId:repository.getContributors()){
			Name<String> userName = NamingScheme.getDefault().name(userId);
			
			helper.
			managedIndividual(repoName, RepositoryHandler.ID).
					property(DEVELOPER).
						withIndividual(userName,UserHandler.ID);
		}
		
//		for (String branchId:repository.getBranches().getBranchIds()){
//			Name<String> branchName = NamingScheme.getDefault().name(repository.getId().toString(),branchId);
//			helper.
//			managedIndividual(repoName, RepositoryHandler.ID).
//					property(HASBRANCH).
//						withIndividual(branchName,BranchHandler.ID);
//		}
//		
		
		if ( repository.getAvatarUrl() !=null){
			helper.
			managedIndividual(repoName, RepositoryHandler.ID).
				property(DEPICTION).
					withIndividual(repoName, RepositoryHandler.ID,DEPICTION_PATH);
			helper.
			relativeIndividual(repoName,RepositoryHandler.ID,DEPICTION_PATH).
				property(TYPE).
					withIndividual(IMAGE).
				property(DEPICTS).
					withIndividual(repository.getAvatarUrl());		
		}
		
		return dataSet;
	}


	
}
