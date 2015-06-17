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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sdh.harvester.constants.AlternativeURI;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ScmOntology {

	String inputFileName; 
	OntModel ontModel;
	
	static final Logger logger = LogManager.getLogger(ScmOntology.class);
	
	public ScmOntology(){
		//inputFileName = "ontology/sdh-scm-ontology.owl";
		//inputFileName = "/home/hagarcia/workspace/scm-ontology(NoCode)/scm.ttl";
		
	}

	public void loadOntology(){
		
		inputFileName=AlternativeURI.scmOntologyFile;		
//		ClassLoader classLoader = getClass().getClassLoader();
		
		inputFileName = "vocabulary/v1/scm.ttl";
		InputStream in=
			Thread.
				currentThread().
					getContextClassLoader().
						getResourceAsStream(inputFileName);		

//		InputStream in = FileManager.get().open(inputFileName);
		
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + inputFileName + " not found");
		}
		
		
//      Finally this option worked 	
//		OntDocumentManager docMgr = new OntDocumentManager();
//		docMgr.setMetadataSearchPath(AlternativeURI.docMgrConfigurationFile, false);
//		docMgr.setProcessImports(true);	
//		OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);		
//		modelSpec.setDocumentManager(docMgr);
//		ontModel= ModelFactory.createOntologyModel(modelSpec);
		
		//this option worked!
		ontModel= ModelFactory.createOntologyModel();
		OntDocumentManager docMgr = ontModel.getDocumentManager();
		docMgr.getFileManager().addLocatorClassLoader(Thread.currentThread().getContextClassLoader());
		docMgr.addAltEntry(AlternativeURI.srcDoapURI,"vocabulary/external/doap/doap.rdf");		
		docMgr.addAltEntry(AlternativeURI.srcDctermsURI,"vocabulary/external/dcmi/dcterms.rdf");
		docMgr.addAltEntry(AlternativeURI.srcDctypeURI, "vocabulary/external/dcmi/dctype.rdf");
		docMgr.addAltEntry(AlternativeURI.srcPlatformURI, "vocabulary/v1/platform.ttl");
		
//		docMgr.addAltEntry(AlternativeURI.srcDctermsURI, AlternativeURI.trgDctermsURI);		
//		docMgr.addAltEntry(AlternativeURI.srcDctypeURI, AlternativeURI.trgDctypeURI);		
//		docMgr.addAltEntry(AlternativeURI.srcPlatformURI,  AlternativeURI.trgPlatformURI);							

		// read the RDF/XML file
		ontModel.read(in, null, "TTL" );
		// write it to standard out
//		ontModel.writeAll(System.out, "TTL");
		
		
//		OutputStream loggerOS = IoBuilder.forLogger(logger).
//				                          setLevel(Level.DEBUG).
//				                          buildOutputStream();
//						
//		OntModel doap = ontModel.getImportedModel("http://www.smartdeveloperhub.org/vocabulary/external/doap/doap.rdf");		//
//		doap.write(loggerOS,"TTL");
//		doap.write(System.out,"TTL");				
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
