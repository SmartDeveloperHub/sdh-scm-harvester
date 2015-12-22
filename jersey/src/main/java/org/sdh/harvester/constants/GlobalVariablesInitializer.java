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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.jersey:scm-harvester-jersey:0.2.0
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.constants;

import javax.servlet.ServletContext;

public class GlobalVariablesInitializer {

	public static ServletContext servletContext;
	public GlobalVariablesInitializer(ServletContext servletContext) {
		this.servletContext=servletContext;
	}
	
	public static String getSCMIndividualNs(){
		String scmIndividualNS=servletContext.getInitParameter("scm-individual-namespace");
		return scmIndividualNS;
	}
	
	public static String getScmOntologyPath(){
		String scmOntologyPath=servletContext.getInitParameter("scm.ttl");
		return scmOntologyPath;
	}
	
//	public static String getTargetPlaformUri() {
//		String targetPlatformUri=servletContext.getInitParameter("platform.ttl");
//		return targetPlatformUri;
//	}
//	
//	public static String getTargetDoapURI(){
//		String targetDoapUri=servletContext.getInitParameter("doap.rdf");
//		return targetDoapUri;
//	}
//
//	public static String getTargetDcTerms() {
//		String targetDcTermsUri=servletContext.getInitParameter("dcterms.rdf");
//		return targetDcTermsUri;
//	}
//	
//	public static String getTargetDcType() {
//		String targetDcTypeUri=servletContext.getInitParameter("dctype.rdf");
//		return targetDcTypeUri;
//	}

	public static String getSrcPlaformUri() {
		String srcPlatformUri=servletContext.getInitParameter("platform-reference");
		return srcPlatformUri;
	}

	public static String getSrcDoapURI() {
		String srcDoapUri=servletContext.getInitParameter("doap-reference");
		return srcDoapUri;
	}

	public static String getSrcDcTerms() {
		String srcDcTermsUri=servletContext.getInitParameter("dcterms-reference");
		return srcDcTermsUri;
	}

	public static String getSrcDcType() {
		String srcDcTypeUri=servletContext.getInitParameter("dctype-reference");
		return srcDcTypeUri;
	}
	
	public static String getGitlabEnhancerEndpoint() {
		String gitlabEnhancerEndpoint=servletContext.getInitParameter("gitlab-enhancer-endpoint");
		return gitlabEnhancerEndpoint;
	}

	public static String gestScmOntologyEndPoint() {
		String scmOntologyEndpoint=servletContext.getInitParameter("scm-ontology-endpoint");
		return scmOntologyEndpoint;
	}
}
