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

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.CommitterDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.Event;
import org.smartdeveloperhub.harvesters.scm.backend.notification.GitCollector;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryCreatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryDeletedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryUpdatedEvent;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branch;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commit;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.User;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class GitLabEnhancer {

	public static class UpdateReport {
	
		private Event event;
		private final List<String> ignoredTasks;
		private IOException failure;
	
		UpdateReport() {
			this.ignoredTasks=Lists.newArrayList();
		}
	
		UpdateReport curatedEvent(final Event event) {
			this.event = event;
			return this;
		}
	
		UpdateReport updateFailure(final IOException failure) {
			this.failure = failure;
			return this;
		}
	
		UpdateReport warn(final String task) {
			this.ignoredTasks.add(task);
			return this;
		}
	
		public boolean wasSuccesful() {
			return this.event!=null && this.failure==null;
		}
	
		public String notificationFailure() {
			return Throwables.getStackTraceAsString(this.failure);
		}
	
		public Event curatedEvent() {
			return this.event;
		}
	
		public List<String> warnings() {
			return Collections.unmodifiableList(this.ignoredTasks);
		}
	
	}

	private final Map<String,CommitterState> committers;
	private final Map<Integer,RepositoryState> repositories;
	private final GitCollector collector;

	private Consumer consumer;

	private GitLabEnhancer(final GitCollector collector) {
		this.collector = collector;
		this.committers=Maps.newLinkedHashMap();
		this.repositories=Maps.newLinkedHashMap();
	}

	public GitLabEnhancer logTo(final Consumer consumer) {
		this.consumer = consumer;
		return this;
	}

	public Enhancer getEnhancer(final String instance) {
		final Enhancer en = new Enhancer();
		en.setId(instance);
		en.setName("Testing GitLab Enhancer Service");
		en.setStatus("OK");
		en.setVersion("0.1.0");
		en.getCollectors().add(this.collector.getConfig());
		return en;
	}

	public List<String> getCommitters() {
		return ImmutableList.copyOf(this.committers.keySet());
	}

	public User getCommitter(final String committerId) {
		return findCommitter(committerId).toEntity();
	}

	public List<Integer> getRepositories() {
		return ImmutableList.copyOf(this.repositories.keySet());
	}

	public Repository getRepository(final Integer repositoryId) {
		return findRepository(repositoryId).toEntity();
	}

	public List<String> getRepositoryBranches(final Integer repositoryId) {
		return findRepository(repositoryId).branches();
	}

	public List<String> getRepositoryCommits(final Integer repositoryId) {
		return findRepository(repositoryId).commits();
	}

	public Commit getRepositoryCommit(final Integer repositoryId, final String commitId) {
		return findRepository(repositoryId).commit(commitId).toEntity();
	}

	public Branch getRepositoryBranch(final Integer repositoryId, final String name) {
		return findRepository(repositoryId).branch(name).toEntity();
	}

	public List<String> getRepositoryBranchCommits(final Integer repositoryId, final String name) {
		return findRepository(repositoryId).branch(name).commits();
	}

	public UpdateReport update(final Event event) {
		Console.logTo(this.consumer);
		try {
			final UpdateReport report=new UpdateReport();
			Event filtered=null;
			if(event instanceof CommitterCreatedEvent) {
				filtered=createCommitters((CommitterCreatedEvent)event);
			} else if(event instanceof CommitterDeletedEvent) {
				filtered=deleteCommitters((CommitterDeletedEvent)event);
			} else if(event instanceof RepositoryCreatedEvent) {
				filtered=createRepositories((RepositoryCreatedEvent)event);
			} else if(event instanceof RepositoryDeletedEvent) {
				filtered=deleteRepositories((RepositoryDeletedEvent)event);
			} else if(event instanceof RepositoryUpdatedEvent) {
				filtered=updateRepository((RepositoryUpdatedEvent)event);
			} else {
				report.updateFailure(new IOException("Unsupported event type "+event.getClass().getSimpleName()));
			}
			if(filtered!=null) {
				filtered.setInstance(this.collector.getInstance());
				filtered.setTimestamp(System.currentTimeMillis());
				report.curatedEvent(filtered);
				try {
					this.collector.notify(filtered);
				} catch (final IOException e) {
					report.updateFailure(e);
				}
			}
			return report;
		} finally {
			Console.remove();
		}
	}

	private CommitterState findCommitter(final String committerId) {
		CommitterState state = this.committers.get(committerId);
		if(state==null) {
			state=new NullCommitterState(committerId);
		}
		return state;
	}

	private RepositoryState findRepository(final Integer repositoryId) {
		RepositoryState state = this.repositories.get(repositoryId);
		if(state==null) {
			state=new NullRepositoryState(repositoryId);
		}
		return state;
	}

	private RepositoryUpdatedEvent updateRepository(final RepositoryUpdatedEvent event) {
		final RepositoryUpdatedEvent filtered=new RepositoryUpdatedEvent();
		filtered.setRepository(event.getRepository());
		filtered.getContributors().addAll(event.getContributors());
		filtered.getContributors().retainAll(getCommitters());

		final RepositoryState state = findRepository(event.getRepository());
		for(final String id:event.getDeletedBranches()) {
			if(state.deleteBranch(id)) {
				filtered.getDeletedBranches().add(id);
			}
		}
		for(final String id:event.getDeletedCommits()) {
			if(state.deleteCommit(id)) {
				filtered.getDeletedCommits().add(id);
			}
		}

		if(!filtered.getContributors().isEmpty()) {
			final Set<String> contributingContributors=Sets.newHashSet();

			final Iterator<String> candidateContributors =
					Iterators.
						cycle(filtered.getContributors());

			for(final String id:event.getNewBranches()) {
				final String contributor=candidateContributors.next();
				if(state.createBranch(id)) {
					filtered.getNewBranches().add(id);
					contributingContributors.add(contributor);
				}
			}

			for(final String id:event.getNewCommits()) {
				final String contributor=candidateContributors.next();
				if(state.createCommit(id,contributor)) {
					filtered.getNewCommits().add(id);
					contributingContributors.add(contributor);
				}
			}

			filtered.getContributors().retainAll(contributingContributors);
		}
		return
			filtered.getDeletedBranches().isEmpty() &&
			filtered.getDeletedCommits().isEmpty()  &&
			filtered.getNewBranches().isEmpty()     &&
			filtered.getNewCommits().isEmpty()      ?
				null:
				filtered;
	}

	/**
	 * NOTE: What happens to all the committers related to the repositories?
	 */
	private RepositoryDeletedEvent deleteRepositories(final RepositoryDeletedEvent event) {
		final RepositoryDeletedEvent filtered=new RepositoryDeletedEvent();
		for(final Integer repositoryId:event.getDeletedRepositories()) {
			if(this.repositories.remove(repositoryId)!=null) {
				filtered.getDeletedRepositories().add(repositoryId);
				Console.currentConsole().log("Deleted repository %s",repositoryId);
			}
		}
		return filtered.getDeletedRepositories().isEmpty()?null:filtered;
	}

	private RepositoryCreatedEvent createRepositories(final RepositoryCreatedEvent event) {
		final RepositoryCreatedEvent filtered=new RepositoryCreatedEvent();
		if(!this.committers.isEmpty()) {
			final Iterator<String> committerIds = Iterators.cycle(this.committers.keySet());
			for(final Integer repositoryId:event.getNewRepositories()) {
				if(!this.repositories.containsKey(repositoryId)) {
					final RepositoryState repository =
						new ImmutableRepositoryState(
							repositoryId,
							this.committers.get(committerIds.next()));
					repository.createBranch("1");
					this.repositories.put(repositoryId, repository);
					filtered.getNewRepositories().add(repositoryId);
				}
			}
		}
		return filtered.getNewRepositories().isEmpty()?null:filtered;
	}

	/**
	 * NOTE: What happens to all the artifacts associated to this committers?
	 */
	private CommitterDeletedEvent deleteCommitters(final CommitterDeletedEvent event) {
		final CommitterDeletedEvent filtered=new CommitterDeletedEvent();
		for(final String committerId:event.getDeletedCommitters()) {
			final CommitterState committer = this.committers.remove(committerId);
			if(committer!=null) {
				filtered.getDeletedCommitters().add(committerId);
				Console.currentConsole().log("Deleted committer %s (%s)",committerId,committer.getName());
			}
		}
		return filtered.getDeletedCommitters().isEmpty()?null:filtered;
	}

	private CommitterCreatedEvent createCommitters(final CommitterCreatedEvent event) {
		final CommitterCreatedEvent filtered=new CommitterCreatedEvent();
		for(final String committerId:event.getNewCommitters()) {
			if(!this.committers.containsKey(committerId)) {
				final CommitterState committer = new ImmutableCommitterState(committerId);
				this.committers.put(committerId, committer);
				filtered.getNewCommitters().add(committerId);
			}
		}
		return filtered.getNewCommitters().isEmpty()?null:filtered;
	}

	public static GitLabEnhancer newInstance(final GitCollector collector) {
		return new GitLabEnhancer(collector);
	}

}