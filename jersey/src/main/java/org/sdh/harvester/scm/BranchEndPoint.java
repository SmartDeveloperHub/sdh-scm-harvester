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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.jersey:scm-harvester-jersey:0.3.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.scm;

import java.io.InputStream;
import java.net.URI;

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
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.server.ResourceConfig;
import org.sdh.harvester.constants.GitlabEnhancerConstants;
import org.sdh.harvester.constants.GlobalVariablesInitializer;
import org.sdh.harvester.scm.handler.BranchHandler;

/**
 * Root resource (exposed at "repository" path)
 */
@Path("/repositories/{repositoryId}/branches")
public class BranchEndPoint extends EndPoint{
	
	 public BranchEndPoint(@Context ServletContext servletContext) {
		super(servletContext);
		// TODO Auto-generated constructor stub
	}

	/**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	@GET
    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
    public String getBranchesTTL(@PathParam("repositoryId") String repoId, @Context UriInfo uriInfo) {
		
    	String rdf=getBranch(repoId, turtleJena);   	    	
        return rdf;    	
    }
	
	@GET
    @Produces({rdfXmlMediaType})
    public String getBranchesRDF(@PathParam("repositoryId") String repoId, @Context UriInfo uriInfo) {   	
    	String rdf=getBranch(repoId, rdfXmlJena);   	
        return rdf;    	
    }
	
	private String getBranch(String repoId,  String format){
		InputStream branchesIS=getBranchesFromEnhancer(repoId);
    	
    	BranchHandler handler = new BranchHandler();
    	handler.setRepositoryId(repoId);
   	
    	String rdf=handler.processBranches(branchesIS, format);   	
    	return rdf;
	}
	
	private InputStream getBranchesFromEnhancer(String repoId){
		System.out.println("getBranches");    	
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target(GitlabEnhancerConstants.gitlabEnhancerEndpoint);    	
    	WebTarget resourceWebTarget = webTarget.path("projects").path(repoId).path("branches");    	
    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
    	Response response = invocationBuilder.get();
    	
    	System.out.println("response status:"+response.getStatus());   
    	return response.readEntity(InputStream.class);
	}
	
    @GET @Path("/{branchname}")
    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
    public String getBranchTTL(@PathParam("repositoryId") String repoId, @PathParam("branchname") String branchName ) {
    	
    	String rdf = getBranch(repoId, branchName, turtleJena);    	
    	
        return rdf;    	
    }
    
    @GET @Path("/{branchname}")
    @Produces({rdfXmlMediaType})
    public String getBranchRDF(@PathParam("repositoryId") String repoId, @PathParam("branchname") String branchName ) {
    	
    	String rdf = getBranch(repoId, branchName, rdfXmlJena);

        return rdf;    	
    }
    
    private String getBranch(String repoId, String branchName, String format){
    	InputStream branchesIS = getBranchFromEnhancer(repoId, branchName);
    	InputStream commitsIS = getCommitsFromEnhancer(repoId, branchName);
    	BranchHandler handler = new BranchHandler();
    	handler.setRepositoryId(repoId);
    	handler.setId(branchName);
    	    	
    	String rdf=handler.processBranch(branchesIS,commitsIS, format);
    	    	
    	//responseContent =response.readEntity(String.class);    	   
    	//System.out.println(responseContent);
        return rdf;    	
    }
    
    private InputStream getBranchFromEnhancer(String repoId, String branchName){
    	System.out.println("getBranch:"+branchName);    	
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target(GitlabEnhancerConstants.gitlabEnhancerEndpoint);    	
    	WebTarget resourceWebTarget = webTarget.path("projects").path(repoId).path("branches").path(branchName);    	
    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
    	Response response = invocationBuilder.get();
    	
    	System.out.println("response status:"+response.getStatus());
    	return response.readEntity(InputStream.class);
    }

    private InputStream getCommitsFromEnhancer(String repoId, String branchName){
    	System.out.println("getCommits Branch:"+branchName);  
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target(GitlabEnhancerConstants.gitlabEnhancerEndpoint);
    	WebTarget resourceWebTarget = webTarget.path("projects").path(repoId).path("branches").path(branchName).path("commits"); //replace for next line when commits per branch are available    	//
    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
    	Response commitsResponse = invocationBuilder.get();
    	System.out.println("response status:"+commitsResponse.getStatus());
    	return commitsResponse.readEntity(InputStream.class);
    }
}
