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
package org.smartdeveloperhub.harvesters.scm.frontend.core.user;

final class UserVocabulary {

	static final String TYPE           = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	static final String IMG            = "http://xmlns.com/foaf/0.1/img";
	static final String NAME           = "http://xmlns.com/foaf/0.1/name";
	static final String NICK           = "http://xmlns.com/foaf/0.1/nick";
	static final String IMAGE          = "http://xmlns.com/foaf/0.1/Image";
	static final String DEPICTS        = "http://xmlns.com/foaf/0.1/depicts";

	static final String PERSON_TYPE    = "http://www.smartdeveloperhub.org/vocabulary/scm#Committer";
	static final String FIRST_COMMIT   = "http://www.smartdeveloperhub.org/vocabulary/scm#firstCommit";
	static final String LAST_COMMIT    = "http://www.smartdeveloperhub.org/vocabulary/scm#lastCommit";
	static final String MBOX           = "http://www.smartdeveloperhub.org/vocabulary/scm#mbox";
	static final String COMMITTER_ID   = "http://www.smartdeveloperhub.org/vocabulary/scm#committerId";
	static final String EXTERNAL       = "http://www.smartdeveloperhub.org/vocabulary/scm#external";

	static final String CORRESPONDS_TO = "http://www.smartdeveloperhub.org/vocabulary/platform#correspondsTo";

	private UserVocabulary() {
	}

}
