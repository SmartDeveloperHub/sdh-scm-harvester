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

import java.util.Date;

import org.joda.time.DateTime;
import org.sdh.harvester.constants.Namespace;
import org.sdh.vocabulary.scm.external.foaf.Image;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Person extends RDFResource{
	
	
	public Person(OntModel schemaModel, OntModel instanceModel) {
		super(schemaModel, instanceModel);
		// TODO Auto-generated constructor stub
	}

	String personClass;
	
	Literal name;
	Literal userId;
	Literal firstCommit;
	Literal lastCommit;
	Literal signUpDate;	
	
	Resource mbox;
	Resource account;
	
	//acount -> OnlineAccount
	Image img	;
	Resource homepage;
	
	
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
	
	public void setName(String name){
		this.name = schemaModel.createLiteral(name);
	}

	public Literal getUserId() {
		return userId;
	}

	public void setUserId(Literal userId) {
		this.userId = userId;
	}
	
	public void setUserId(String userId) {
		this.userId = schemaModel.createLiteral(userId);
	}	

	public Literal getFirstCommit() {
		return firstCommit;
	}

	public void setFirstCommit(Literal firstCommit) {
		this.firstCommit = firstCommit;
	}

	public void setFirstCommit(Date firstCommit) {
		DateTime dateTime = new DateTime(firstCommit);
		this.firstCommit = schemaModel.createLiteral(dateTime.toString());
	}
	
	public Literal getLastCommit() {
		return lastCommit;
	}

	public void setLastCommit(Literal lastCommit) {
		this.lastCommit = lastCommit;
	}
	
	public void setLastCommit(Date lastCommit) {
		DateTime dateTime = new DateTime(lastCommit);
		this.lastCommit = schemaModel.createLiteral(dateTime.toString());
	}

	public Literal getSignUpDate() {
		return signUpDate;
	}

	public void setSignUpDate(Literal signUpDate) {
		this.signUpDate = signUpDate;
	}
	
	public void setSignUpDate(Date createdAtDate) {
		DateTime dateTime = new DateTime(createdAtDate);
		this.signUpDate = schemaModel.createLiteral(dateTime.toString());
		
	}

	public Resource getMbox() {
		return mbox;
	}

	public void setMbox(Resource mbox) {
		this.mbox = mbox;
	}
	
	public void setMbox(String mbox){
		this.mbox=schemaModel.createResource(mbox);
	}
		
	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}
	
	public Resource getHomepage() {
		return homepage;
	}

	public void setHomepage(Resource homepage) {
		this.homepage = homepage;
	}
	
	public void setHomepage(String homepage){
		this.homepage=schemaModel.createResource(homepage);
	}
	
	public Resource getAccount() {
		return account;
	}

	public void setAccount(Resource account) {
		this.account = account;
	}
	
	public OntModel getIndividualModel(){
		   return getIndividual().getOntModel();
		}

	public Individual getIndividual(){
System.out.println("user.getIndividual");
		OntClass personClass = schemaModel.getOntClass(Namespace.scmNS+"Person");
		Individual indv = instanceModel.createIndividual(Namespace.scmIndividualNS+"users/"+userId, personClass);
		
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
	       	Property signUpDateProperty = schemaModel.getProperty(Namespace.scmNS + "signUpDate");
	       	indv.addLiteral(signUpDateProperty, signUpDate);
    	}
    	
    	//name;
       	if (name!=null){
	        Property nameProperty = schemaModel.getProperty(Namespace.foafNS + "name");	       
	       	indv.addLiteral(nameProperty, name);
       	}
       	
     	//userId;
       	if (userId!=null){
	       	Property userIdProperty = schemaModel.getProperty(Namespace.scmNS + "userId");
	       	indv.addLiteral(userIdProperty, userId);
       	}
       	
       	//Image
       	if (img!=null){
       		Property imgProperty = schemaModel.getProperty( Namespace.foafNS + "img" );
       		indv.addProperty(imgProperty, img.getIndividual());
       	}
       	
       	if (homepage!=null){
       		Property homePageProperty = schemaModel.getProperty( Namespace.foafNS + "homepage" );
       		indv.addProperty(homePageProperty, homepage);
       	}
       	
       	if(account!=null){
       		ObjectProperty accountProperty = schemaModel.getObjectProperty( Namespace.foafNS + "account" );
       		indv.addProperty(accountProperty, account);
       	}
    	
		return indv;
	}

	public Resource getResource(){
		return schemaModel.createResource(Namespace.scmIndividualNS+"users/"+userId);
	}

	
}