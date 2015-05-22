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
	Location location;
	Location codebase;
	//XSDDateTime
	Literal createdOn;
	Literal firstCommit;
	Literal lastBuildDate;
	Literal lastCommit;
	
	//Strings
	Literal isArchived;
	Literal isPublic;
	Literal defaultBranchName;
	Literal description;
	Literal lastBuildStatus;
	Literal name;
    Literal repositoryId;
    Literal tags;
	
	Branch defaultBranch;
	ArrayList<Branch> hasBranch;
	
	Image depiction; 
    
    Person	developer;
    Person documenter;
    Person mainteiner;
    Person tester;
    
    Project isRepositoryOf;
    
    Individual getIndividual(OntModel model, String NS){
    	//OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
    	OntClass repositoryClass = model.getOntClass( NS + "Programme" );
    	Individual repositoryInst = repositoryClass.createIndividual();   	
    	
    	//location
       	ObjectProperty locationProperty = model.getObjectProperty( NS + "location" );   
       	repositoryInst.addProperty(locationProperty, location.getIndividual());
       	
        //codebase
       	ObjectProperty codebaseProperty = model.getObjectProperty( NS + "codebase" );   
       	repositoryInst.addProperty(codebaseProperty, location.getIndividual());
    	
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
