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
package org.sdh.vocabulary.scm.model;


import java.util.ArrayList;

import org.sdh.vocabulary.scm.external.doap.Location;
import org.sdh.vocabulary.scm.external.foaf.Image;
import org.sdh.vocabulary.scm.external.foaf.Person;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateTimeType;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

public class Repository {
	Resource location;
	Resource codebase;
	//XSDDateTime
	Literal createdOn;
	Literal firstCommit;
	Literal lastBuildDate;
	Literal lastCommit;
	
	//Boolean
	Literal isArchived;
	Literal isPublic;
	
	//String
	Literal defaultBranchName;
	Literal description;
	Literal lastBuildStatus;
	Literal name;
    Literal repositoryId;
    Literal tags;
	
	Branch defaultBranch;
	ArrayList<Branch> hasBranch;
	ArrayList<Commit> hasCommit;
	
	Image depiction; 
    
    Person developer;
    Person documenter;
    Person mainteiner;
    Person tester;
    
    Project isRepositoryOf;
    
    public Repository(){
    	hasBranch = new ArrayList<Branch>();
    	hasCommit = new ArrayList<Commit>();
    }
     
    
    public Resource getLocation() {
		return location;
	}


	public void setLocation(Resource location) {
		this.location = location;
	}


	public Resource getCodebase() {
		return codebase;
	}


	public void setCodebase(Resource codebase) {
		this.codebase = codebase;
	}


	public Literal getCreatedOn() {
		return createdOn;
	}


	public void setCreatedOn(Literal createdOn) {
		this.createdOn = createdOn;
	}


	public Literal getFirstCommit() {
		return firstCommit;
	}


	public void setFirstCommit(Literal firstCommit) {
		this.firstCommit = firstCommit;
	}


	public Literal getLastBuildDate() {
		return lastBuildDate;
	}


	public void setLastBuildDate(Literal lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}


	public Literal getLastCommit() {
		return lastCommit;
	}


	public void setLastCommit(Literal lastCommit) {
		this.lastCommit = lastCommit;
	}


	public Literal getIsArchived() {
		return isArchived;
	}


	public void setIsArchived(Literal isArchived) {
		this.isArchived = isArchived;
	}


	public Literal getIsPublic() {
		return isPublic;
	}


	public void setIsPublic(Literal isPublic) {
		this.isPublic = isPublic;
	}


	public Literal getDefaultBranchName() {
		return defaultBranchName;
	}


	public void setDefaultBranchName(Literal defaultBranchName) {
		this.defaultBranchName = defaultBranchName;
	}


	public Literal getDescription() {
		return description;
	}


	public void setDescription(Literal description) {
		this.description = description;
	}


	public Literal getLastBuildStatus() {
		return lastBuildStatus;
	}


	public void setLastBuildStatus(Literal lastBuildStatus) {
		this.lastBuildStatus = lastBuildStatus;
	}


	public Literal getName() {
		return name;
	}


	public void setName(Literal name) {
		this.name = name;
	}


	public Literal getRepositoryId() {
		return repositoryId;
	}


	public void setRepositoryId(Literal repositoryId) {
		this.repositoryId = repositoryId;
	}


	public Literal getTags() {
		return tags;
	}


	public void setTags(Literal tags) {
		this.tags = tags;
	}


	public Branch getDefaultBranch() {
		return defaultBranch;
	}


	public void setDefaultBranch(Branch defaultBranch) {
		this.defaultBranch = defaultBranch;
	}


	public ArrayList<Branch> getHasBranch() {
		return hasBranch;
	}


	public void setHasBranch(ArrayList<Branch> hasBranch) {
		this.hasBranch = hasBranch;
	}


	public ArrayList<Commit> getHasCommit() {
		return hasCommit;
	}


	public void setHasCommit(ArrayList<Commit> hasCommit) {
		this.hasCommit = hasCommit;
	}


	public Image getDepiction() {
		return depiction;
	}


	public void setDepiction(Image depiction) {
		this.depiction = depiction;
	}


	public Person getDeveloper() {
		return developer;
	}


	public void setDeveloper(Person developer) {
		this.developer = developer;
	}


	public Person getDocumenter() {
		return documenter;
	}


	public void setDocumenter(Person documenter) {
		this.documenter = documenter;
	}


	public Person getMainteiner() {
		return mainteiner;
	}


	public void setMainteiner(Person mainteiner) {
		this.mainteiner = mainteiner;
	}


	public Person getTester() {
		return tester;
	}


	public void setTester(Person tester) {
		this.tester = tester;
	}


	public Project getIsRepositoryOf() {
		return isRepositoryOf;
	}


	public void setIsRepositoryOf(Project isRepositoryOf) {
		this.isRepositoryOf = isRepositoryOf;
	}


