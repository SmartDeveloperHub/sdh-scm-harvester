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
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.util.Closeables;

abstract class AbstractClient {

	private static final String API_FAILURE_RESPONSE = "false";
	private static final String API_MIME             = "application/json";
	private static final int    MAX_ATTEMPTS         = 5;

	private final Logger logger; // NOSONAR
	private final String apiBase;

	public AbstractClient(final String apiBase) {
		this.apiBase=apiBase;
		this.logger=LoggerFactory.getLogger(getClass());
	}

	protected final String getResource(final String path, final Object... args) throws IOException {
		CloseableHttpClient client=null;
		try {
			client=HttpClients.createDefault();
			final String resource = this.apiBase+String.format(path,args);
			int attempts=0;
			while(attempts<MAX_ATTEMPTS) {
				attempts++;
				final String response = attemptRetrieval(client,resource);
				if(response!=null) {
					return response;
				}
			}
			throw new IOException("Could not retrieve '"+resource+"' after "+MAX_ATTEMPTS+" attempts");
		} finally {
			Closeables.closeQuietly(client);
		}
	}

	private String attemptRetrieval(final CloseableHttpClient client, final String resourcePath) throws IOException {
		final HttpGet httpRequest = new HttpGet(resourcePath);
		httpRequest.addHeader(HttpHeaders.ACCEPT,API_MIME);
		this.logger.info("GET {}",httpRequest.getURI());
		CloseableHttpResponse httpResponse=null;
		try {
			httpResponse = client.execute(httpRequest);
			final StatusLine statusLine = httpResponse.getStatusLine();
			this.logger.info("{}",statusLine);
			String result=null;
			if(statusLine.getStatusCode()==200) {
				final HttpEntity entity = httpResponse.getEntity();
				result = entity != null ? EntityUtils.toString(entity) : null;
				if(API_FAILURE_RESPONSE.equals(result)) {
					throw new ServiceFailureException(resourcePath,statusLine);
				}
			} else if(!canRetry(statusLine.getStatusCode())) {
				throw new ServiceFailureException(resourcePath,statusLine);
			}
			return result;
		} finally {
			Closeables.closeQuietly(httpResponse);
		}
	}

	private boolean canRetry(final int status) {
		return status==429 || status==503 || status==504;
	}

}
