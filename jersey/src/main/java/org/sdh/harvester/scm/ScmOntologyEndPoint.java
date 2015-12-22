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
package org.sdh.harvester.scm;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.sdh.harvester.scm.handler.ScmOntologyHandler;
import org.sdh.vocabulary.scm.ScmOntology;

import com.hp.hpl.jena.ontology.OntModel;

@Path("/ontology")
public class ScmOntologyEndPoint extends EndPoint {
	
	
	public ScmOntologyEndPoint(@Context ServletContext servletContext) {
		super(servletContext);
		//  Auto-generated constructor stub
	}
	
	@GET
    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
    public String getOntologyTTL(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getAllModels(turtleJena);
	}
	
	@GET
    @Produces({rdfXmlMediaType})
    public String getOntologyRDF(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getAllModels(rdfXmlJena);
	}
	
	@GET @Path("/scm")
    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
    public String getScmOntologyTTL(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getScmModel(turtleJena);	
	}
	
	@GET @Path("/scm")
    @Produces({rdfXmlMediaType})
    public String getScmOntologyRDF(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getScmModel(rdfXmlJena);	
	}
	
	@GET @Path("/platform")
    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
    public String getPlatformOntologyTTL(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getPlatformModel(turtleJena);	
	}
	
	@GET @Path("/platform")
    @Produces({rdfXmlMediaType})
    public String getPlatformOntologyRDF(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getPlatformModel(rdfXmlJena);	
	}

	@GET @Path("/doap")
    @Produces({MediaType.TEXT_PLAIN, turtleMediaType})
    public String getDoapOntologyTTL(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getDoapModel(turtleJena);	
	}

	@GET @Path("/doap")
    @Produces({rdfXmlMediaType})
    public String getDoapOntology(){
		ScmOntologyHandler onto = new ScmOntologyHandler();
		return onto.getDoapModel(rdfXmlJena);	
	}

}
