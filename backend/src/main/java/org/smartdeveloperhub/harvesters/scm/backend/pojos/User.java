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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"username",
	"first_commit_at",
	"name",
	"created_at",
	"emails",
	"state",
	"avatar_url",
	"last_commit_at",
	"id",
	"external"
})
public class User {

	@JsonProperty("username")
	private String username;
	@JsonProperty("first_commit_at")
	private Long firstCommitAt;
	@JsonProperty("name")
	private String name;
	@JsonProperty("created_at")
	private Long createdAt;
	@JsonProperty("emails")
	private List<String> emails = new ArrayList<String>();
	@JsonProperty("state")
	private String state;
	@JsonProperty("avatar_url")
	private String avatarUrl;
	@JsonProperty("last_commit_at")
	private Long lastCommitAt;
	@JsonProperty("id")
	private String id;
	@JsonProperty("external")
	private boolean external;
	@JsonIgnore
	private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return The username
	 */
	@JsonProperty("username")
	public String getUsername() {
		return this.username;
	}

	/**
	 *
	 * @param username
	 *            The username
	 */
	@JsonProperty("username")
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 *
	 * @return The firstCommitAt
	 */
	@JsonProperty("first_commit_at")
	public Long getFirstCommitAt() {
		return this.firstCommitAt;
	}

	/**
	 *
	 * @param firstCommitAt
	 *            The first_commit_at
	 */
	@JsonProperty("first_commit_at")
	public void setFirstCommitAt(final Long firstCommitAt) {
		this.firstCommitAt = firstCommitAt;
	}

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

	/**
	 *
	 * @return The state
	 */
	@JsonProperty("state")
	public String getState() {
		return this.state;
	}

	/**
	 *
	 * @param state
	 *            The state
	 */
	@JsonProperty("state")
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 *
	 * @return The avatarUrl
	 */
	@JsonProperty("avatar_url")
	public String getAvatarUrl() {
		return this.avatarUrl;
	}

	/**
	 *
	 * @param avatarUrl
	 *            The avatar_url
	 */
	@JsonProperty("avatar_url")
	public void setAvatarUrl(final String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/**
	 *
	 * @return The lastCommitAt
	 */
	@JsonProperty("last_commit_at")
	public Long getLastCommitAt() {
		return this.lastCommitAt;
	}

	/**
	 *
	 * @param lastCommitAt
	 *            The last_commit_at
	 */
	@JsonProperty("last_commit_at")
	public void setLastCommitAt(final Long lastCommitAt) {
		this.lastCommitAt = lastCommitAt;
	}

	/**
	 *
	 * @return The id
	 */
	@JsonProperty("id")
	public String getId() {
		return this.id;
	}

	/**
	 *
	 * @param id
	 *            The id
	 */
	@JsonProperty("id")
	public void setId(final String id) {
		this.id = id;
	}

	public boolean isExternal() {
		return this.external;
	}

	public void setExternal(final boolean external) {
		this.external = external;
	}

	public List<String> getEmails() {
		return this.emails;
	}

	public void setEmails(final List<String> emails) {
		this.emails = emails;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(final String name, final Object value) {
		this.additionalProperties.put(name, value);
	}

}
