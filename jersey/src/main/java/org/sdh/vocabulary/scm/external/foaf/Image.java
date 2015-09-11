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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.jersey:scm-harvester-jersey:0.2.0-SNAPSHOT
 *   Bundle      : scmharvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.sdh.vocabulary.scm.external.foaf;

import org.sdh.harvester.constants.Namespace;
import org.sdh.vocabulary.scm.model.RDFResource;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Image extends RDFResource{
	
	public Image(OntModel schemaModel, OntModel instanceModel) {
		super(schemaModel, instanceModel);
		// TODO Auto-generated constructor stub
	}

    String userId;    
    String repoId;
	Resource foafDepicts;
	
	
	
	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public Resource getFoafDepicts() {
		return foafDepicts;
	}


	public void setFoafDepicts(Resource foafDepicts) {
		this.foafDepicts = foafDepicts;
	}
	
	public void setFoafDepicts(String foafDepicts){
		this.foafDepicts = schemaModel.createResource(foafDepicts);
	}

	

	public String getRepoId() {
		return repoId;
	}


	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}


	public Resource getIndividual(){
		OntClass imageClass = schemaModel.getOntClass(Namespace.foafNS+"Image" );
		Individual indv = null;
		if (userId !=null){
			if (!userId.isEmpty())			
				indv = instanceModel.createIndividual(Namespace.scmIndividualNS+"users/"+userId+"/image", imageClass);
		}
		else if (repoId !=null)
			if (!repoId.isEmpty())
				indv = instanceModel.createIndividual(Namespace.scmIndividualNS+repoId+"/image", imageClass);
		
		if (indv!=null)
			if (foafDepicts!=null){	    		
				Property isDefinedByProperty = schemaModel.getProperty( Namespace.foafNS + "depicts" ); 
	    		indv.addProperty(isDefinedByProperty, foafDepicts);
	    	}
		
		return indv;
		
	}
}
