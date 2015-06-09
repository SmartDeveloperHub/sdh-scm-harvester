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

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.sdh.harvester.scm.handler.UserHandler;

/**
 * Root resource (exposed at "user" path)
 */
@Path("users")
public class UserEndPoint extends EndPoint{
	
	 public UserEndPoint(@Context ServletContext servletContext) {
		super(servletContext);
		// TODO Auto-generated constructor stub
	}

	UserHandler userHandler;
	    /**
	     * Method handling HTTP GET requests. The returned object will be sent
	     * to the client as "text/plain" media type.
	     *
	     * @return String that will be returned as a text/plain response.
	     */
	    @GET
	    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
	    public String getUsers() {	    	
	    	Client client = ClientBuilder.newClient();
	    	WebTarget webTarget = client.target("http://192.168.0.10:5000/api/");    	
	    	WebTarget resourceWebTarget = webTarget.path("users");
	    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
	    	Response response = invocationBuilder.get();
	    	System.out.println("response status:"+response.getStatus());
	    	
	    	userHandler = new UserHandler();
	    	String rdf=userHandler.processUsers(response.readEntity(InputStream.class), "TTL");
				    	
	        return rdf;
	    }	
	    
	    @GET @Path("/{userId}")
	    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
	    public String getUser(@PathParam("userId") String userId) {
	    	Client client = ClientBuilder.newClient();
	    	WebTarget webTarget = client.target("http://192.168.0.10:5000/api/");    	
	    	WebTarget resourceWebTarget = webTarget.path("users").path(userId);
	    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
	    	Response response = invocationBuilder.get();
	    	System.out.println("response status:"+response.getStatus());
	    	
	    	userHandler = new UserHandler();
	    	String rdf=userHandler.processUser(response.readEntity(InputStream.class), "TTL");
				    	
	        return rdf;
	    }
	
}
