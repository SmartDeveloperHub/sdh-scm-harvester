/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015-2016 Center for Open Middleware.
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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.rest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryClient extends ScmClient {
	private static final Logger LOGGER=LoggerFactory.getLogger(RepositoryClient.class);

	public RepositoryClient(final String scmRestService) {
		super(scmRestService);
	}

	public String getRepositories() throws Exception{
		int attempts=0;
		while(attempts<this.maxAttempts){
			attempts++;
			final CloseableHttpClient httpclient = HttpClients.createDefault();
			try{
				final HttpGet httpGet = new HttpGet(this.scmRestService+"/projects");
				LOGGER.info("Call {}",httpGet.getURI());
				httpGet.addHeader("accept", "application/json");
				final CloseableHttpResponse response1 = httpclient.execute(httpGet);
				try{
					final int status = response1.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						LOGGER.info("response {}",status);
						final HttpEntity entity = response1.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						LOGGER.info("HTTP GET fail with response code {}",response1.getStatusLine());
					}
				} catch(final Exception e){
					LOGGER.info("Not raised Exception {}",e);
				} finally {
					response1.close();
				}
			} catch(final Exception e){
				LOGGER.info("Not raised Exception {}",e);
			} finally {
				httpclient.close();
			}
		}
		throw new Exception("Maximum attempts for HTTP GET reached");
	}

	public String getRepository(final String repoId) throws Exception{
		int attempts=0;
		while(attempts<this.maxAttempts){
			attempts++;
			final CloseableHttpClient httpclient = HttpClients.createDefault();
			try{
				final HttpGet httpGet = new HttpGet(this.scmRestService+"/projects/"+repoId);
				LOGGER.info("Call {}",httpGet.getURI());
				httpGet.addHeader("accept", "application/json");
				final CloseableHttpResponse response1 = httpclient.execute(httpGet);
				try{
					final int status = response1.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						LOGGER.info("response {}",status);
						final HttpEntity entity = response1.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						LOGGER.info("HTTP GET fail with response code {}",response1.getStatusLine());
					}
				} catch(final Exception e){
					LOGGER.info("Not raised Exception {}",e);
				} finally {
					response1.close();
				}
			} catch(final Exception e){
				LOGGER.info("Not raised Exception {}",e);
			} finally {
				httpclient.close();
			}
		}
		throw new Exception("Maximum attempts for HTTP GET reached");
	}

}
