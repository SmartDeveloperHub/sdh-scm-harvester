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

import org.sdh.harvester.constants.AlternativeURI;
import org.sdh.harvester.constants.Namespace;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SCMVocabulary extends RDFResource {
	
	String harvesterId;
	

	public SCMVocabulary(OntModel schemaModel, OntModel instanceModel) {
		super(schemaModel, instanceModel);
	}
	
	
	
	public String getHarvesterId() {
		return harvesterId;
	}



	public void setHarvesterId(String harvesterId) {
		this.harvesterId = harvesterId;
	}



	public OntModel getIndividualModel(){
		   return getIndividual().getOntModel();
	}
	
	public Individual getIndividual(){	   
		System.out.println("vocabulary.getIndividual");
		if (schemaModel==null)
			System.out.println("schemaModel Null");
		if (instanceModel==null)
			System.out.println("instanceModel Null");
		
		//vocabulary;
		OntClass scmVocabularyClass = schemaModel.getOntClass(Namespace.scmNS+"SCMVocabulary");
		Individual vocabularyIndividual = instanceModel.createIndividual(Namespace.scmIndividualNS+"harvester/scm/"+harvesterId+"/vocabulary", scmVocabularyClass);
		
		OntClass vocabularyClass = schemaModel.getOntClass(Namespace.platformNS+"Vocabulary");	
		Property typeProperty = schemaModel.getProperty(Namespace.rdfNS+"type");
		vocabularyIndividual.addProperty(typeProperty, vocabularyClass);
		
		//source: this property points to the ontology uri
		DatatypeProperty  source = schemaModel.getDatatypeProperty(Namespace.platformNS + "source");		
		instanceModel.addLiteral(vocabularyIndividual, source, ResourceFactory.createTypedLiteral(AlternativeURI.scmOntologyEndpoint+"ontology/scm", XSDDatatype.XSDanyURI) );
		//vocabularyIndividual.addProperty(source,AlternativeURI.scmOntologyEndpoint+"ontology/scm");
		
		
		return vocabularyIndividual;
		
	}
	
	public Resource getResource(){
		return instanceModel.createResource(Namespace.scmIndividualNS+"harvester/scm/"+harvesterId+"/vocabulary");
	}

}
