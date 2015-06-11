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

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class UserEndPointTest {
	
	   //@Test
	   public void testUser(){
			ArrayList<OntModel> usersModels = getRepositoryUsers();		
			for (OntModel userModel: usersModels){
		    	SCMOntology scmOnto = new SCMOntology();	    	
		    	assertTrue(scmOnto.ValidateInstances(userModel));
		    	break;
			}		    	    	    	
		}
		
		private ArrayList<OntModel> getRepositoryUsers(){
			ArrayList<OntModel> userModels = new ArrayList<OntModel>();
			RepositoryEndPointTest repo = new RepositoryEndPointTest();
			ArrayList<OntModel> repoModels = repo.getHarvesterRepositories();
						
			for (OntModel repoModel:repoModels){				
				OntClass repoClass=repoModel.getOntClass("http://www.smartdeveloperhub.org/vocabulary/scm#Repository"); 
				Property developer=repoModel.getProperty("http://usefulinc.com/ns/doap#developer");
				
				for (ExtendedIterator<? extends OntResource>  instances = repoClass.listInstances(); instances.hasNext();){
					Individual repoIndividual = (Individual) instances.next();
					System.out.println("repo:"+repoIndividual.getURI());
					System.out.println("developer op:"+developer.getURI());
					Resource personReference= repoIndividual.getPropertyResourceValue(developer);					
					OntModel personInstance=getUser(personReference);
					userModels.add(personInstance);
				}
				break;
			}																		
			return userModels;
			
		}

		private OntModel getUser(Resource userReference) {
			System.out.println("init getUser:"+userReference.getURI());
	    	Client client = ClientBuilder.newClient();
	    	WebTarget webTarget = client.target(userReference.getURI());    	    	
	    	Invocation.Builder invocationBuilder = webTarget.request("text/turtle");
	    	
	    	System.out.println("get user");
	    	Response response = invocationBuilder.get();        	
	    	InputStream is = response.readEntity(InputStream.class);
	    	System.out.println("done!");
	    	
	    	System.out.println("Loading instance data");
	    	
//	    	java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
//	    	String content = s.hasNext()? s.next(): "";
//	    	System.out.print(content);
	    	
	    	OntModel instanceModel= ModelFactory.createOntologyModel();
	    	instanceModel.read(is, null, "TTL" );
	    	System.out.println("Done!");
	    	return instanceModel;
		}

}
