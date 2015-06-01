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


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.sdh.vocabulary.scm.Namespace;
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
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Repository extends RDFResource {
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
	
	Image depiction; 
    
    Person developer;
    Person documenter;
    Person maintainer;
    Person tester;
    Person owner;
    
    Project isRepositoryOf;
    
    public Repository(OntModel schemaModel, OntModel instanceModel){
    	super(schemaModel, instanceModel);
    	//defaultBranch = new Branch(schemaModel, instanceModel);
    	hasBranch = new ArrayList<Branch>();
    }
     
    
    public Resource getLocation() {
		return location;
	}

	public void setLocation(Resource location) {
		this.location = location;
	}
	
	public void setLocation(String location) {
		this.location = schemaModel.createResource(location);
	}


	public Resource getCodebase() {
		return codebase;
	}


	public void setCodebase(Resource codebase) {
		this.codebase = codebase;
	}
	
	public void setCodebase(String codebase) {
		this.codebase = schemaModel.createResource(codebase);
	}


	public Literal getCreatedOn() {
		return createdOn;
	}


	public void setCreatedOn(Literal createdOn) {
		this.createdOn = createdOn;
	}
	
	public void setCreatedOn(Date createdOn){
		DateTime dateTime = new DateTime(createdOn);
		this.createdOn = schemaModel.createLiteral(dateTime.toString());
	}


	public Literal getFirstCommit() {
		return firstCommit;
	}


	public void setFirstCommit(Literal firstCommit) {
		this.firstCommit = firstCommit;
	}
	
	public void setFirstCommit(Date firstCommit) {
		DateTime dateTime = new DateTime(firstCommit);
		this.firstCommit = schemaModel.createLiteral(dateTime.toString());
	}


	public Literal getLastBuildDate() {
		return lastBuildDate;
	}


	public void setLastBuildDate(Literal lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}
	
	public void setLastBuildDate(Date lastBuildDate){
		DateTime dateTime = new DateTime(lastBuildDate);
		this.lastBuildDate = schemaModel.createLiteral(dateTime.toString());
	}


	public Literal getLastCommit() {
		return lastCommit;
	}


	public void setLastCommit(Literal lastCommit) {
		this.lastCommit = lastCommit;
	}
	
	public void setLastCommit(Date lastCommit){
		DateTime dateTime = new DateTime(lastCommit);
		this.lastCommit = schemaModel.createLiteral(dateTime.toString());
	}


	public Literal getIsArchived() {
		return isArchived;
	}


	public void setIsArchived(Literal isArchived) {
		this.isArchived = isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = schemaModel.createLiteral(isArchived.toString());
	}

	public Literal getIsPublic() {
		return isPublic;
	}


	public void setIsPublic(Literal isPublic) {
		this.isPublic = isPublic;
	}

	public void setIsPublic(Boolean isPublic){
		this.isPublic = schemaModel.createLiteral(isPublic.toString());
	}

	public Literal getDefaultBranchName() {
		return defaultBranchName;
	}


	public void setDefaultBranchName(Literal defaultBranchName) {
		this.defaultBranchName = defaultBranchName;
	}

	public void setDefaultBranchName(String defaultBranchName) {
		this.defaultBranchName = schemaModel.createLiteral(defaultBranchName,true);
	}
	
	public Literal getDescription() {
		return description;
	}


	public void setDescription(Literal description) {
		this.description = description;
	}
	
	public void setDescription(String description) {
		this.description = schemaModel.createLiteral(description,true);
	}

	public Literal getLastBuildStatus() {
		return lastBuildStatus;
	}


	public void setLastBuildStatus(Literal lastBuildStatus) {
		this.lastBuildStatus = lastBuildStatus;
	}

	public void setLastBuildStatus(String lastBuildStatus) {
		this.lastBuildStatus = schemaModel.createLiteral(lastBuildStatus,true);
	}

	public Literal getName() {
		return name;
	}

	public void setName(Literal name) {
		this.name =  name;
	}
	
	public void setName(String name) {
		this.name = schemaModel.createLiteral(name,true);
	}

	public Literal getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Literal repositoryId) {
		this.repositoryId = repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = schemaModel.createLiteral(repositoryId,true);
	}
	
	public Literal getTags() {
		return tags;
	}


	public void setTags(Literal tags) {
		this.tags = tags;
	}

	public void setTags(String tags) {
		this.tags = schemaModel.createLiteral(tags,true);
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

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Person getDocumenter() {
		return documenter;
	}


	public void setDocumenter(Person documenter) {
		this.documenter = documenter;
	}


	public Person getmaintainer() {
		return maintainer;
	}


	public void setmaintainer(Person maintainer) {
		this.maintainer = maintainer;
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


	public OntModel getIndividualModel(){
				
//		OntModel doap = schemaModel.getImportedModel("http://www.smartdeveloperhub.org/vocabulary/external/doap/doap.rdf");
//		OntModel platform = schemaModel.getImportedModel("http://www.smartdeveloperhub.org/vocabulary/platform");
		
    	
    	OntClass repositoryClass = schemaModel.getOntClass(Namespace.doapNS+"Repository" );
    	//Individual repositoryInst = repositoryClass.createIndividual(Namespace.scmNS+"repo01");
    	Individual indv = instanceModel.createIndividual(Namespace.scmNS+repositoryId.getString(), repositoryClass);
    	
    	//location
    	if (location!=null){
    		ObjectProperty locationProperty = schemaModel.getObjectProperty( Namespace.doapNS + "location" );   
    		indv.addProperty(locationProperty, location);
    	}
       	
        //codebase
    	if (codebase!=null){
	       	ObjectProperty codebaseProperty = schemaModel.getObjectProperty( Namespace.scmNS + "codebase" );   
	       	indv.addProperty(codebaseProperty, codebase);
    	}
    	
    	//createdOn
    	if (createdOn!=null){
	       	DatatypeProperty createdOnProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "createdOn");
	       	indv.addLiteral(createdOnProperty, createdOn);
    	}
       	
        //firstCommit
    	if (firstCommit!=null){
	       	DatatypeProperty firstCommitProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "firstCommit");
	       	indv.addLiteral(firstCommitProperty, firstCommit);
    	}
    	
        //lastBuildDate
    	if (lastBuildDate!=null){
	       	DatatypeProperty lastBuildDateProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "lastBuildDate");
	       	indv.addLiteral(lastBuildDateProperty, lastBuildDate);
    	}
       	
        //lastCommit
    	if (lastCommit!=null){
	       	DatatypeProperty lastCommitProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "lastCommit");
	       	indv.addLiteral(lastCommitProperty, lastCommit);
    	}
    	       	
       	//isArchived;
       	if (isArchived!=null){
	       	DatatypeProperty isArchivedProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "isArchived");
	       	indv.addLiteral(isArchivedProperty, isArchived);
       	}
    	
       	//isPublic;
       	if (isPublic!=null){
	       	DatatypeProperty isPublicProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "isPublic");
	       	indv.addLiteral(isPublicProperty, isPublic);
       	}
       	
    	//defaultBranchName;
       	if (defaultBranchName!=null){
	       	DatatypeProperty defaultBranchNameProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "defaultBranchName");
	       	indv.addLiteral(defaultBranchNameProperty, defaultBranchName);
       	}
       	
    	//description;
       	if (description!=null){
	       	Property descriptionNameProperty = schemaModel.getProperty(Namespace.doapNS + "description");
	       	indv.addLiteral(descriptionNameProperty, description);
       	}
       	
       	//lastBuildStatus;
       	if (lastBuildStatus!=null){
	    	DatatypeProperty lastBuildStatusProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "lastBuildStatus");
	       	indv.addLiteral(lastBuildStatusProperty, lastBuildStatus);
       	}
       	
    	//name;
       	if (name!=null){
	       	DatatypeProperty nameProperty = schemaModel.getDatatypeProperty(Namespace.doapNS + "name");
	       	indv.addLiteral(nameProperty, name);
       	}
       	
        //repositoryId;
       	if (repositoryId!=null){
       		DatatypeProperty repositoryIdProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "repositoryId");
       		indv.addLiteral(repositoryIdProperty, repositoryId);
       	}
       	
        //tags;
       	if (tags!=null){
       		DatatypeProperty tagsProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "tags");
       		indv.addLiteral(tagsProperty, tags);
       	}

       	//defaultBranch;
       	if (defaultBranch!=null){
       		ObjectProperty defaultBranchProperty = schemaModel.getObjectProperty( Namespace.scmNS + "defaultBranch" );   
       		indv.addProperty(defaultBranchProperty, defaultBranch.getIndividual());
       	}

       	//(0..*)hasBranch;
       	if (hasBranch!=null){
       		ObjectProperty hasBranchProperty = schemaModel.getObjectProperty( Namespace.scmNS + "hasBranch" );   
       		for (Branch branch:hasBranch){
       			indv.addProperty(hasBranchProperty, branch.getIndividual());
       		}
       	}

       	//depiction; 
       	if (depiction!=null){
       		ObjectProperty depictionProperty = schemaModel.getObjectProperty( Namespace.foafNS + "depiction" );   
       		indv.addProperty(depictionProperty, depiction.getIndividual());
       	}

       	//developer;
       	if (developer!=null){
       		Property developerProperty = schemaModel.getProperty( Namespace.doapNS + "developer" );   
       		indv.addProperty(developerProperty, developer.getIndividual());
       	}
       	
       	//owner;
       	if (owner!=null){
       		ObjectProperty ownerProperty = schemaModel.getObjectProperty( Namespace.scmNS + "owner" );   
       		indv.addProperty(ownerProperty, owner.getIndividual());
       	}

       	//documenter;
       	if (documenter!=null){
       		Property documenterProperty = schemaModel.getProperty( Namespace.doapNS + "documenter" );   
       		indv.addProperty(documenterProperty, documenter.getIndividual());
       	}

       	//maintainer;
       	if (maintainer!=null){
       		Property maintainerProperty = schemaModel.getProperty( Namespace.doapNS + "maintainer" );   
       		indv.addProperty(maintainerProperty, maintainer.getIndividual());
       	}

       	//tester;
       	if (tester!=null){
       		Property testerProperty = schemaModel.getProperty( Namespace.doapNS + "tester" );   
       		indv.addProperty(testerProperty, tester.getIndividual());
       	}

       	//isRepositoryOf;
       	if (isRepositoryOf!=null){
       		ObjectProperty isRepositoryOfProperty = schemaModel.getObjectProperty( Namespace.scmNS + "isRepositoryOf" );   
       		indv.addProperty(isRepositoryOfProperty, isRepositoryOf.getIndividual());
       	}
       	       	        
		return instanceModel;    	
    }
        
    public String getRdfModel(String rdfFormat){
    	OntModel outputModel = instanceModel;
	    ByteArrayOutputStream output= new ByteArrayOutputStream();
	    outputModel.writeAll(output, rdfFormat);
	    return output.toString();
    }
}
