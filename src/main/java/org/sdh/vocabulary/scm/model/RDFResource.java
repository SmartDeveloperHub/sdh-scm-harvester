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
package org.sdh.vocabulary.scm.model;

import java.io.ByteArrayOutputStream;

import com.hp.hpl.jena.ontology.OntModel;

public class RDFResource {
	
	public OntModel instanceModel;
	public OntModel schemaModel;
	
	public RDFResource(OntModel schemaModel, OntModel instanceModel)
	{
		this.instanceModel=instanceModel;
		this.schemaModel=schemaModel;
	}

	public OntModel getInstanceModel() {
		return instanceModel;
	}

	public void setInstanceModel(OntModel instanceModel) {
		this.instanceModel = instanceModel;
	}

	public OntModel getSchemaModel() {
		return schemaModel;
	}

	public void setSchemaModel(OntModel schemaModel) {
		this.schemaModel = schemaModel;
	}
	
    public String getRdfModel(String rdfFormat){
    	OntModel outputModel = instanceModel;
	    ByteArrayOutputStream output= new ByteArrayOutputStream();
	    outputModel.writeAll(output, rdfFormat);
	    return output.toString();
    }
	
	
}
