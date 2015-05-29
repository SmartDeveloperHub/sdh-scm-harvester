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

package org.sdh.vocabulary.scm;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.sdh.harvester.scm.Harvester;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class ScmOntology {
	String inputFileName; 
	OntModel ontModel;
	
	static final Logger logger = LogManager.getLogger(ScmOntology.class);
	
	public ScmOntology(){
		//inputFileName = "ontology/sdh-scm-ontology.owl";
		inputFileName = "/home/hagarcia/workspace/scm-ontology(NoCode)/scm.ttl";
	}

	public void loadOntology(){
		
		ClassLoader classLoader = getClass().getClassLoader();
		
//		InputStream in = FileManager.get().open(classLoader.getResource(inputFileName).getFile());
		InputStream in = FileManager.get().open(inputFileName);
		
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + inputFileName + " not found");
		}
		
		
//      Finally this option worked 	
		OntDocumentManager docMgr = new OntDocumentManager();
		docMgr.setMetadataSearchPath(AlternativeURI.docMgrConfigurationFile, false);
		docMgr.setProcessImports(true);	
		OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);		
		modelSpec.setDocumentManager(docMgr);
		ontModel= ModelFactory.createOntologyModel(modelSpec);
		
		//this option worked!
//		ontModel= ModelFactory.createOntologyModel();
//		OntDocumentManager docMgr = ontModel.getDocumentManager();
//		docMgr.addAltEntry("http://www.smartdeveloperhub.org/vocabulary/external/doap/doap.rdf", 
//				"file:/home/hagarcia/git-repos/sdh-vocabulary/src/main/resources/vocabulary/external/doap/doap.rdf");
//		
//		docMgr.addAltEntry("http://www.smartdeveloperhub.org/vocabulary/external/dcmi/dcterms.rdf", 
//				"file:/home/hagarcia/git-repos/sdh-vocabulary/src/main/resources/vocabulary/external/dcmi/dcterms.rdf");
//		
//		docMgr.addAltEntry("http://www.smartdeveloperhub.org/vocabulary/external/dcmi/dctype.rdf", 
//				"file:/home/hagarcia/git-repos/sdh-vocabulary/src/main/resources/vocabulary/external/dcmi/dctype.rdf");
//		
//		docMgr.addAltEntry("http://www.smartdeveloperhub.org/vocabulary/platform", 
//				"file:/home/hagarcia/workspace/scm-ontology(NoCode)/platform.ttl");
		
					
		
		// read the RDF/XML file
		ontModel.read(in, null, "TTL" );
		// write it to standard out
//		ontModel.writeAll(System.out, "TTL");
		
		
//		OutputStream loggerOS = IoBuilder.forLogger(logger).
//				                          setLevel(Level.DEBUG).
//				                          buildOutputStream();
//						
		OntModel doap = ontModel.getImportedModel("http://www.smartdeveloperhub.org/vocabulary/external/doap/doap.rdf");		//
//		doap.write(loggerOS,"TTL");
		doap.write(System.out,"TTL");				
//		System.out.println("*****"+ontModel.hasLoadedImport("http://www.smartdeveloperhub.org/vocabulary/external/doap/doap.rdf"));
//       
//		OntModel platform = ontModel.getImportedModel("http://www.smartdeveloperhub.org/vocabulary/platform");		//	    
////		platform.write(System.out,"TTL");
//		platform.write(loggerOS,"TTL");
//		System.out.println("*****"+ontModel.hasLoadedImport("http://www.smartdeveloperhub.org/vocabulary/platform"));			
	}
	
	public OntModel getJenaModel(){
		return ontModel;
	}
}
