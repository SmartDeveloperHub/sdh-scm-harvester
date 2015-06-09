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

import org.glassfish.jersey.server.ResourceConfig;
import org.sdh.harvester.constants.GlobalVariablesInitializer;
import org.sdh.harvester.scm.handler.CommitHandler;

@Path("/repositories/{repositoryId}/branches/{branchName}/commits")
public class CommitEndPoint extends EndPoint{
	
	 public CommitEndPoint(@Context ServletContext servletContext) {
		super(servletContext);
		// TODO Auto-generated constructor stub
	}
		
	    @GET @Path("/{commitId}")
	    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
	    public String getCommit(@PathParam("repositoryId") String repositoryId, @PathParam("branchName") String branchName, @PathParam("commitId") String commitId) {
	    	System.out.println("getBranch");
	    	String responseContent="";
	    	Client client = ClientBuilder.newClient();
	    	WebTarget webTarget = client.target("http://192.168.0.10:5000/api/");    	
	    	WebTarget resourceWebTarget = webTarget.path("projects").path(repositoryId).path("commits").path(commitId);    	
	    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
	    	Response response = invocationBuilder.get();
	    	System.out.println("response status:"+response.getStatus());
	    	
	    	CommitHandler handler = new CommitHandler();
	    	handler.setRepositoryId(Integer.valueOf(repositoryId));
	    	handler.setBranchId(branchName);
	    		    	
	    		    	
	    	String rdf=handler.processCommit(response.readEntity(InputStream.class), "TTL");
	    	
	    	responseContent =response.readEntity(String.class);    	   
	    	System.out.println(responseContent);
	    	
	        return rdf;    		    		    
	    	
	    }

}
