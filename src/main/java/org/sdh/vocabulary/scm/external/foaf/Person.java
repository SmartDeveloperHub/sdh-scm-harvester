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
package org.sdh.vocabulary.scm.external.foaf;

import org.sdh.vocabulary.scm.Namespace;
import org.sdh.vocabulary.scm.model.RDFResource;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

public class Person extends RDFResource{
	
	
	public Person(OntModel instanceModel, OntModel schemaModel) {
		super(instanceModel, schemaModel);
		// TODO Auto-generated constructor stub
	}

	String personClass;
	
	Literal name;
	Literal userId;
	Literal firstCommit;
	Literal lastCommit;
	Literal signUpDate;
	
	Literal mbox;	
	
	//acount -> OnlineAccount
	Image img	;
	
	public Literal getName() {
		return name;
	}

	public String getPersonClass() {
		return personClass;
	}

	public void setPersonClass(String personClass) {
		this.personClass = personClass;
	}

	public void setName(Literal name) {
		this.name = name;
	}

	public Literal getUserId() {
		return userId;
	}

	public void setUserId(Literal userId) {
		this.userId = userId;
	}

	public Literal getFirstCommit() {
		return firstCommit;
	}

	public void setFirstCommit(Literal firstCommit) {
		this.firstCommit = firstCommit;
	}

	public Literal getLastCommit() {
		return lastCommit;
	}

	public void setLastCommit(Literal lastCommit) {
		this.lastCommit = lastCommit;
	}

	public Literal getSignUpDate() {
		return signUpDate;
	}

	public void setSignUpDate(Literal signUpDate) {
		this.signUpDate = signUpDate;
	}

	public Literal getMbox() {
		return mbox;
	}

	public void setMbox(Literal mbox) {
		this.mbox = mbox;
	}
		
	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}

	public Individual getIndividual(){
		OntClass personClass = schemaModel.getOntClass(Namespace.foafNS+"Person");
		Individual indv = instanceModel.createIndividual("http://localhost:9090/scmharvester/webapi/user/"+userId, personClass);
		
		//firstCommit
    	if (firstCommit!=null){
	       	DatatypeProperty firstCommitProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "firstCommit");
	       	indv.addLiteral(firstCommitProperty, firstCommit);
    	}
    	
        //lastCommit
    	if (lastCommit!=null){
	       	DatatypeProperty lastCommitProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "lastCommit");
	       	indv.addLiteral(lastCommitProperty, lastCommit);
    	}
    	
        //signUpDate
    	if (signUpDate!=null){
	       	DatatypeProperty signUpDateProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "signUpDate");
	       	indv.addLiteral(signUpDateProperty, signUpDate);
    	}
    	
    	//name;
       	if (name!=null){
	       	DatatypeProperty nameProperty = schemaModel.getDatatypeProperty(Namespace.foafNS + "name");
	       	indv.addLiteral(nameProperty, name);
       	}
       	
     	//userId;
       	if (userId!=null){
	       	DatatypeProperty userIdProperty = schemaModel.getDatatypeProperty(Namespace.scmNS + "userId");
	       	indv.addLiteral(userIdProperty, userId);
       	}
       	
       	//Image
       	if (img!=null){
       		ObjectProperty imgProperty = schemaModel.getObjectProperty( Namespace.foafNS + "img" );
       		indv.addProperty(imgProperty, img.getIndividual());
       	}
    	
		return indv;
	}

	
}
