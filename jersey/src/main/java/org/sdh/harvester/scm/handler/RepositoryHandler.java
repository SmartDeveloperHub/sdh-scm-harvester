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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.jersey:scm-harvester-jersey:0.2.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.scm.handler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.sdh.harvester.constants.GitlabEnhancerConstants;
import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.external.foaf.Image;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Person;
import org.sdh.vocabulary.scm.model.Repository;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RepositoryHandler {
	
	OntModel schemaModel;
	OntModel instModel;
	Repository repo;
	Branch branch;
	UserHandler userHandler;
	BranchHandler branchHandler;
	Person user;
	Image userImage ;
	Image repoImage;
	
	
	String httpUrlToRepo ;
	String webUrl;
	Integer id;
	Boolean archived ;
	Boolean publicRepo ;
	String description ;
	String defaultBranch ;
	String defaultBranchName;
	long lastActivityAt ;
	Date lastActivityAtDate ;
	String name ;
	long createdAt ;
	Date createdAtDate ;
	String avatarUrl;
	long firstCommit ;
	Date firstCommitDate ;
	long lastCommit ;
	Date lastCommitDate ;
	
	JsonObject owner;
	ArrayList<Integer> developerIds;	
	ArrayList<String> branchIds;
	ArrayList<String> tags;
	
	String rdfRepresentation;
	
	
	
	
	
	public Repository getRepo() {
		return repo;
	}

	public void setRepo(Repository repo) {
		this.repo = repo;
	}

	public ArrayList<Integer> getRepositoriesResources(){
		InputStream repositoriesIS = callRepositoriesEndpoint();
		ArrayList<Integer> RepositoriesId = readReposistoriesIS(repositoriesIS);	
		return RepositoriesId;
	}
	
	private InputStream callRepositoriesEndpoint(){
		String responseContent="";
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target(GitlabEnhancerConstants.gitlabEnhancerEndpoint);    	
    	WebTarget resourceWebTarget = webTarget.path("projects");
    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
    	Response response = invocationBuilder.get();    	
    	return response.readEntity(InputStream.class);
	}

	private ArrayList<Integer> readReposistoriesIS(InputStream repositoriesIS) {	
		ArrayList<Integer> ids = new ArrayList<Integer>();
		try( 
				JsonReader jsonReader = Json.createReader(repositoriesIS)
	    	)
	    	{    		 
			  JsonArray array = jsonReader.readArray();			  			 
			  for (JsonNumber repositoryId : array.getValuesAs(JsonNumber.class)){
				  if(repositoryId!=null){
						id = repositoryId.intValue();
						ids.add(id);
				  }
			  }
	    	}
		return ids;
	}

	public String processRepositories(InputStream entityInputStream, String rdfFormat){
		rdfRepresentation = "";
		try( 
				JsonReader jsonReader = Json.createReader(entityInputStream)
	    	)
	    	{    		 
			  JsonArray array = jsonReader.readArray();
			  
			  initOntologyModels();			  
			  for (JsonNumber repositoryId : array.getValuesAs(JsonNumber.class)){
				  	processRepositoryId(repositoryId);
				  	createRepositoryEntity();				    
			  }
			  rdfRepresentation = repo.getRdfModel(rdfFormat);
	    	}
		return rdfRepresentation;
	}
	
	private void processRepositoryId(JsonNumber repositoryId){
		if(repositoryId!=null)
			id = repositoryId.intValue();
	}
	

	public String processRepository(InputStream repositoryInputStream, InputStream branchesInputStream, String rdfFormat){
		rdfRepresentation = "";
		try( 
				JsonReader jsonReader = Json.createReader(repositoryInputStream)
	    	)
	    	{    		 
			  //JsonArray array = jsonReader.readArray();
			  JsonObject repository = jsonReader.readObject();			  
			  initOntologyModels();		  	
			  processBranches(branchesInputStream);
			  processRepositoryJsonObject(repository);						  
			  rdfRepresentation = repo.getRdfModel(rdfFormat);
	    	}
		return rdfRepresentation;
	}
	


	private void processBranches(InputStream branchesInputStream) {
		try( 
				JsonReader jsonReader = Json.createReader(branchesInputStream)
	    	)
	    	{ 
			     JsonArray array = jsonReader.readArray();
			     branchIds = new ArrayList<String>();
			     for (JsonString branchId : array.getValuesAs(JsonString.class)){
			    	 	branchIds.add(branchId.getString());					  					   
				  }
			
	    	}		
	}

	private void processRepositoryJsonObject(JsonObject repository){
		  if (repository.containsKey("http_url_to_repo"))
			  if (!repository.isNull("http_url_to_repo"))
				  httpUrlToRepo = repository.getString("http_url_to_repo");
		  if (repository.containsKey("web_url"))
			  if (!repository.isNull("web_url"))
				  webUrl= repository.getString("web_url");
		  if (repository.containsKey("id"))				
			  if (!repository.isNull("id"))
			  	id = Integer.valueOf(repository.getInt("id")) ;				  
		  if (repository.containsKey("archived"))
			  if (!repository.isNull("archived"))
				  archived = Boolean.valueOf(repository.getString("archived"));
		  if (repository.containsKey("public"))
			  if (!repository.isNull("public"))
				  publicRepo = Boolean.valueOf(repository.getString("public"));
		  if (repository.containsKey("description"))
			  if (!repository.isNull("description"))
				  description = repository.getString("description");
		  if (repository.containsKey("default_branch"))
			  if (!repository.isNull("default_branch"))
				  defaultBranch = repository.getString("default_branch");
		  if (repository.containsKey("default_branch-name"))
			  if (!repository.isNull("default_branch-name"))
				  defaultBranchName = repository.getString("default_branch-name");
		  
		  if (repository.containsKey("last_activity_at"))
			  if (!repository.isNull("last_activity_at")){
				  lastActivityAt = repository.getJsonNumber("last_activity_at").longValue();					     
				  lastActivityAtDate = new DateTime(lastActivityAt).toDate();				    	 
			  }
			  
		  if (repository.containsKey("name"))
			  if (!repository.isNull("name"))
				  name = repository.getString("name");
		  
		  if (repository.containsKey("created_at"))
			  if (!repository.isNull("created_at")){
				  createdAt = repository.getJsonNumber("created_at").longValue();				  
				  createdAtDate = new DateTime(Long.valueOf(createdAt)).toDate();
			  }
		  
		  if (repository.containsKey("first_commit_at"))
			  if (!repository.isNull("first_commit_at")){
				  firstCommit = repository.getJsonNumber("first_commit_at").longValue();				  
				  firstCommitDate = new DateTime(Long.valueOf(firstCommit)).toDate();
			  }
		  
		  if (repository.containsKey("last_commit_at"))
			  if (!repository.isNull("last_commit_at")){
				  lastCommit = repository.getJsonNumber("last_commit_at").longValue();				  
				  lastCommitDate = new DateTime(Long.valueOf(lastCommit)).toDate();
			  }
		  				  
		  if (repository.containsKey("owner"))
			  if (!repository.isNull("owner")){
				  owner = repository.getJsonObject("owner");
			  }
			  		  
		  if (repository.containsKey("avatar_url"))
			  if (!repository.isNull("avatar_url"))
				  avatarUrl = repository.getString("avatar_url");
		  			
		  tags = new ArrayList<String>();
 		  if (repository.containsKey("tags"))
			  if (!repository.isNull("tags")){
				  JsonArray tagsArray = repository.getJsonArray("tags");				  
				  for (JsonString tag : tagsArray.getValuesAs(JsonString.class)){
					  if (!tag.getString().isEmpty())				  
						  tags.add(tag.getString());
				  }
			  }
		
		  
		  System.out.println("-----------");
		  developerIds = new ArrayList<Integer>();
		  if (repository.containsKey("contributors"))
			  if (!repository.isNull("contributors")){
//				  if (repository.getValueType().equals(JsonValue.ValueType.ARRAY)){
					  JsonArray contributorArray = repository.getJsonArray("contributors");				
					  int developerId;
					  	for (JsonNumber contributorId : contributorArray.getValuesAs(JsonNumber.class)){					  		
//					  		if (contributorId.toString().startsWith("users:"))
//					  			developerId = contributorId.getString().replace("user:","");
//					  		else 
					  			developerId = contributorId.intValue();
					  		//if (!developerId.isEmpty())
					  			developerIds.add(Integer.valueOf(developerId));
					  	}
//				  }
//				  else //dealing with the string
//				  {
//					  JsonString contributorsJsonString = repository.getJsonString("contributors");	
//					  String contributorsString = contributorsJsonString.toString().replace("'","").replace("[","").replace("]","").replace("users:","").replace(" ","");
//					  String[] contributors = contributorsString.toString().split(",");
//					  String developerId;
//					  for (int i=0; i<contributors.length;i++){
//						  developerId="";
//						  developerId = contributors[i];			  		
//					  	  if (!developerId.isEmpty())
//					  		developerIds.add(Integer.valueOf(developerId));						  
//					  }
//					  
//				  }
					  
			  }		  
		  
		  createRepositoryEntity();	
	}

	
	private void initOntologyModels(){
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		schemaModel=onto.getJenaModel();
		instModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		repo = new Repository(schemaModel, instModel);
		branch = new Branch(schemaModel, instModel);
		user = new Person(schemaModel, instModel);
		userImage = new Image(schemaModel, instModel);
		repoImage = new Image(schemaModel, instModel);
		userHandler = new UserHandler();
		branchHandler = new BranchHandler();		
	}
	
	void initEmbededHandler(OntModel schemaModel, OntModel instModel){
		this.schemaModel=schemaModel;
		this.instModel = instModel;
		repo = new Repository(schemaModel, instModel);
	}

	private void createRepositoryEntity() {		
		boolean noIndividual=true;
		if (httpUrlToRepo!=null)
			repo.setLocation(httpUrlToRepo);
				
		//repo.setCodebase(codebase);
		
		if (createdAtDate!=null)
			repo.setCreatedOn(createdAtDate);
				
		// repo.setFirstCommit(firstCommit);				
		// repo.setLastBuildDate(lastBuildDate);
		
		if (firstCommitDate!=null)
			repo.setFirstCommit(firstCommitDate);
		
		if (lastCommitDate!=null)
			repo.setLastCommit(lastCommitDate);
			
		if (archived !=null)
			repo.setIsArchived(archived);
				
		if (publicRepo !=null)			
			repo.setIsPublic(publicRepo);
				
		if (defaultBranchName != null)
		    repo.setDefaultBranchName(defaultBranchName);
				
		if (description!=null)
			repo.setDescription(description);		
		
		//repo.setLastBuildStatus(schemaModel.createLiteral("Sucesful",true));
		
		if (name!=null)
			repo.setName(name);
	
		if (id!=null)
	      repo.setRepositoryId(String.valueOf(id));
	    
	    //repo.setTags(schemaModel.createLiteral("repo, development, business",true));
		
		if (avatarUrl!=null){
			repoImage.setFoafDepicts(avatarUrl);
			repoImage.setRepoId(String.valueOf(id));
			repo.setDepiction(repoImage);
		}
		if (tags!=null){
			repo.setTags(tags);
		}
					
		if (defaultBranch != null){			
			branch.setId(defaultBranch);
			branch.setRepo(String.valueOf(id));
			repo.setDefaultBranch(branch);
		}
		
		
		ArrayList<Person> developers=new ArrayList<Person>();
		if (developerIds!=null)
			if (!developerIds.isEmpty()){							
				for (Integer developerId:developerIds){		
					userHandler.initEmbededHandler(schemaModel, instModel);
					userHandler.setId(developerId);				
					userHandler.createUserIndividual(noIndividual);
					developers.add(userHandler.getUser());				
				}			
				repo.setDevelopers(developers);
			}
		
		//if the owner is a group (type!=user) then assign the first developer as owner
		if (owner!=null){	
			Integer userId = null;
			String userType="";
			if (owner.containsKey("id"))				
				  if (!owner.isNull("id"))
				  	userId = owner.getJsonNumber("id").intValue();
			if (owner.containsKey("type"))				
				  if (!owner.isNull("type"))
				  	userType = owner.getJsonString("type").getString();
			
			if (userType.equalsIgnoreCase("user")){
				if (userId!=null){
					userHandler.initEmbededHandler(schemaModel, instModel);
					userHandler.setId(userId);
					userHandler.createUserIndividual(noIndividual);	
					}			
			}else
			{	
				if(!developers.isEmpty())
					userHandler.setUser(developers.get(0));
		
			}
			repo.setOwner(userHandler.user);
		}
		
		if (branchIds!=null)
			if (!branchIds.isEmpty()){
				ArrayList<Branch> branches=new ArrayList<Branch>();			
				for (String branchId:branchIds){		
					branchHandler.initEmbededHandler(schemaModel, instModel);
					branchHandler.setId(branchId);
					branchHandler.setRepositoryId(String.valueOf(id));
					branchHandler.createBranchIndividual(noIndividual);
					branches.add(branchHandler.getBranch());				
				}			
				repo.setHasBranch(branches);
			}		
		
		repo.getIndividualModel();
				
	 }

}
