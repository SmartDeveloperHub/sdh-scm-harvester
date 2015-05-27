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
package org.sdh.harvester.scm;

import java.util.ArrayList;

import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.external.foaf.Image;
import org.sdh.vocabulary.scm.external.foaf.Person;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Commit;
import org.sdh.vocabulary.scm.model.Repository;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class Harvester {
	
	private void testRepository(OntModel ontModel, String NS){
		Repository repo = new Repository();
		
		Resource location = ontModel.createResource(NS+"location");
		repo.setLocation(location);
		
		Resource codebase = ontModel.createResource(NS+"codebase");
		repo.setCodebase(codebase);
		
		Literal createdOn = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setCreatedOn(createdOn);
		
		Literal firstCommit = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setFirstCommit(firstCommit);
		
		Literal lastBuildDate = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setLastBuildDate(lastBuildDate);
		
		Literal lastCommit = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setLastCommit(lastCommit);
		
		Literal isArchived = ontModel.createLiteral("false",true);
		repo.setIsArchived(isArchived);
		
		Literal isPublic = ontModel.createLiteral("boolean",true);
		repo.setIsPublic(isPublic);
		
		Literal defaultBranchName = ontModel.createLiteral("Master",true);
		repo.setDefaultBranchName(defaultBranchName);
		
		Literal description = ontModel.createLiteral("This is very nice repo",true);
		repo.setDescription(description);
		
		Literal lastBuildStatus=  ontModel.createLiteral("Sucesful",true);
		repo.setLastBuildStatus(lastBuildStatus);
		
		Literal name  = ontModel.createLiteral("Main product repo",true);
		repo.setName(name);
		
	    Literal repositoryId  = ontModel.createLiteral("repo001",true);
	    repo.setRepositoryId(repositoryId);
	    
	    Literal tags = ontModel.createLiteral("repo, development, business",true);;
	    repo.setTags(tags);
	    
//	    //Missing instance Configuration 
//		Branch defaultBranch = new Branch();		
//		repo.setDefaultBranch(defaultBranch);
//		
//		ArrayList<Branch> hasBranch = new ArrayList<Branch>();
//		//Missing add branches
//		repo.setHasBranch(hasBranch);
//		
//		ArrayList<Commit> hasCommit = new ArrayList<Commit>();
//		//Missing add commits
//		
//		repo.setHasCommit(hasCommit);
//		
//		Image depiction = new Image();
//		repo.setDepiction(depiction);
//	    
//	    Person developer = new Person();
//	    repo.setDeveloper(developer);
//	    
//	    Person documenter = new Person();
//	    repo.setDocumenter(documenter);
//	    
//	    Person maintainer = new Person();
//	    repo.setmaintainer(maintainer);
//	    
//	    Person tester = new Person();
//	    repo.setTester(tester);
	    
	    		
	    Individual ontRep = repo.getIndividual(ontModel, NS);
	    System.out.println(ontRep.toString());
	    
	    
	}

	public static void main (String[] argv){
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		OntModel ontModel=onto.getJenaModel();
		Harvester harvester = new Harvester();
		
		String NS = "http://www.smartdeveloperhub.org/vocabulary/sdh/v1/scm#";
		NS = "http://www.smartdeveloperhub.org/vocabulary/scm#";
		harvester.testRepository(ontModel,NS);
	}
}
