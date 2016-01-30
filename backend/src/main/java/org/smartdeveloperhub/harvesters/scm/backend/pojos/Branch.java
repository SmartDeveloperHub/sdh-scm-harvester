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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.pojos;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"created_at",
	"protected",
	"name",
	"contributors",
	"last_commit"
})
public class Branch extends Contributable {

	@JsonProperty("protected")
	private String prot;

	@JsonProperty("last_commit")
	private String lastCommit;

	private Commits commits;

	public Commits getCommits() {
		return this.commits;
	}

	public void setCommits(final Commits commits) {
		this.commits = commits;
	}

	/**
	 *
	 * @return The _protected
	 */
	@JsonProperty("protected")
	public String getProtected() {
		return this.prot;
	}

	/**
	 *
	 * @param prot
	 *            The protected
	 */
	@JsonProperty("protected")
	public void setProtected(final String prot) {
		this.prot = prot;
	}

	/**
	 *
	 * @return The lastCommit
	 */
	@JsonProperty("last_commit")
	public String getLastCommit() {
		return this.lastCommit;
	}

	/**
	 *
	 * @param lastCommit
	 *            The last_commit
	 */
	@JsonProperty("last_commit")
	public void setLastCommit(final String lastCommit) {
		this.lastCommit = lastCommit;
	}

}
