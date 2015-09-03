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
 *   Artifact    : org.smartdeveloperhub.harvester:sdh-scm-harvester:0.1.0
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.scm.handler;

import java.io.InputStream;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.joda.time.DateTime;
import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Commit;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class CommitHandler {
	OntModel schemaModel;
	OntModel instModel;	
	String id;
	String branchId;
	Integer repositoryId;
	long createdAt ;
	int authorId;
	Date createdAtDate ;	
	
	Commit commit;
	private UserHandler userHandler;
	private String rdfRepresentation;
	
	public void initEmbededHandler(OntModel schemaModel, OntModel instModel) {
		// TODO Auto-generated method stub
		this.schemaModel=schemaModel;
		this.instModel=instModel;
		commit = new Commit(schemaModel, instModel);		
	}
		
	private void initOntologyModels(){
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		schemaModel=onto.getJenaModel();
		instModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );		
		commit = new Commit(schemaModel, instModel);
		userHandler = new UserHandler();		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Integer repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	
	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}
	
	public String processCommit(InputStream commitsInputStream, String rdfFormat) {
		rdfRepresentation = "";
		
		try( 
				JsonReader jsonReader = Json.createReader(commitsInputStream)
	    	)
	    	{    		 			 			 
			  initOntologyModels();
			  JsonObject commitObj = jsonReader.readObject();			  
			  processCommitJsonObject(commitObj);
			  rdfRepresentation = commit.getRdfModel(rdfFormat);
	    	}
		return rdfRepresentation;
	}
	
	private void processCommitJsonObject(JsonObject commitObj) {
		processCommitJsonObject(commitObj, false);
	}
				
	private void processCommitJsonObject(JsonObject commitObj, boolean noIndividual){
		  if (commitObj.containsKey("id"))
			  if (!commitObj.isNull("id"))
				  id = commitObj.getString("id");
		  
		  if (noIndividual){
			    createCommitIndividual(noIndividual);
	    		return;
		  }
		  
		  if (commitObj.containsKey("created_at"))
			  if (!commitObj.isNull("created_at")){
				  createdAt = commitObj.getJsonNumber("created_at").longValue();				  
					  createdAtDate = new DateTime(createdAt).toDate();
			  }
		  
		  //missing performedBy User
		  if (commitObj.containsKey("author"))
			  if (!commitObj.isNull("author")){
				  boolean noIndividualUser=true;
				  authorId = commitObj.getJsonNumber("author").intValue();
				  userHandler.initEmbededHandler(schemaModel, instModel);
				  userHandler.setId(authorId);
				  userHandler.createUserIndividual(noIndividualUser);
				  commit.setPerformedBy(userHandler.getUser());
			  }
		  
		  createCommitIndividual();		  
	}
	
	public void createCommitIndividual() {
		createCommitIndividual(false);
	}

	public void createCommitIndividual(boolean noIndividual) {
		// TODO Auto-generated method stub
		if (id != null)
			commit.setId(id);
		
		if (repositoryId!=null)
			commit.setRepoId(repositoryId);
		
		if (branchId!=null)
			commit.setBranchId(branchId);
		
		if (noIndividual)
			return;
		
		if (createdAtDate!=null)
			commit.setCreatedOn(createdAtDate);
		
			
		//return the individual rdf model in the instanceModel
		commit.getIndividualModel();
	}


	
	

}
