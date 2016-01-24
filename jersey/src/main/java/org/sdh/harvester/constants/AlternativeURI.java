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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.jersey:scm-harvester-jersey:0.3.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.constants;

public final class AlternativeURI {
	
	public static final String docMgrConfigurationFile="file:/home/hagarcia/workspace/scm-ontology(NoCode)/ont-policy.rdf";
	
	public static final String scmOntologyEndpoint;
	
//	public static final String srcDoapURI="http://www.smartdeveloperhub.org/vocabulary/external/doap/doap.rdf";
//	public static final String srcPlatformURI="http://www.smartdeveloperhub.org/vocabulary/platform";
//	public static final String srcDctermsURI="http://www.smartdeveloperhub.org/vocabulary/external/dcmi/dcterms.rdf";	
//	public static final String srcDctypeURI="http://www.smartdeveloperhub.org/vocabulary/external/dcmi/dctype.rdf";

	public static final String srcDoapURI;
	public static final String srcPlatformURI;
	public static final String srcDctermsURI;
	public static final String srcDctypeURI;
	
	public static final String scmOntologyFile;
//	public static final String trgPlatformURI;;
//	public static final String trgDoapURI;
//	public static final String trgDctermsURI;
//	public static final String trgDctypeURI;


	
	static{
		scmOntologyFile = GlobalVariablesInitializer.getScmOntologyPath();
		
		scmOntologyEndpoint = GlobalVariablesInitializer.gestScmOntologyEndPoint();
		
		srcPlatformURI = GlobalVariablesInitializer.getSrcPlaformUri();
//		trgPlatformURI = GlobalVariablesInitializer.getTargetPlaformUri();
		
		srcDoapURI = GlobalVariablesInitializer.getSrcDoapURI();
//		trgDoapURI = GlobalVariablesInitializer.getTargetDoapURI();
		
		srcDctermsURI = GlobalVariablesInitializer.getSrcDcTerms();
//		trgDctermsURI = GlobalVariablesInitializer.getTargetDcTerms();
		
		srcDctypeURI = GlobalVariablesInitializer.getSrcDcType();
//		trgDctypeURI = GlobalVariablesInitializer.getTargetDcType();
	}
	

	
	
	
	
}
