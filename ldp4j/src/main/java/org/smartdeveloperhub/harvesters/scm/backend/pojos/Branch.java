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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.2.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
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
    "created_at",
    "protected",
    "name",
    "contributors",
    "last_commit"
})
public class Branch {

    @JsonProperty("created_at")
    private Long createdAt;
    @JsonProperty("protected")
    private String _protected;
    @JsonProperty("name")
    private String name;
    @JsonProperty("contributors")
    private List<String> contributors = new ArrayList<String>();
    @JsonProperty("last_commit")
    private String lastCommit;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private Commits commits;
    /**
     * 
     * @return
     *     The createdAt
     */
    
    @JsonProperty("created_at")
    public Long getCreatedAt() {
        return createdAt;
    }

    public Commits getCommits() {
		return commits;
	}

	public void setCommits(Commits commits) {
		this.commits = commits;
	}

	/**
     * 
     * @param createdAt
     *     The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The _protected
     */
    @JsonProperty("protected")
    public String getProtected() {
        return _protected;
    }

    /**
     * 
     * @param _protected
     *     The protected
     */
    @JsonProperty("protected")
    public void setProtected(String _protected) {
        this._protected = _protected;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The contributors
     */
    @JsonProperty("contributors")
    public List<String> getContributors() {
        return contributors;
    }

    /**
     * 
     * @param contributors
     *     The contributors
     */
    @JsonProperty("contributors")
    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }

    /**
     * 
     * @return
     *     The lastCommit
     */
    @JsonProperty("last_commit")
    public String getLastCommit() {
        return lastCommit;
    }

    /**
     * 
     * @param lastCommit
     *     The last_commit
     */
    @JsonProperty("last_commit")
    public void setLastCommit(String lastCommit) {
        this.lastCommit = lastCommit;
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
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
