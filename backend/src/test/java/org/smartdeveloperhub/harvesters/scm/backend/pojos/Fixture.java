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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-backend:0.4.0-SNAPSHOT
 *   Bundle      : scm-harvester-backend-0.4.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.backend.pojos;

import java.util.Arrays;

public class Fixture {

	public static Branch defaultBranch() {
		final Branch commit = new Branch();
		commit.setAdditionalProperty("additionalProperty", "value");
		commit.setContributors(Arrays.asList("contributor1","constributor2"));
		commit.setCreatedAt(System.nanoTime());
		commit.setLastCommit(System.nanoTime());
		commit.setName("name");
		commit.setProtected("protected");
		return commit;
	}

	public static Commit defaultCommit() {
		final Commit commit = new Commit();
		commit.setAdditionalProperty("additionalProperty", "value");
		commit.setAuthor("archived");
		commit.setAuthoredDate(System.nanoTime());
		commit.setCommittedDate(System.nanoTime());
		commit.setCreatedAt(System.nanoTime());
		commit.setId("id");
		commit.setLinesAdded(3);
		commit.setLinesRemoved(4);
		commit.setMessage("message");
		commit.setParentIds(Arrays.asList((Object)1,(Object)2,(Object)3));
		commit.setShortId("shortId");
		commit.setTitle("title");
		return commit;
	}

	public static Commits defaultCommits() {
		final Commits commits=new Commits();
		commits.setCommitIds(Arrays.asList("commit1","commit2"));
		return commits;
	}

	public static Branches defaultBranches() {
		final Branches branches=new Branches();
		branches.setBranchIds(Arrays.asList("branch1","branch2"));
		return branches;
	}

	public static Owner defaultOwner() {
		final Owner owner=new Owner();
		owner.setId("id");
		owner.setType("type");
		owner.setAdditionalProperty("ownerAdditionalProperty", "value");
		return owner;
	}

	public static Repository defaultRepository() {
		final Repository repository = new Repository();
		repository.setState("state");
		repository.setAvatarUrl("avatarUrl");
		repository.setContributors(Arrays.asList("contributor1","constributor2"));
		repository.setCreatedAt(System.nanoTime());
		repository.setDefaultBranch("defaultBranch");
		repository.setDescription("description");
		repository.setFirstCommitAt(System.nanoTime());
		repository.setHttpUrlToRepo("httpUrlTpRepo");
		repository.setId(Integer.toString(1));
		repository.setLastActivityAt(System.nanoTime());
		repository.setLastCommitAt(System.nanoTime());
		repository.setName("name");
		repository.setOwner(defaultOwner());
		repository.setPublic("public");
		repository.setTags(Arrays.asList("tag1","tag2"));
		repository.setWebUrl("webUrl");
		repository.setAdditionalProperty("additionalProperty", "value");
		return repository;
	}

	public static User defaultUser() {
		final User commit = new User();
		commit.setAdditionalProperty("additionalProperty", "value");
		commit.setAvatarUrl("avatarUrl");
		commit.setCreatedAt(System.nanoTime());
		commit.setEmails(Arrays.asList("email1","email2"));
		commit.setExternal(true);
		commit.setFirstCommitAt(System.nanoTime());
		commit.setId("id");
		commit.setLastCommitAt(System.nanoTime());
		commit.setName("name");
		commit.setState("state");
		commit.setUsername("username");
		return commit;
	}

	public static Repositories defaultRepositories() {
		final Repositories defaultRepos=new Repositories();
		defaultRepos.setRepositoryIds(Arrays.asList("1","2","3"));
		return defaultRepos;
	}

}
