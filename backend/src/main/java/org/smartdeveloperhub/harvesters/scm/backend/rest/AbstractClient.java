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

abstract class AbstractClient {

	protected static final int MAX_ATTEMPTS=5;

	private final Logger logger; // NOSONAR

	private final String scmRestService;

	public AbstractClient(final String scmRestService) {
		this.scmRestService=scmRestService;
		this.logger=LoggerFactory.getLogger(getClass());
	}

	protected final String getResource(final String path, final Object... args) throws IOException {
		int attempts=0;
		while(attempts<MAX_ATTEMPTS){
			attempts++;
			try(CloseableHttpClient client=HttpClients.createDefault()) {
				return attemptRetrieval(client,String.format(path,args));
			} catch(final Exception e){
				this.logger.warn("Ignored exception",e);
			}
		}
		throw new IOException("Maximum attempts for HTTP GET reached");
	}

	/**
	 * TODO: Refine the behavior on failure (e.g., should we follow
	 * redirections, show we re-attempt on 4XX, what do we do with 5XX?) and
	 * whether or not we expect a body...
	 */
	private String attemptRetrieval(final CloseableHttpClient client, final String resourcePath) throws IOException {
		final HttpGet httpGet = new HttpGet(this.scmRestService+resourcePath);
		httpGet.addHeader("accept", "application/json");
		this.logger.info("Call {}",httpGet.getURI());
		try(CloseableHttpResponse response = client.execute(httpGet)){
			final int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				this.logger.info("Response: {}",response.getStatusLine());
				final HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				throw new IOException("Resource retrieval failed with response code "+response.getStatusLine());
			}
		}
	}

}
