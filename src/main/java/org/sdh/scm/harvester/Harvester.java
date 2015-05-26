package org.sdh.scm.harvester;

import java.util.ArrayList;

import org.sdh.vocabulary.scm.ScmOntology;
import org.sdh.vocabulary.scm.external.foaf.Image;
import org.sdh.vocabulary.scm.external.foaf.Person;
import org.sdh.vocabulary.scm.model.Branch;
import org.sdh.vocabulary.scm.model.Commit;
import org.sdh.vocabulary.scm.model.Repository;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class Harvester {
	
	private void testRepository(Model ontoModel, String NS){
		Repository repo = new Repository();
		
		Resource location = ontoModel.createResource(NS+"location");
		repo.setLocation(location);
		
		Resource codebase = ontoModel.createResource(NS+"codebase");
		repo.setCodebase(codebase);
		
		Literal createdOn = ontoModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setCreatedOn(createdOn);
		
		Literal firstCommit = ontoModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setFirstCommit(firstCommit);
		
		Literal lastBuildDate = ontoModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setLastBuildDate(lastBuildDate);
		
		Literal lastCommit = ontoModel.createLiteral("2015-05-20T21:00:00Z",true);
		repo.setLastCommit(lastCommit);
		
		Literal isArchived = ontoModel.createLiteral("false",true);
		repo.setIsArchived(isArchived);
		
		Literal isPublic = ontoModel.createLiteral("boolean",true);
		repo.setIsPublic(isPublic);
		
		Literal defaultBranchName = ontoModel.createLiteral("Master",true);
		repo.setDefaultBranchName(defaultBranchName);
		
		Literal description = ontoModel.createLiteral("This is very nice repo",true);
		repo.setDescription(description);
		
		Literal lastBuildStatus=  ontoModel.createLiteral("Sucesful",true);
		repo.setLastBuildStatus(lastBuildStatus);
		
		Literal name  = ontoModel.createLiteral("Main product repo",true);
		repo.setName(name);
		
	    Literal repositoryId  = ontoModel.createLiteral("repo001",true);
	    repo.setRepositoryId(repositoryId);
	    
	    Literal tags = ontoModel.createLiteral("repo development business",true);;
	    repo.setTags(tags);
	    
		Branch defaultBranch;
		ArrayList<Branch> hasBranch;
		ArrayList<Commit> hasCommit;
		
		Image depiction; 
	    
	    Person developer;
	    Person documenter;
	    Person mainteiner;
	    Person tester;
		
	}

	public static void main (String[] argv){
		ScmOntology onto = new ScmOntology();
		onto.loadOntology();
		Model ontoModel=onto.getJenaModel();
		Harvester harvester = new Harvester();
		String NS = "http://www.smartdeveloperhub.org/vocabulary/sdh/v1/scm#";
		harvester.testRepository(ontoModel,NS);
	}
}
