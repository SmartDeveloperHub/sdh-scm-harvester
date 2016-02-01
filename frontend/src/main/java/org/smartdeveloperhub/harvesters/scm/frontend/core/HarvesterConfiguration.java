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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.ldp4j.application.ext.Configuration;
import org.ldp4j.application.ext.Namespaces;
import org.ldp4j.application.util.ImmutableNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class HarvesterConfiguration extends Configuration {

	private static final Logger LOGGER=LoggerFactory.getLogger(HarvesterConfiguration.class);

	static final String GITLAB_ENHANCER_SYSTEM_PROPERTY      = "scm.harvester.enhancer";
	static final String GITLAB_ENHANCER_ENVIRONMENT_VARIABLE = "GITLAB_ENHANCER";

	private URI target=null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Namespaces namespaces() {
		return
			new ImmutableNamespaces().
				withPrefix("scm", "http://www.smartdeveloperhub.org/vocabulary/scm#").
				withPrefix("platform", "http://www.smartdeveloperhub.org/vocabulary/platform#").
				withPrefix("doap", "http://usefulinc.com/ns/doap#").
				withPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	}

	/**
	 * Retrieve the location of the GitLab Enhancer to use. The location is read
	 * first from the {@value #GITLAB_ENHANCER_SYSTEM_PROPERTY} system property.
	 * If no valid value is found, then the
	 * {@value #GITLAB_ENHANCER_ENVIRONMENT_VARIABLE} is tried.
	 *
	 * @return The location of the GitLab Enhancer or null if no valid location
	 *         has been specified
	 */
	public URI target() {
		if(this.target==null) {
			String gitLabEnhancer = System.getProperty(GITLAB_ENHANCER_SYSTEM_PROPERTY);
			this.target=verifyEnhancerURI(gitLabEnhancer,true,GITLAB_ENHANCER_SYSTEM_PROPERTY);
			if(this.target==null) {
				gitLabEnhancer = System.getenv(GITLAB_ENHANCER_ENVIRONMENT_VARIABLE);
				this.target=verifyEnhancerURI(gitLabEnhancer,false,GITLAB_ENHANCER_ENVIRONMENT_VARIABLE);
			}
		}
		return this.target;
	}

	private URI verifyEnhancerURI(final String uri, final boolean systemProperty, final String identifier) {
		URI result = null;
		if(uri!=null) {
			try {
				final URI tmp=new URI(uri);
				if(!"http".equals(tmp.getScheme())) {
					logFailure(uri,systemProperty,identifier,null,"unsupported protocol ("+tmp.getScheme()+")");
				} else {
					result=tmp;
				}
			} catch (final URISyntaxException e) {
				logFailure(uri,systemProperty,identifier,e,null);
			}
		}
		return result;
	}

	private void logFailure(
			final String uri,
			final boolean systemProperty,
			final String identifier,
			final Throwable failure,
			final String message) {
		LOGGER.warn(
			"Invalid GitLab Enhancer URI '{}' configured via {} '{}'{}",
			uri,
			systemProperty?
				"system property":
				"environment variable",
			identifier,
			message!=null?": "+message:"",
			failure);
	}

}