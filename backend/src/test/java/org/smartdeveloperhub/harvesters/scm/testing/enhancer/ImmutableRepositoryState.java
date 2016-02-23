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
import java.util.Map;
import java.util.Set;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Owner;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

final class ImmutableRepositoryState implements RepositoryState {

	private final Map<String,ImmutableCommitState> commits;
	private final Map<String,ImmutableBranchState> branches;

	private final Set<String> contributors;
	private final String avatarUrl;
	private final String name;
	private final Long createdAt;
	private final Integer id;
	private final String httpUrlToRepo;
	private final String owner;
	private final List<String> tags;
	private final String webUrl;

	private int major=0;
	private int minor=0;
	private int revision=0;
	private int feature=0;

	private Long firstCommitAt;
	private Long lastActivityAt;

	ImmutableRepositoryState(final Integer id, final CommitterState owner) {
		this.id=id;
		this.name=StateUtil.generateRepoName();
		this.owner=owner.getId();
		this.avatarUrl=StateUtil.generateAvatarUrl("repositories",id);
		this.createdAt=System.currentTimeMillis();
		this.webUrl=StateUtil.generateWebUrl(this.name);
		this.httpUrlToRepo=StateUtil.generateGitUrl(this.name);
		this.commits=Maps.newLinkedHashMap();
		this.branches=Maps.newLinkedHashMap();
		this.contributors=Sets.newLinkedHashSet();
		this.tags=Lists.newArrayList();
		Console.currentConsole().log("Created repository %s (%s) with owner %s (%s)",this.id,this.name,this.owner,owner.getName());
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public List<String> commits() {
		return ImmutableList.copyOf(this.commits.keySet());
	}

	@Override
	public List<String> branches() {
		return ImmutableList.copyOf(this.branches.keySet());
	}

	@Override
	public CommitState commit(final String commitId) {
		CommitState result = this.commits.get(commitId);
		if(result==null) {
			result=new NullCommitState(this.id,commitId);
		}
		return result;
	}

	@Override
	public BranchState branch(final String branchId) {
		BranchState result = this.branches.get(branchId);
		if(result==null) {
			result=new NullBranchState(this.id,branchId);
		}
		return result;
	}

	@Override
	public Repository toEntity() {
		final Repository repository = new Repository();
		repository.setAvatarUrl(this.avatarUrl);
		repository.setContributors(Lists.newArrayList(this.contributors));
		repository.setCreatedAt(this.createdAt);
		repository.setDefaultBranch("1");
		repository.setDescription(StateUtil.generateSentences(2,5));
		repository.setFirstCommitAt(this.firstCommitAt);
		repository.setHttpUrlToRepo(this.httpUrlToRepo);
		repository.setId(this.id);
		repository.setLastActivityAt(this.lastActivityAt);
		repository.setName(this.name);
		final Owner owner = new Owner();
		owner.setType("user");
		owner.setId(this.owner);
		repository.setOwner(owner);
		repository.setPublic(Boolean.toString(this.id%2==0));
		repository.setState("active");
		repository.setTags(this.tags);
		repository.setWebUrl(this.webUrl);
		return repository;
	}

	@Override
	public boolean createCommit(final String commitId, final String contributor) {
		final ImmutableCommitState state = this.commits.get(commitId);
		if(state==null) {
			this.commits.put(commitId,new ImmutableCommitState(commitId,contributor));
			final String branchId = selectBranch(commitId);
			this.branches.get(branchId).addContribution(commitId, contributor);
			Console.currentConsole().log("Contributed commit %s by committer %s to branch %s of repository %s",commitId,contributor,branchId,this.id);
		} else {
			Reports.currentReport().warn("Cannot create commit %s in repository %s (%s): commit already exists", state.getId(),this.id,this.name);
		}
		return state==null;
	}

	@Override
	public boolean createBranch(final String branchId) {
		final ImmutableBranchState state = this.branches.get(branchId);
		if(state==null) {
			final String name = nextBranchName(branchId);
			this.branches.put(branchId,new ImmutableBranchState(this.id,branchId,name));
			Console.currentConsole().log("Added branch %s (%s) to repository %s (%s)",branchId,name,this.id,this.name);
		} else {
			Reports.currentReport().warn("Cannot create branch %s (%s) in repository %s (%s): branch already exists", state.getId(),state.getName(),this.id,this.name);
		}
		return state==null;
	}

	@Override
	public boolean deleteCommit(final String commitId) {
		final ImmutableCommitState commit = this.commits.remove(commitId);
		if(commit!=null) {
			Console.currentConsole().log("Deleted commit %s from repository %s (%s)",commitId,this.id,this.name);
		} else {
			Reports.currentReport().warn("Cannot delete commit %s of repository %s (%s): commit does not exist", commitId,this.id,this.name);
		}
		return commit!=null;
	}

	@Override
	public boolean deleteBranch(final String branchId) {
		final ImmutableBranchState branch = this.branches.remove(branchId);
		if(branch!=null) {
			Console.currentConsole().log("Deleted branch %s (%s) from repository %s (%s)",branch.getId(),branch.getName(),this.id,this.name);
		} else {
			Reports.currentReport().warn("Cannot delete branch %s of repository %s (%s): commit does not exist", branchId,this.id,this.name);
		}
		return branch!=null;
	}

	private String selectBranch(final String commitId) {
		final List<String> branchIds=Lists.newArrayList(this.branches.keySet());
		return branchIds.get(commitId.hashCode()%branchIds.size());
	}

	private String nextBranchName(final String branchId) {
		if(this.branches.size()==0) {
			return "master";
		}
		if(this.branches.size()==1) {
			return "develop";
		}
		final int i = branchId.length()%5;
		if(i==0) {
			this.revision++;
			return "release/"+this.major+"."+this.minor+"."+this.revision;
		} else if(i==1) {
			this.minor++;
			this.revision=0;
			return "release/"+this.major+"."+this.minor+"."+this.revision;
		} else if(i==2) {
			this.major++;
			this.minor=0;
			this.revision=0;
			return "release/"+this.major+"."+this.minor+"."+this.revision;
		} else if(i==3) {
			this.revision++;
			return "hotfix/"+this.major+"."+this.minor+"."+this.revision;
		} else {
			this.feature++;
			return "feature/issue-"+this.feature;
		}
	}

}