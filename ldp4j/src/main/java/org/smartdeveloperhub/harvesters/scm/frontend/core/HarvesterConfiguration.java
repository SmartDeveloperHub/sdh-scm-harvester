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
package org.smartdeveloperhub.harvesters.scm.frontend.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.ldp4j.application.ext.Configuration;
import org.ldp4j.application.ext.Namespaces;
import org.ldp4j.application.util.ImmutableNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HarvesterConfiguration extends Configuration {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(HarvesterConfiguration.class);

	//system property
	private static final String SCM_HARVESTER_TARGET = "scm.harvester.target";
	//property file
	private static final String SCM_HARVESTER_CONFIG_PATH = "Scm.harvester.config";
	
	@Override
	public Namespaces namespaces() {
		return
			new ImmutableNamespaces().
				withPrefix("scm", "http://www.smartdeveloperhub.org/vocabulary/scm#").				                   
				withPrefix("platform", "http://www.smartdeveloperhub.org/vocabulary/platform#").				
				withPrefix("doap", "http://usefulinc.com/ns/doap#").
				withPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	}
	
	// the name of the service you're publishing.
	public URI target() throws IOException {
		LOGGER.info("- Get GitLab enhancer ..");
		//first try to get the enhancer url from the system property;
		String target = System.getProperty(SCM_HARVESTER_TARGET);		
		if(target==null) {
			//if the system property is not set then read the property file
			target=getURIfromPropertyFile();
			if (target==null){
				target="http://192.168.0.10:5000/api/";
				LOGGER.info("- Using default enhancer url ..: {}", target);
			}
			else 
				LOGGER.info("- Enhancer from property file {}..: {}",SCM_HARVESTER_CONFIG_PATH, target);
		}
		else{
			LOGGER.info("- Enhancer from system property {}..: {}",SCM_HARVESTER_TARGET, target);
		}
		return URI.create(target);
	}
	
	private String getURIfromPropertyFile() throws IOException{
		String target=null;
		Properties prop = new Properties();		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SCM_HARVESTER_CONFIG_PATH);

		if (inputStream != null) {
			prop.load(inputStream);
			target = prop.getProperty("gitlab.enhancer");
		}
		return target;
	}
	
}