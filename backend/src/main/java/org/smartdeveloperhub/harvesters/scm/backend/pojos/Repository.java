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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"first_commit_at",
	"archived",
	"last_activity_at",
	"name",
	"contributors",
	"tags",
	"created_at",
	"default_branch",
	"id",
	"http_url_to_repo",
	"web_url",
	"owner",
	"last_commit_at",
	"public",
	"avatar_url"
})
public class Repository extends Contributable {

	@JsonProperty("first_commit_at")
	private Long firstCommitAt;

	@JsonProperty("archived")
	private String archived;

	@JsonProperty("last_activity_at")
	private Long lastActivityAt;

	@JsonProperty("description")
	private String description;

	@JsonProperty("tags")
	private List<String> tags = new ArrayList<>();

	@JsonProperty("default_branch")
	private String defaultBranch;

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("http_url_to_repo")
	private String httpUrlToRepo;

	@JsonProperty("web_url")
	private String webUrl;

	@JsonProperty("owner")
	private Owner owner;

	@JsonProperty("last_commit_at")
	private Long lastCommitAt;

	@JsonProperty("public")
	private String pub;

	@JsonProperty("avatar_url")
	private String avatarUrl;

	@JsonIgnore
	private Branches branches;

	@JsonIgnore
	private Commits commits;

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
	 * @return The archived
	 */
	@JsonProperty("archived")
	public String getArchived() {
		return this.archived;
	}

	/**
	 *
	 * @param archived
	 *            The archived
	 */
	@JsonProperty("archived")
	public void setArchived(final String archived) {
		this.archived = archived;
	}

	/**
	 *
	 * @return The lastActivityAt
	 */
	@JsonProperty("last_activity_at")
	public Long getLastActivityAt() {
		return this.lastActivityAt;
	}

	/**
	 *
	 * @param lastActivityAt
	 *            The last_activity_at
	 */
	@JsonProperty("last_activity_at")
	public void setLastActivityAt(final Long lastActivityAt) {
		this.lastActivityAt = lastActivityAt;
	}

	/**
	 *
	 * @return The tags
	 */
	@JsonProperty("tags")
	public List<String> getTags() {
		return this.tags;
	}

	/**
	 *
	 * @param tags
	 *            The tags
	 */
	@JsonProperty("tags")
	public void setTags(final List<String> tags) {
		this.tags = tags;
	}

	/**
	 *
	 * @return The defaultBranch
	 */
	@JsonProperty("default_branch")
	public String getDefaultBranch() {
		return this.defaultBranch;
	}

	/**
	 *
	 * @param defaultBranch
	 *            The default_branch
	 */
	@JsonProperty("default_branch")
	public void setDefaultBranch(final String defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

	/**
	 *
	 * @return The id
	 */
	@JsonProperty("id")
	public Integer getId() {
		return this.id;
	}

	/**
	 *
	 * @param id
	 *            The id
	 */
	@JsonProperty("id")
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 *
	 * @return The httpUrlToRepo
	 */
	@JsonProperty("http_url_to_repo")
	public String getHttpUrlToRepo() {
		return this.httpUrlToRepo;
	}

	/**
	 *
	 * @param httpUrlToRepo
	 *            The http_url_to_repo
	 */
	@JsonProperty("http_url_to_repo")
	public void setHttpUrlToRepo(final String httpUrlToRepo) {
		this.httpUrlToRepo = httpUrlToRepo;
	}

	/**
	 *
	 * @return The webUrl
	 */
	@JsonProperty("web_url")
	public String getWebUrl() {
		return this.webUrl;
	}

	/**
	 *
	 * @param webUrl
	 *            The web_url
	 */
	@JsonProperty("web_url")
	public void setWebUrl(final String webUrl) {
		this.webUrl = webUrl;
	}

	/**
	 *
	 * @return The owner
	 */
	@JsonProperty("owner")
	public Owner getOwner() {
		return this.owner;
	}

	/**
	 *
	 * @param owner
	 *            The owner
	 */
	@JsonProperty("owner")
	public void setOwner(final Owner owner) {
		this.owner = owner;
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
	 * @return The _public
	 */
	@JsonProperty("public")
	public String getPublic() {
		return this.pub;
	}

	/**
	 *
	 * @param pub
	 *            The public
	 */
	@JsonProperty("public")
	public void setPublic(final String pub) {
		this.pub = pub;
	}

	@JsonProperty("avatar_url")
	public String getAvatarUrl() {
		return this.avatarUrl;
	}

	@JsonProperty("avatar_url")
	public void setAvatarUrl(final String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	@JsonAnySetter
	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public Branches getBranches() {
		return this.branches;
	}

	public void setBranches(final Branches branches) {
		this.branches = branches;
	}

	public Commits getCommits() {
		return this.commits;
	}

	public void setCommits(final Commits commits) {
		this.commits = commits;
	}

}
