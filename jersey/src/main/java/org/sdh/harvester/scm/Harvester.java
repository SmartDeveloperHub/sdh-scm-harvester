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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.jersey:scm-harvester-jersey:0.3.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.scm;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.external.foaf.Image;
import org.sdh.vocabulary.scm.model.Action;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Commit;
import org.sdh.vocabulary.scm.model.Person;
import org.sdh.vocabulary.scm.model.Repository;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.PrintUtil;

public class Harvester {

	static final Logger logger = LogManager.getLogger(Harvester.class);


	private void testRepository(OntModel ontModel,OntModel instModel, String NS) throws UnsupportedEncodingException{
		Repository repo = new Repository(ontModel, instModel);
				
		repo.setLocation(ontModel.createResource(NS+"location"));
				
		repo.setCodebase(ontModel.createResource(NS+"codebase"));
		
		Literal createdOn = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setCreatedOn(createdOn);
		
		Literal firstCommit = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setFirstCommit(firstCommit);
		
		Literal lastBuildDate = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setLastBuildDate(lastBuildDate);
		
		Literal lastCommit = ontModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setLastCommit(lastCommit);
				
		repo.setIsArchived(ontModel.createLiteral("false",true));
				
		repo.setIsPublic(ontModel.createLiteral("boolean",true));
				
		repo.setDefaultBranchName(ontModel.createLiteral("Master",true));
				
		repo.setDescription(ontModel.createLiteral("Development repo",true));
		
		repo.setLastBuildStatus(ontModel.createLiteral("Sucesful",true));
		
		repo.setName(ontModel.createLiteral("Main product repo",true));
	
	    repo.setRepositoryId(ontModel.createLiteral("repo001",true));
	    
	    //repo.setTags(ontModel.createLiteral("repo, development, business",true));
	    
	    //Branch
		Branch branch01 = new Branch(ontModel, instModel);				
		branch01.setName(ontModel.createLiteral("Master Branch",true));		
		branch01.setCreatedOn(createdOn);
		
		Action action01 = new Action(ontModel, instModel);
		branch01.setIsTargetOf(action01);		
				
		Commit commit01 = new Commit(ontModel, instModel);
		Person person01 = new Person(ontModel, instModel);
		//person01.setMbox(ontModel.createResource("andresgs77@hotmail.com"));
		person01.setName(ontModel.createLiteral("andres garcia",true));
		person01.setSignUpDate(ontModel.createLiteral("2015-05-20T21:00:00Z",true));
		person01.setFirstCommit(ontModel.createLiteral("2015-05-20T21:00:00Z",true));
		person01.setLastCommit(ontModel.createLiteral("2015-05-20T21:00:00Z",true));
		
		commit01.setCreatedOn(createdOn);
		commit01.setPerformedBy(person01);
		commit01.setSubClassOf(action01);
		
		ArrayList<Commit> hasCommit = new ArrayList<Commit>();
		hasCommit.add(commit01);
		
		branch01.setHasCommit(hasCommit);
		
		repo.setDefaultBranch(branch01);
		
		//repo.hasBranch
		ArrayList<Branch> hasBranch = new ArrayList<Branch>();
		hasBranch.add(branch01);
		repo.setHasBranch(hasBranch);
		
	
//		Image depiction = new Image();
//		repo.setDepiction(depiction);
//	    
//	    Person developer = new Person(ontModel,instModel);
//	    repo.setDeveloper(developer);
	    
	    		
	    OntModel outputModel = repo.getIndividualModel();
	    ByteArrayOutputStream output= new ByteArrayOutputStream();
	    outputModel.writeAll(output, "TTL");
	    
	    System.out.println(output.toString());
	    //instModel.writeAll(System.out, "TTL");
	    
	    
	}
	
	public void startProcessing() throws UnsupportedEncodingException{
		//PropertiesConfigurator is used to configure logger from properties file
		ClassLoader classLoader = getClass().getClassLoader();
		PropertyConfigurator.configure(classLoader.getResource("log4j.properties").getFile());
		logger.debug("Log4j appender configuration is successful !!");
		
		// Set up a simple configuration that logs on the console.
        //BasicConfigurator.configure();
		
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		OntModel ontModel=onto.getJenaModel();
		String NS = "http://www.smartdeveloperhub.org/vocabulary/sdh/v1/scm#";
		NS = "http://www.smartdeveloperhub.org/vocabulary/scm#";
		
    	OntModel instanceModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		testRepository(ontModel,instanceModel, NS);
	}

	public static void main (String[] argv) throws UnsupportedEncodingException{
				
		Harvester harvester = new Harvester();
		harvester.startProcessing();
	}
}
