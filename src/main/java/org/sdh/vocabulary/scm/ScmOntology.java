
package org.sdh.vocabulary.scm;

import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class ScmOntology {
	String inputFileName; 
	Model model;
	
	public ScmOntology(){
		inputFileName = "ontology/sdh-scm-ontology.owl";
	}

	public void loadOntology(){
		
		ClassLoader classLoader = getClass().getClassLoader();
		
		InputStream in = FileManager.get().open(classLoader.getResource(inputFileName).getFile());
		
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + inputFileName + " not found");
		}
		
		model = ModelFactory.createDefaultModel();
		// read the RDF/XML file
		model.read(in, null, "TTL" );

		// write it to standard out
//		model.write(System.out);
	}
	
	public Model getJenaModel(){
		return model;
	}
}