	Individual getIndividual(OntModel model, String NS){
    	//OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
    	OntClass repositoryClass = model.getOntClass( NS + "Programme" );
    	Individual repositoryInst = repositoryClass.createIndividual();   	
    	
    	//location
       	ObjectProperty locationProperty = model.getObjectProperty( NS + "location" );   
       	repositoryInst.addProperty(locationProperty, location);
       	
        //codebase
       	ObjectProperty codebaseProperty = model.getObjectProperty( NS + "codebase" );   
       	repositoryInst.addProperty(codebaseProperty, codebase);
    	
    	//createdOn
       	DatatypeProperty createdOnProperty = model.getDatatypeProperty(NS + "createdOn");
       	repositoryInst.addLiteral(createdOnProperty, createdOn);
       	
        //firstCommit
       	DatatypeProperty firstCommitProperty = model.getDatatypeProperty(NS + "firstCommit");
       	repositoryInst.addLiteral(firstCommitProperty, firstCommit);
    	
        //lastBuildDate
       	DatatypeProperty lastBuildDateProperty = model.getDatatypeProperty(NS + "lastBuildDate");
       	repositoryInst.addLiteral(lastBuildDateProperty, lastBuildDate);
       	
        //lastCommit
       	DatatypeProperty lastCommitProperty = model.getDatatypeProperty(NS + "lastCommit");
       	repositoryInst.addLiteral(lastCommitProperty, lastCommit);
    	       	
       	//isArchived;
       	DatatypeProperty isArchivedProperty = model.getDatatypeProperty(NS + "isArchived");
       	repositoryInst.addLiteral(isArchivedProperty, isArchived);
    	
       	//isPublic;
       	DatatypeProperty isPublicProperty = model.getDatatypeProperty(NS + "isPublic");
       	repositoryInst.addLiteral(isPublicProperty, isPublic);
       	
    	//defaultBranchName;
       	DatatypeProperty defaultBranchNameProperty = model.getDatatypeProperty(NS + "defaultBranchName");
       	repositoryInst.addLiteral(defaultBranchNameProperty, defaultBranchName);
       	
    	//description;
       	DatatypeProperty descriptionNameProperty = model.getDatatypeProperty(NS + "description");
       	repositoryInst.addLiteral(descriptionNameProperty, description);
       	
       	//lastBuildStatus;
    	DatatypeProperty lastBuildStatusProperty = model.getDatatypeProperty(NS + "lastBuildStatus");
       	repositoryInst.addLiteral(lastBuildStatusProperty, lastBuildStatus);
       	
    	//name;
       	DatatypeProperty nameProperty = model.getDatatypeProperty(NS + "name");
       	repositoryInst.addLiteral(nameProperty, name);
       	
        //repositoryId;
       	DatatypeProperty repositoryIdProperty = model.getDatatypeProperty(NS + "repositoryId");
       	repositoryInst.addLiteral(repositoryIdProperty, repositoryId);
       	
        //tags;
       	DatatypeProperty tagsProperty = model.getDatatypeProperty(NS + "tags");
       	repositoryInst.addLiteral(tagsProperty, tags);
       	
       	//defaultBranch;
       	ObjectProperty defaultBranchProperty = model.getObjectProperty( NS + "defaultBranch" );   
       	repositoryInst.addProperty(defaultBranchProperty, defaultBranch.getIndividual());
       	
    	//(0..*)hasBranch;
       	ObjectProperty hasBranchProperty = model.getObjectProperty( NS + "hasBranch" );   
       	for (Branch branch:hasBranch){
       		repositoryInst.addProperty(hasBranchProperty, branch.getIndividual());
       	}
       	
        //(0..*)hasCommit;
       	ObjectProperty hasCommitProperty = model.getObjectProperty( NS + "hasCommit" );   
       	for (Commit commit:hasCommit){
       		repositoryInst.addProperty(hasCommitProperty, commit.getIndividual());
       	}
    	
    	//depiction; 
       	ObjectProperty depictionProperty = model.getObjectProperty( NS + "depiction" );   
       	repositoryInst.addProperty(depictionProperty, depiction.getIndividual());
        
       	//developer;
       	ObjectProperty developerProperty = model.getObjectProperty( NS + "developer" );   
       	repositoryInst.addProperty(developerProperty, developer.getIndividual());
       	
       	//documenter;
       	ObjectProperty documenterProperty = model.getObjectProperty( NS + "documenter" );   
       	repositoryInst.addProperty(documenterProperty, documenter.getIndividual());
       	
        //mainteiner;
       	ObjectProperty mainteinerProperty = model.getObjectProperty( NS + "mainteiner" );   
       	repositoryInst.addProperty(mainteinerProperty, mainteiner.getIndividual());
       	
        //tester;
        ObjectProperty testerProperty = model.getObjectProperty( NS + "tester" );   
       	repositoryInst.addProperty(testerProperty, tester.getIndividual());
        
        //isRepositoryOf;
        ObjectProperty isRepositoryOfProperty = model.getObjectProperty( NS + "isRepositoryOf" );   
       	repositoryInst.addProperty(isRepositoryOfProperty, isRepositoryOf.getIndividual());
        
		return repositoryInst;    	
    }
        
    
}
