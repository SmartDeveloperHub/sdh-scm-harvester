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
 *   Artifact    : org.smartdeveloperhub.harvester:sdh-scm-harvester:1.0.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.scm.handler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.joda.time.DateTime;
import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Commit;
import org.sdh.vocabulary.scm.model.Repository;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class BranchHandler {
	
	OntModel schemaModel;
	OntModel instModel;	
	Branch branch;
	Commit commit;
	

	String repositoryId;
	String name;	
	long createdAt ;
	Date createdAtDate ;
	
	String rdfRepresentation;
	private ArrayList<String> commitIds;
	private CommitHandler commitHandler;
	
	public String getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Branch getBranch() {
		return branch;
	}
	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	private void initOntologyModels(){
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		schemaModel=onto.getJenaModel();
		instModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );		
		//repo = new Repository(schemaModel, instModel);
		branch = new Branch(schemaModel, instModel);
		commitHandler = new CommitHandler();
		
	}
	
	void initEmbededHandler(OntModel schemaModel, OntModel instModel){
		this.schemaModel=schemaModel;
		this.instModel = instModel;
		branch = new Branch(schemaModel, instModel);
	}
	
	public String processBranch(InputStream branchInputStream, InputStream commitsInputStream, String rdfFormat){
		rdfRepresentation = "";
		try( 
				JsonReader jsonReader = Json.createReader(branchInputStream)
	    	)
	    	{    		 			 			 
			  initOntologyModels();
			  JsonObject branchObj = jsonReader.readObject();
			  processCommits(commitsInputStream);
			  processBranchJsonObject(branchObj);
			  rdfRepresentation = branch.getRdfModel(rdfFormat);
	    	}
		return rdfRepresentation;
	}
	
	private void processCommits(InputStream commitsInputStream) {
		try( 
				JsonReader jsonReader = Json.createReader(commitsInputStream)
	    	)
	    	{ 
			     JsonArray array = jsonReader.readArray();
			     commitIds = new ArrayList<String>();
			     for (JsonString commitId : array.getValuesAs(JsonString.class)){
			    	 commitIds.add(commitId.getString());					  					   
				  }
			
	    	}	
		
	}
	public String processBranches(InputStream entityInputStream, String rdfFormat){
		rdfRepresentation = "";
		try( 
				JsonReader jsonReader = Json.createReader(entityInputStream)
	    	)
	    	{ 			
			  initOntologyModels();
			  JsonArray array = jsonReader.readArray();
			  for (JsonString branchId : array.getValuesAs(JsonString.class)){
				  processBranchId(branchId);
				  createBranchIndividual();	
			  }
			  rdfRepresentation = branch.getRdfModel(rdfFormat);	  						  			 
	    	}
		return rdfRepresentation;		
	}
	
	private void processBranchId(JsonString branchId) {
		if(branchId!=null)
			name = branchId.getString();
		
	}
	private void processBranchJsonObject(JsonObject branchObj){
		processBranchJsonObject(branchObj, false);
	}
	
	private void processBranchJsonObject(JsonObject branchObj, boolean noIndividual){
		  if (branchObj.containsKey("name"))
			  if (!branchObj.isNull("name"))
				  name = branchObj.getString("name");
		  
		  if (noIndividual){
	    		createBranchIndividual(noIndividual);
	    		return;
		  }
		  
		  if (branchObj.containsKey("created_at"))
			  if (!branchObj.isNull("created_at")){
				  createdAt = branchObj.getJsonNumber("created_at").longValue();
					  createdAtDate = new DateTime(createdAt).toDate();
			  }
		  
			if (commitIds!=null)
				if (!commitIds.isEmpty()){
					ArrayList<Commit> commits=new ArrayList<Commit>();		
					boolean noIndividualCommit=true;
					for (String commitId:commitIds){		
						commitHandler.initEmbededHandler(schemaModel, instModel);
						commitHandler.setId(commitId);
						commitHandler.setBranchId(name);
						commitHandler.setRepositoryId(Integer.valueOf(repositoryId));
						commitHandler.createCommitIndividual(noIndividualCommit);
						commits.add(commitHandler.getCommit());				
					}			
					branch.setHasCommit(commits);
				}	
 		  
		  createBranchIndividual();		  
	}
	
	public void createBranchIndividual() {
		createBranchIndividual(false);
	}
	public void createBranchIndividual(boolean noIndividual) {
		if (name != null)
			branch.setName(name);
		
		if (repositoryId!=null)
			branch.setRepo(repositoryId);
		
		if (noIndividual)
			return;
		
		if (createdAtDate!=null)
			branch.setCreatedOn(createdAtDate);
			
		//return the individual rdf model in the instanceModel
		branch.getIndividualModel();
	}
}
