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
	"lines_removed",
	"short_id",
	"author",
	"lines_added",
	"created_at",
	"title",
	"parent_ids",
	"committed_date",
	"message",
	"authored_date",
	"id"
})
public class Commit {

	@JsonProperty("lines_removed")
	private Integer linesRemoved;
	@JsonProperty("short_id")
	private String shortId;
	@JsonProperty("author")
	private String author;
	@JsonProperty("lines_added")
	private Integer linesAdded;
	@JsonProperty("created_at")
	private Long createdAt;
	@JsonProperty("title")
	private String title;
	@JsonProperty("parent_ids")
	private List<Object> parentIds = new ArrayList<Object>();
	@JsonProperty("committed_date")
	private Long committedDate;
	@JsonProperty("message")
	private String message;
	@JsonProperty("authored_date")
	private Long authoredDate;
	@JsonProperty("id")
	private String id;
	@JsonIgnore
	private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return The linesRemoved
	 */
	@JsonProperty("lines_removed")
	public Integer getLinesRemoved() {
		return this.linesRemoved;
	}

	/**
	 *
	 * @param linesRemoved
	 *            The lines_removed
	 */
	@JsonProperty("lines_removed")
	public void setLinesRemoved(final Integer linesRemoved) {
		this.linesRemoved = linesRemoved;
	}

	/**
	 *
	 * @return The shortId
	 */
	@JsonProperty("short_id")
	public String getShortId() {
		return this.shortId;
	}

	/**
	 *
	 * @param shortId
	 *            The short_id
	 */
	@JsonProperty("short_id")
	public void setShortId(final String shortId) {
		this.shortId = shortId;
	}

	/**
	 *
	 * @return The author
	 */
	@JsonProperty("author")
	public String getAuthor() {
		return this.author;
	}

	/**
	 *
	 * @param author
	 *            The author
	 */
	@JsonProperty("author")
	public void setAuthor(final String author) {
		this.author = author;
	}

	/**
	 *
	 * @return The linesAdded
	 */
	@JsonProperty("lines_added")
	public Integer getLinesAdded() {
		return this.linesAdded;
	}

	/**
	 *
	 * @param linesAdded
	 *            The lines_added
	 */
	@JsonProperty("lines_added")
	public void setLinesAdded(final Integer linesAdded) {
		this.linesAdded = linesAdded;
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
	 * @return The title
	 */
	@JsonProperty("title")
	public String getTitle() {
		return this.title;
	}

	/**
	 *
	 * @param title
	 *            The title
	 */
	@JsonProperty("title")
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 *
	 * @return The parentIds
	 */
	@JsonProperty("parent_ids")
	public List<Object> getParentIds() {
		return this.parentIds;
	}

	/**
	 *
	 * @param parentIds
	 *            The parent_ids
	 */
	@JsonProperty("parent_ids")
	public void setParentIds(final List<Object> parentIds) {
		this.parentIds = parentIds;
	}

	/**
	 *
	 * @return The committedDate
	 */
	@JsonProperty("committed_date")
	public Long getCommittedDate() {
		return this.committedDate;
	}

	/**
	 *
	 * @param committedDate
	 *            The committed_date
	 */
	@JsonProperty("committed_date")
	public void setCommittedDate(final Long committedDate) {
		this.committedDate = committedDate;
	}

	/**
	 *
	 * @return The message
	 */
	@JsonProperty("message")
	public String getMessage() {
		return this.message;
	}

	/**
	 *
	 * @param message
	 *            The message
	 */
	@JsonProperty("message")
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 *
	 * @return The authoredDate
	 */
	@JsonProperty("authored_date")
	public Long getAuthoredDate() {
		return this.authoredDate;
	}

	/**
	 *
	 * @param authoredDate
	 *            The authored_date
	 */
	@JsonProperty("authored_date")
	public void setAuthoredDate(final Long authoredDate) {
		this.authoredDate = authoredDate;
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
