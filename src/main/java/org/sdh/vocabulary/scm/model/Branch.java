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
import org.sdh.vocabulary.scm.ScmOntology;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
	String repo;
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
	
	public void setCreatedOn(Date createdOn){
		DateTime dateTime = new DateTime(createdOn);
		this.createdOn = schemaModel.createLiteral(dateTime.toString());
	}

	public Literal getName() {
		return name;
	}


	public void setName(Literal name) {
		this.name = name;
	}
	
	public void setName(String name){
		this.name = schemaModel.createLiteral(name);
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

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
	}

	public OntModel getIndividualModel(){
	   return getIndividual().getOntModel();
	}
	

	
	public Individual getIndividual(){	   
		System.out.println("branch.getIndividual");
		OntClass branchClass = schemaModel.getOntClass(Namespace.scmNS+"Branch" );
		Individual indv = instanceModel.createIndividual(Namespace.scmIndividualNS+"repositories/"+repo+"/branches/"+name, branchClass);
		
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
    	if (isTargetOf!=null){
    		ObjectProperty isTargetOfProperty = schemaModel.getObjectProperty( Namespace.scmNS + "isTargetOf" );   
    		indv.addProperty(isTargetOfProperty, isTargetOf.getIndividual());
    	}
    	
		return indv;
		
	}
	
	public Resource getResource(){
		return schemaModel.createResource(Namespace.scmIndividualNS+"repositories/"+repo+"/branches/"+name);
	}

	
//	public String getRdfModel(String rdfFormat){
//    	OntModel outputModel = instanceModel;
//	    ByteArrayOutputStream output= new ByteArrayOutputStream();
//	    outputModel.writeAll(output, rdfFormat);
//	    return output.toString();
//    }
}
