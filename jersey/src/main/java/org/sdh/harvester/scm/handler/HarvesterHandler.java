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
package org.sdh.harvester.scm.handler;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.sdh.harvester.constants.GitlabEnhancerConstants;
import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Repository;
import org.sdh.vocabulary.scm.model.SCMHarvester;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class HarvesterHandler {

	RepositoryHandler repoHandler;
	private OntModel schemaModel;
	private OntModel instModel;
	private SCMHarvester harvester;
	
	public String publishHarvester(String rdfFormat) {
		System.out.println("publish Harvester in :"+rdfFormat);    	
		initOntologyModels();
		harvester.setId("gitlab");
		
		ArrayList<Integer> repoIds = repoHandler.getRepositoriesResources();
		ArrayList<Repository> repositoryResources = new ArrayList<Repository>();		
		for(Integer repoId:repoIds){
			repoHandler.initEmbededHandler(schemaModel, instModel);
			repoHandler.getRepo().setRepositoryId(String.valueOf(repoId));			
			repositoryResources.add(repoHandler.getRepo());			
		}
						
		harvester.setHasRepository(repositoryResources);								
    	
		harvester.getIndividualModel();
		
		String rdfRepresentation = harvester.getRdfModel(rdfFormat);
		
		return rdfRepresentation;
		
		
		
	}
	private void initOntologyModels() {
			ScmOntology onto = new ScmOntology();
			onto.loadOntology();
			schemaModel=onto.getJenaModel();
			instModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );		
			//repo = new Repository(schemaModel, instModel);
			harvester = new SCMHarvester(schemaModel, instModel);
			repoHandler = new RepositoryHandler();
			

	}

}
