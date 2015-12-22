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
package org.sdh.vocabulary.scm.model;

import java.util.ArrayList;

import org.sdh.harvester.constants.Namespace;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class SCMHarvester extends RDFResource{ 

	ArrayList<Repository> hasRepository;
	SCMVocabulary   vocabulary;
	String id;
	
	public SCMHarvester(OntModel schemaModel, OntModel instanceModel) {
		super(schemaModel, instanceModel);
		hasRepository = new ArrayList<Repository>();
		vocabulary = new SCMVocabulary(schemaModel, instanceModel);
	}
	
	
	public ArrayList<Repository> getHasRepository() {
		return hasRepository;
	}


	public void setHasRepository(ArrayList<Repository> hasRepository) {
		this.hasRepository = hasRepository;
	}


	public SCMVocabulary getVocabulary() {
		return vocabulary;
	}


	public void setVocabulary(SCMVocabulary vocabulary) {
		this.vocabulary = vocabulary;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public OntModel getIndividualModel(){
		   return getIndividual().getOntModel();
	}		

	public Individual getIndividual(){	   
			System.out.println("harvester.getIndividual");
			OntClass scmHarvesterClass = schemaModel.getOntClass(Namespace.scmNS+"SCMHarvester" );
			Individual indv = instanceModel.createIndividual(Namespace.scmIndividualNS+"harvester/scm/"+id, scmHarvesterClass);
			
			//is a Harvester	    	
			OntClass harvesterClass = schemaModel.getOntClass(Namespace.platformNS+"Harvester");
			Property typeProperty = schemaModel.getProperty(Namespace.rdfNS+"type");
			indv.addProperty(typeProperty, harvesterClass);
			
	    	//vocabulary 		
			vocabulary.setHarvesterId(id);
			ObjectProperty vocabularyProperty = schemaModel.getObjectProperty(Namespace.platformNS+"vocabulary");
			indv.addProperty(vocabularyProperty, vocabulary.getIndividual());					
			
			//hasRepository		    	       	
	    	if (hasRepository!=null){
	       		ObjectProperty hasRepositoryProperty = schemaModel.getObjectProperty( Namespace.scmNS + "hasRepository" );   
	       		for (Repository repo:hasRepository){
	       			indv.addProperty(hasRepositoryProperty, repo.getResource());
	       		}
	       	}
	    		    		    	    	
	   
			return indv;
			
		}
		
		public Resource getResource(){
			return instanceModel.createResource(Namespace.scmIndividualNS+"harvester/scm/"+id);
		}
}
