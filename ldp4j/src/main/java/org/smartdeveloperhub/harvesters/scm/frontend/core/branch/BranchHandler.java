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
public class BranchHandler implements ResourceHandler, BranchVocabulary{

	public static final String ID="BranchHandler";
	BackendController backendController;
	
	public BranchHandler(BackendController backendController) {	
		this.backendController = backendController;
	}
	
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException,
		ApplicationRuntimeException {		
		@SuppressWarnings("unchecked")
		Name<String> name = (Name<String>)resource.name();
		
		BranchKey branchKey=backendController.getBranchIdentityMap().getKey(name);
		
		try{
			Branch branch= backendController.getBranch(branchKey.getRepoId(), branchKey.getBranchId());		
			return maptoDataSet(branchKey.getRepoId(), branch,name);	
		}
		catch(Exception e){
			 throw new ApplicationRuntimeException(e);
		}				
	}

	private DataSet maptoDataSet(String repositoryId, Branch branch, Name<String> branchName) {
					
		DataSet dataSet=DataSets.createDataSet(branchName);
		DataSetHelper helper=DataSetUtils.newHelper(dataSet);
		
		helper.
		managedIndividual(branchName, BranchHandler.ID).
			property(TYPE).
				withIndividual(BRANCHTYPE).				
			property(NAME).
				withLiteral(branch.getName()).
			property(CREATEDON).
				withLiteral(new Date(branch.getCreatedAt()));
//						Mapper.toLiteral(new DateTime(branch.getCreatedAt()).toDate()));
		
		for (String commitId:branch.getCommits().getCommitIds()){			
				Name<String> commitName = NamingScheme.getDefault().name(repositoryId,commitId);
				helper.
				managedIndividual(branchName, BranchHandler.ID).
						property(HASCOMMIT).
							withIndividual(commitName,CommitHandler.ID).
						property(iSTARGETOF).
							withIndividual(commitName,CommitHandler.ID);							
		}
		
//			property(HASCOMMIT).
//				withIndividual(user.getId().toString()).
//			property(iSTARGETOF).
//				withIndividual(user.getId().toString());
		
		return dataSet;
	}

}