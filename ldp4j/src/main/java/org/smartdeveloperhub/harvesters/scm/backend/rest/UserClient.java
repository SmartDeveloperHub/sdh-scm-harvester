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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.2.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class UserClient extends ScmClient {
    
    public UserClient(String scmRestService) {
		super(scmRestService);
		// TODO Auto-generated constructor stub
	}

	public InputStream getUser(String userId) throws URISyntaxException, UnsupportedOperationException, IOException {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		HttpGet httpGet = new HttpGet(scmRestService+"/users/"+userId);
		System.out.println("*"+httpGet.getURI());
		
		httpGet.addHeader("accept", "application/json");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		try {
		    System.out.println(response1.getStatusLine());
		    HttpEntity entity1 = response1.getEntity();
		    return entity1.getContent();
		} finally {
		  //  response1.close();
		}
//    	Client client = ClientBuilder.newClient();
//    	WebTarget webTarget = client.target(scmRestService);    	
//    	WebTarget resourceWebTarget = webTarget.path("users").path(userId);
//    	Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON);
//    	Response response = invocationBuilder.get();
//    	System.out.println("response status:"+response.getStatus());
//    	
//    	return response.readEntity(InputStream.class); 	

    }

}