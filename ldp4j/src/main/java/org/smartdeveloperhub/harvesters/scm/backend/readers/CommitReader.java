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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.2.0
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import java.io.IOException;
import java.util.List;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branches;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class CommitReader {
	
	ObjectMapper mapper = new ObjectMapper();
	Commits commits;
	Commit commit;

	public Commits readCommits(String commitsIS) throws JsonParseException, JsonMappingException, IOException{
		//commits = mapper.readValue(commitsIS, Commits.class);
		List<String> list = mapper.readValue(commitsIS, 
				  TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));	
		commits = new Commits();
		commits.setCommitIds(list);		
		return commits;
	}
	
	public Commit readCommit(String commitIS) throws JsonParseException, JsonMappingException, IOException{
		commit = mapper.readValue(commitIS, Commit.class);		
		return commit;
	}
}
