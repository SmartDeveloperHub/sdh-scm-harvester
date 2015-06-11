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
import java.util.Iterator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.sdh.harvester.constants.GitlabEnhancerConstants;
import org.sdh.harvester.constants.GlobalVariablesInitializer;
import org.sdh.harvester.constants.Namespace;
import org.sdh.vocabulary.scm.ScmOntology;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;

public class HarvesterEndPointTest {
	
	//@Test
	public void testHarvesterIndividual() throws NamingException{				
		OntModel instanceModel = getHarvester();
    	
    	SCMOntology scmOnto = new SCMOntology();
    	
    	assertTrue(scmOnto.ValidateInstances(instanceModel));    	    	
	}
	
	public OntModel getHarvester(){
		System.out.println("init test");
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target("http://localhost:9090/scmharvester/webapi");    	
    	WebTarget resourceWebTarget = webTarget.path("harvester").path("scm").path("gitlab");
    	Invocation.Builder invocationBuilder = resourceWebTarget.request("text/turtle");
    	
    	System.out.println("get harvester");
    	Response response = invocationBuilder.get();        	
    	InputStream harvesterIs = response.readEntity(InputStream.class);
    	System.out.println("done!");
    	
    	System.out.println("Loading instance data");
    	
//    	java.util.Scanner s = new java.util.Scanner(harvesterIs).useDelimiter("\\A");
//    	String content = s.hasNext()? s.next(): "";
//    	System.out.print(content);
    	OntModel instanceModel= ModelFactory.createOntologyModel();
    	instanceModel.read(harvesterIs, null, "TTL" );
    	System.out.println("Done!");
    	
    	return instanceModel;
	}
	

}
