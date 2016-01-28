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

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScmClient {

	protected static final int MAX_ATTEMPTS=5;

	private final Logger logger; // NOSONAR

	private final String scmRestService;

	public ScmClient(final String scmRestService) {
		this.scmRestService=scmRestService;
		this.logger=LoggerFactory.getLogger(getClass());
	}

	protected final String getResource(final String resourcePath) throws IOException {
		int attempts=0;
		while(attempts<MAX_ATTEMPTS){
			attempts++;
			try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
				final HttpGet httpGet = new HttpGet(this.scmRestService+resourcePath);
				this.logger.info("Call {}",httpGet.getURI());
				httpGet.addHeader("accept", "application/json");
				try(CloseableHttpResponse response1 = httpclient.execute(httpGet)){
					final int status = response1.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						this.logger.info("response {}",status);
						final HttpEntity entity = response1.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						this.logger.info("HTTP GET fail with response code {}",response1.getStatusLine());
					}
				} catch(final Exception e){
					this.logger.info("Not raised Exception {}",e);
				}
			} catch(final Exception e){
				this.logger.info("Not raised Exception {}",e);
			}
		}
		throw new IOException("Maximum attempts for HTTP GET reached");
	}

}
