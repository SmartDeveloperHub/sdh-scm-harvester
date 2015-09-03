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
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.joda.time.DateTime;
import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.external.foaf.Image;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Person;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class UserHandler {

	String name;
	String userName;
    String avatarUrl;        
    String email;       
    long createdAt;
    Date createdAtDate;     
    long lastCommit;
    Date lastCommitDate;
    long firstCommit;
    Date firstCommitDate;
    String websiteUrl;
    Integer id;
    
	OntModel schemaModel;
	OntModel instModel;	
	Person user;
	Image userImage;
	
	String rdfRepresentation;
	
	
    
    public Person getUser() {
		return user;
	}

	public void setUser(Person user) {
		this.user = user;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

		
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Date getCreatedAtDate() {
		return createdAtDate;
	}

	public void setCreatedAtDate(Date createdAtDate) {
		this.createdAtDate = createdAtDate;
	}

	public long getLastCommit() {
		return lastCommit;
	}

	public void setLastCommit(long lastCommit) {
		this.lastCommit = lastCommit;
	}

	public Date getLastCommitDate() {
		return lastCommitDate;
	}

	public void setLastCommitDate(Date lastCommitDate) {
		this.lastCommitDate = lastCommitDate;
	}

	public long getFirstCommit() {
		return firstCommit;
	}

	public void setFirstCommit(long firstCommit) {
		this.firstCommit = firstCommit;
	}

	public Date getFirstCommitDate() {
		return firstCommitDate;
	}

	public void setFirstCommitDate(Date firstCommitDate) {
		this.firstCommitDate = firstCommitDate;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	//to use the handler standalone	
	private void initHandler(){
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		schemaModel=onto.getJenaModel();
		instModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );		
		user = new Person(schemaModel, instModel);
		userImage= new Image(schemaModel, instModel);
	}	
	
	//to use embeded in other handlers
	void initEmbededHandler(OntModel schemaModel, OntModel instModel){
		this.schemaModel=schemaModel;
		this.instModel = instModel;
		user = new Person(schemaModel, instModel);
		userImage= new Image(schemaModel, instModel);
	}
	
	public String processUser(InputStream entityInputStream, String rdfFormat){
		rdfRepresentation = "";
		try( 
				JsonReader jsonReader = Json.createReader(entityInputStream)
	    	)
	    	{    		 			 			 
			  initHandler();
			  JsonObject branchObj = jsonReader.readObject();
			  processJsonObject(branchObj);
			  rdfRepresentation = user.getRdfModel(rdfFormat);
	    	}
		return rdfRepresentation;
	}
	
	public String processUsers(InputStream entityInputStream, String rdfFormat){
		rdfRepresentation = "";
		try( 
				JsonReader jsonReader = Json.createReader(entityInputStream)
	    	)
	    	{ 			
			  initHandler();
			  JsonArray array = jsonReader.readArray();
			  for (JsonNumber userId : array.getValuesAs(JsonNumber.class)){
				  	processUserId(userId);
				  	createUserIndividual();				    
			  }
			  rdfRepresentation = user.getRdfModel(rdfFormat);	  						  			 
	    	}
		return rdfRepresentation;		
	}
	
	private void processUserId(JsonNumber userId) {
		if(userId!=null)
			id = userId.intValue();
		
	}

	//if embeded in another object before calling this method it
	// is necessary to call the initEmbededHandler() and set noIndividual=true
	public void processJsonObject(JsonObject userObj){
		processJsonObject(userObj,false);
	}
	
    public void processJsonObject(JsonObject userObj, boolean noIndividual){
  	  	
    	if (userObj.containsKey("id"))				
			  if (!userObj.isNull("id"))
			  	id = userObj.getJsonNumber("id").intValue();	
    	
    	if (noIndividual){
    		createUserIndividual(noIndividual);
    		return;
    	}
    	
    	  if (userObj.containsKey("name"))
			  if (!userObj.isNull("name"))
				  name = userObj.getString("name");
    	  
    	  if (userObj.containsKey("username"))
			  if (!userObj.isNull("username"))
				  userName = userObj.getString("username");
    	  
    	  if (userObj.containsKey("avatar_url"))
			  if (!userObj.isNull("avatar_url"))
				  avatarUrl = userObj.getString("avatar_url");    	
    	 
    	  
    	  if (userObj.containsKey("email"))				
			  if (!userObj.isNull("email"))
				  email = userObj.getString("email");    	  
    	     
    	  if (userObj.containsKey("created_at"))
			  if (!userObj.isNull("created_at")){
				  createdAt = userObj.getJsonNumber("created_at").longValue();				  
					  createdAtDate = new DateTime(createdAt).toDate();					  
			  }
    	  
    	  if (userObj.containsKey("last_commit_at"))
			  if (!userObj.isNull("last_commit_at")){
				  lastCommit = userObj.getJsonNumber("last_commit_at").longValue();				  
					  lastCommitDate = new DateTime(lastCommit).toDate();			
			  }
    	  
    	  if (userObj.containsKey("first_commit_at"))
			  if (!userObj.isNull("first_commit_at")){
				  firstCommit = userObj.getJsonNumber("first_commit_at").longValue();
						  firstCommitDate = new DateTime(firstCommit).toDate();		
			  }
    	  
    	  if (userObj.containsKey("website_url"))				
			  if (!userObj.isNull("website_url"))
				  websiteUrl = userObj.getString("website_url"); 
    	  
    	  createUserIndividual();	
    }
    
    public void createUserIndividual(){
    	createUserIndividual(false);
    }
    
    public void createUserIndividual(boolean noIndividual) {
    	
    	if (id!=null)
    		user.setUserId(String.valueOf(id));
    	
    	if (noIndividual)
    		return;
    	
    	if (name!=null)
    		user.setName(name);
    	
    	//String userName; there's no correspondence in the ontology
		
    	if (avatarUrl!=null){
			userImage.setFoafDepicts(avatarUrl);
			userImage.setUserId(String.valueOf(id));
			user.setImg(userImage);
		}
        
    
        if (email!=null)
    		user.setMbox(email);
        
        if (createdAtDate!=null){
        	user.setSignUpDate(createdAtDate);
        }
         
        if (lastCommitDate!=null){
        	user.setLastCommit(lastCommitDate);
        }
        
        if (firstCommitDate!=null){
        	user.setFirstCommit(firstCommitDate);
        }
        
        if (websiteUrl!=null){
        	user.setHomepage(websiteUrl);
        }               
        
        user.getIndividualModel();
    	
    }


    
    
    

//	person01.setMbox(ontModel.createLiteral("andresgs77@hotmail.com",true));
//	person01.setName(ontModel.createLiteral("andres garcia",true));
//	person01.setSignUpDate(ontModel.createLiteral("2015-05-20T21:00:00Z",true));
//	person01.setFirstCommit(ontModel.createLiteral("2015-05-20T21:00:00Z",true));
//	person01.setLastCommit(ontModel.createLiteral("2015-05-20T21:00:00Z",true));
}
