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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.3.0
 *   Bundle      : scm-harvester-backend-0.3.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.pojos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Contributable<T> extends Identifiable<T> {

	@JsonProperty("name")
	private String name;

	@JsonProperty("contributors")
	private List<String> contributors = new ArrayList<>();

	@JsonProperty("created_at")
	private Long createdAt;

	/**
	 *
	 * @return The name
	 */
	@JsonProperty("name")
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * @param name
	 *            The name
	 */
	@JsonProperty("name")
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 *
	 * @return The contributors
	 */
	@JsonProperty("contributors")
	public List<String> getContributors() {
		return this.contributors;
	}

	/**
	 *
	 * @param contributors
	 *            The contributors
	 */
	@JsonProperty("contributors")
	public void setContributors(final List<String> contributors) {
		this.contributors = contributors;
	}

	/**
	 *
	 * @return The createdAt
	 */
	@JsonProperty("created_at")
	public Long getCreatedAt() {
		return this.createdAt;
	}

	/**
	 *
	 * @param createdAt
	 *            The created_at
	 */
	@JsonProperty("created_at")
	public void setCreatedAt(final Long createdAt) {
		this.createdAt = createdAt;
	}

}
