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

import org.sdh.vocabulary.scm.Namespace;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Branch extends RDFResource{

	Literal createdOn;
	Literal name;
	ArrayList<Commit>  hasCommit;
	Action  isTargetOf;

	public Branch(OntModel schemaModel, OntModel instanceModel) {
		super(schemaModel, instanceModel);
		hasCommit = new ArrayList<Commit>();
		
	}
		
	public Literal getCreatedOn() {
		return createdOn;
	}


	public void setCreatedOn(Literal createdOn) {
		this.createdOn = createdOn;
	}


	public Literal getName() {
		return name;
	}


	public void setName(Literal name) {
		this.name = name;
	}


	public ArrayList<Commit> getHasCommit() {
		return hasCommit;
	}


	public void setHasCommit(ArrayList<Commit> hasCommit) {
		this.hasCommit = hasCommit;
	}


	public Action getIsTargetOf() {
		return isTargetOf;
	}


	public void setIsTargetOf(Action isTargetOf) {
		this.isTargetOf = isTargetOf;
	}


	Individual getIndividual(){
		OntClass branchClass = schemaModel.getOntClass(Namespace.scmNS+"Branch" );
		Individual indv = instanceModel.createIndividual(branchClass);
		
		//createdOn
    	if (createdOn!=null){
	       	DatatypeProperty createdOnProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "createdOn");
	       	indv.addLiteral(createdOnProperty, createdOn);
    	}		
		
    	//name;
       	if (name!=null){
	       	DatatypeProperty nameProperty = schemaModel.getDatatypeProperty(Namespace.doapNS + "name");
	       	indv.addLiteral(nameProperty, name);
       	}
       	
       	//hasCommit
    	if (hasCommit!=null){
       		ObjectProperty hasCommitProperty = schemaModel.getObjectProperty( Namespace.scmNS + "hasCommit" );   
       		for (Commit commit:hasCommit){
       			indv.addProperty(hasCommitProperty, commit.getIndividual());
       		}
       	}

    	//isTargetOf Action
    	ObjectProperty isTargetOfProperty = schemaModel.getObjectProperty( Namespace.scmNS + "isTargetOf" );   
    	indv.addProperty(isTargetOfProperty, isTargetOf.getIndividual());
    	
		return indv;
	}
}
