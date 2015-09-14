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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.jersey:scm-harvester-jersey:0.2.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.scm;

import java.io.InputStream;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SCMOntology {
	
	private Reasoner getReasoner(){
		//load ontology schema
		System.out.println("init test");
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target("http://localhost:9090/scmharvester/webapi");
    	
    	WebTarget resourceWebTarget = webTarget.path("ontology");
    	Invocation.Builder invocationBuilder = resourceWebTarget.request("text/turtle");
    	Response response = invocationBuilder.get();    	
    	InputStream scmOntologyIs = response.readEntity(InputStream.class);
    	
    	System.out.println("Loading ontology");
    	OntModel schemaModel= ModelFactory.createOntologyModel();
    	schemaModel.read(scmOntologyIs, null, "TTL" );
    	System.out.println("done!");
    	
//    	System.out.println("Merging instance and ontology");
//    	Model populatedModel=ModelFactory.createUnion(schemaModel, schemaModel);
//    	System.out.println("Done!");
    	
    	System.out.println("Reasoning");
    	Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
    	reasoner = reasoner.bindSchema(schemaModel);
    	return reasoner;
	}
	
	public boolean ValidateInstances(OntModel instanceModel){
		Reasoner reasoner = getReasoner();
		
		InfModel inf = ModelFactory.createInfModel(reasoner, instanceModel);
		System.out.println("Done!");
		
		System.out.println("Validating");
		ValidityReport validity = inf.validate();
		if (validity.isValid()) {
		    System.out.println("OK");
		    return true;
		} else {
		    System.out.println("Conflicts");
		    for (Iterator<Report> i = validity.getReports(); i.hasNext(); ) {
		        System.out.println(" - " + i.next());
		    }
		    return false;
		}	
	}
	
	
}
