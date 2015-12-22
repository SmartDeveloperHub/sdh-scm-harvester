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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryClient extends ScmClient {
	private static final Logger LOGGER=LoggerFactory.getLogger(RepositoryClient.class);
	
	public RepositoryClient(String scmRestService) {
		super(scmRestService);
		// TODO Auto-generated constructor stub
	}

//	public InputStream getRepositories() throws Exception{
	public String getRepositories() throws Exception{
		int attempts=0;
		while(attempts<maxAttempts){
			attempts++;
			CloseableHttpClient httpclient = HttpClients.createDefault();
	        try{
				HttpGet httpGet = new HttpGet(scmRestService+"/projects");
				LOGGER.info("Call {}",httpGet.getURI());
				httpGet.addHeader("accept", "application/json");
				CloseableHttpResponse response1 = httpclient.execute(httpGet);
				try{
		        	int status = response1.getStatusLine().getStatusCode();
			        if (status >= 200 && status < 300) {
						LOGGER.info("response {}",status);		    
						HttpEntity entity = response1.getEntity();
	                    return entity != null ? EntityUtils.toString(entity) : null;	
			         }
			        else {
						 LOGGER.info("HTTP GET fail with response code {}",response1.getStatusLine());						       
				     }
				}
				catch(Exception e){
					LOGGER.info("Not raised Exception {}",e.getMessage());
				} finally {				
					response1.close();		 		      
			    }
			}
			catch(Exception e){
				LOGGER.info("Not raised Exception {}",e.getMessage());
			} finally {				
				httpclient.close();
			}		    
		}
		throw new Exception("Maximum attempts for HTTP GET reached");		
	}
	
//	public InputStream getRepository(String repoId) throws Exception{
	public String getRepository(String repoId) throws Exception{
		int attempts=0;
		while(attempts<maxAttempts){
			attempts++;
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try{
				HttpGet httpGet = new HttpGet(scmRestService+"/projects/"+repoId);
				LOGGER.info("Call {}",httpGet.getURI());				
				httpGet.addHeader("accept", "application/json");
				CloseableHttpResponse response1 = httpclient.execute(httpGet);
				try{
		        	int status = response1.getStatusLine().getStatusCode();
			        if (status >= 200 && status < 300) {
						LOGGER.info("response {}",status);		    
						HttpEntity entity = response1.getEntity();
	                    return entity != null ? EntityUtils.toString(entity) : null;
			         }
			        else {
						 LOGGER.info("HTTP GET fail with response code {}",response1.getStatusLine());						       
				     }
				}
				catch(Exception e){
					LOGGER.info("Not raised Exception {}",e.getMessage());
				} finally {				
					response1.close();		 		      
			    }
			}
			catch(Exception e){
				LOGGER.info("Not raised Exception {}",e.getMessage());
			} finally {				
				httpclient.close();
			}		    
		}
		throw new Exception("Maximum attempts for HTTP GET reached");		
	}
	
}
