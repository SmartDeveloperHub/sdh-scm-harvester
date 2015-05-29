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

import org.sdh.vocabulary.scm.Namespace;
import org.sdh.vocabulary.scm.external.foaf.Person;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;

public class Commit extends RDFResource {

	Action subClassOf;
	Literal createdOn;
	Person performedBy;
	
	public Commit(OntModel schemaModel, OntModel instanceModel) {
		super(schemaModel, instanceModel);
		// TODO Auto-generated constructor stub
	}

	
	public Action getSubClassOf() {
		return subClassOf;
	}


	public void setSubClassOf(Action subClassOf) {
		this.subClassOf = subClassOf;
	}


	public Literal getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Literal createdOn) {
		this.createdOn = createdOn;
	}

	public Person getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(Person performedBy) {
		this.performedBy = performedBy;
	}

	Individual getIndividual(){
		OntClass commitClass = schemaModel.getOntClass(Namespace.scmNS+"Commit" );
		Individual indv = instanceModel.createIndividual(commitClass);
		
		//createdOn
    	if (createdOn!=null){
	       	DatatypeProperty createdOnProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "createdOn");
	       	indv.addLiteral(createdOnProperty, createdOn);
    	}	
    	
    	//performedBy;
       	if (performedBy!=null){
       		ObjectProperty performedByProperty = schemaModel.getObjectProperty( Namespace.scmNS + "performedBy" );   
       		indv.addProperty(performedByProperty, performedBy.getIndividual());
       	}
       	
       	//subClassOf
       	if (subClassOf!=null){
       		Property subClassOfProperty = schemaModel.getProperty(Namespace.rdfsNS + "subClassOf");
	       	indv.addProperty(subClassOfProperty, subClassOf.getIndividual());
    	}
    	
    	return indv;
	}

}

