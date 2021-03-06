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
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Extensible;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event extends Extensible {

	static final String INSTANCE  = "instance";
	static final String TIMESTAMP = "timestamp";

	@JsonProperty(INSTANCE)
	private String instance;

	@JsonProperty(TIMESTAMP)
	private Long timestamp;

	/**
	 * Get the identifier of the instance that produced the event
	 *
	 * @return The identifier of the instance
	 */
	@JsonProperty(INSTANCE)
	public String getInstance() {
		return this.instance;
	}

	/**
	 * Set the identifier of the instance that produced the event
	 *
	 * @param id
	 *            The identifier of the instance
	 */
	@JsonProperty(INSTANCE)
	public void setInstance(final String id) {
		this.instance = id;
	}

	/**
	 * Get the Unix/POSIX time of the event
	 *
	 * @return The timestamp
	 */
	@JsonProperty(TIMESTAMP)
	public Long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Set the Unix/POSIX time of the event
	 *
	 * @param timestamp
	 *            The timestamp
	 */
	@JsonProperty(TIMESTAMP)
	public void setTimestamp(final Long timestamp) {
		this.timestamp = timestamp;
	}

}