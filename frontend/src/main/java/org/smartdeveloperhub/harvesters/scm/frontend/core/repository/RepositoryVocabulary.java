/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015-2016 Center for Open Middleware.
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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.repository;

abstract class RepositoryVocabulary {

	static final String TYPE          = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	static final String DEVELOPER     = "http://usefulinc.com/ns/doap#developer";
	static final String NAME          = "http://usefulinc.com/ns/doap#name";
	static final String DESCRIPTION   = "http://usefulinc.com/ns/doap#description";

	static final String DEPICTION     = "http://xmlns.com/foaf/0.1/depiction";
	static final String IMAGE         = "http://xmlns.com/foaf/0.1/Image";
	static final String DEPICTS       = "http://xmlns.com/foaf/0.1/depicts";

	static final String SCMREPOSITORY = "http://www.smartdeveloperhub.org/vocabulary/scm#Repository";
	static final String LOCATION      = "http://www.smartdeveloperhub.org/vocabulary/scm#location";
	static final String CREATEDON     = "http://www.smartdeveloperhub.org/vocabulary/scm#createdOn";
	static final String DEFAULTBRANCH = "http://www.smartdeveloperhub.org/vocabulary/scm#defaultBranch";
	static final String FIRSTCOMMIT   = "http://www.smartdeveloperhub.org/vocabulary/scm#firstCommit";
	static final String LASTCOMMIT    = "http://www.smartdeveloperhub.org/vocabulary/scm#lastCommit";
	static final String HASBRANCH     = "http://www.smartdeveloperhub.org/vocabulary/scm#hasBranch";
	static final String ARCHIVED      = "http://www.smartdeveloperhub.org/vocabulary/scm#isArchived";
	static final String PUBLIC        = "http://www.smartdeveloperhub.org/vocabulary/scm#isPublic";
	static final String OWNER         = "http://www.smartdeveloperhub.org/vocabulary/scm#owner";
	static final String REPOSITORYID  = "http://www.smartdeveloperhub.org/vocabulary/scm#repositoryId";
	static final String TAGS          = "http://www.smartdeveloperhub.org/vocabulary/scm#tags";
	static final String REPOSITORYOF  = "http://www.smartdeveloperhub.org/vocabulary/scm#isRepositoryOf";

	private RepositoryVocabulary() {
	}

}
