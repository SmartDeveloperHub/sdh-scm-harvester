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
package org.sdh.scm.harvester;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hp.hpl.jena.rdf.model.Model;
//import com.sdh.scm.ontology.ScmOntology;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class Repository {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
    	String responseContent="";
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target("http://192.168.0.10:5000/api/");    	
    	WebTarget resourceWebTarget = webTarget.path("users");
    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
    	Response response = invocationBuilder.get();
    	System.out.println("response status:"+response.getStatus());
    	
    	
////    	ScmOntology onto = new ScmOntology();
//		onto.loadOntology();
//		Model ontoModel=onto.getJenaModel();
		
    	//responseContent =response.readEntity(String.class);
    	try(
    		JsonReader jsonReader = Json.createReader(response.readEntity(InputStream.class))
    		)
    	{    		 
		  JsonArray array = jsonReader.readArray();
		  
		  for (JsonObject user : array.getValuesAs(JsonObject.class)){		  
			  System.out.print("username:" + user.getString("username"));
			  System.out.print("name" + user.getString("name"));
			  System.out.print("email" + user.getString("email"));
			  System.out.print("avatar_url" + user.getString("avatar_url"));
			  System.out.print("website_url" + user.getString("website_url"));
			  System.out.print("created_at" + user.getJsonString("created_at"));
			  //System.out.print(user.getString("created_at"));
		 
			  System.out.println("-----------");
		  }
    	}
    	    	
    	//System.out.println(responseContent);
        return responseContent;
    }
}
