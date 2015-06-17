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
 *   Artifact    : org.smartdeveloperhub.harvester:sdh-scm-harvester:1.0.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.harvester.scm.handler;

import java.io.ByteArrayOutputStream;

import org.sdh.harvester.constants.AlternativeURI;
import org.sdh.vocabulary.scm.ScmOntology;

import com.hp.hpl.jena.ontology.OntModel;

public class ScmOntologyHandler {
	ByteArrayOutputStream output;

	public OntModel getSchemaModel(){
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		OntModel schemaModel = onto.getJenaModel();
		return schemaModel;
	}
	
	public String getAllModels(String format){
		OntModel schemaModel=getSchemaModel();
		output= new ByteArrayOutputStream();
		schemaModel.writeAll(output, format);
		return output.toString();
	    //schemaModel.write(output, "TTL");
	}
	
	public String getScmModel(String format){
		OntModel schemaModel=getSchemaModel();
		output= new ByteArrayOutputStream();
		schemaModel.write(output, format);
		return output.toString();
	    //schemaModel.write(output, "TTL");
	}
	
	public String getPlatformModel(String format){
		OntModel schemaModel=getSchemaModel();
		System.out.println("platform reference:"+AlternativeURI.srcPlatformURI);
//		System.out.println("platform.ttl:"+AlternativeURI.trgPlatformURI);
		OntModel platform = schemaModel.getImportedModel(AlternativeURI.srcPlatformURI);
		output= new ByteArrayOutputStream();
		platform.write(output, format);
		return output.toString();
	}
	
	public String getDoapModel(String format){
		OntModel schemaModel=getSchemaModel();
		OntModel doap = schemaModel.getImportedModel(AlternativeURI.srcDoapURI);
		output= new ByteArrayOutputStream();
		doap.write(output, format);
		return output.toString();
	}
}
