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
package org.smartdeveloperhub.harvesters.scm.testing.enhancer;

import java.util.List;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;

import com.google.common.collect.Lists;

final class ImmutableCommitterState implements CommitterState {

	private Long lastCommitAt;

	private final Long firstCommitAt;
	private final Long createdAt;
	private final String id;
	private final String avatarUrl;
	private final List<String> emails;
	private final boolean external;
	private final String name;
	private final String username;

	ImmutableCommitterState(final String committerId) {
		this.id = committerId;
		this.createdAt=System.currentTimeMillis();
		this.firstCommitAt=this.createdAt;
		this.lastCommitAt=this.createdAt;
		this.name=StateUtil.generateUserName();;
		this.username=toUserName(this.name);
		this.avatarUrl=StateUtil.generateAvatarUrl("committers", committerId);
		this.emails=Lists.newArrayList(this.username+"@example.org");
		this.external=committerId.length()%2==0;
		Console.currentConsole().log("Created committer %s (%s)",this.id,this.name);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public User toEntity() {
		final User user = new User();
		user.setAvatarUrl(this.avatarUrl);
		user.setCreatedAt(this.createdAt);
		user.setEmails(this.emails);
		user.setExternal(this.external);
		user.setFirstCommitAt(this.firstCommitAt);
		user.setId(this.id);
		user.setLastCommitAt(this.lastCommitAt);
		user.setName(this.name);
		user.setState("active");
		user.setUsername(this.username);
		return user;
	}

	@Override
	public void logActivity(final long timestamp) {
		this.lastCommitAt=timestamp;
	}

	@Override
	public String getName() {
		return this.name;
	}

	private static String toUserName(final String name) {
		final String[] parts=name.toLowerCase().split(" ");
		return parts[0].substring(0,1)+parts[1]+parts[2];
	}

}