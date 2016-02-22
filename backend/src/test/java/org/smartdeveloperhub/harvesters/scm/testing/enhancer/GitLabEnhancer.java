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

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class GitLabEnhancer {

	public static class UpdateReport {

		private Event event;
		private final List<String> warnings;
		private IOException failure;
		private String failureMessage;

		UpdateReport() {
			this.warnings=Lists.newArrayList();
		}

		UpdateReport curatedEvent(final Event event) {
			this.event = event;
			return this;
		}

		UpdateReport updateFailed(final IOException failure, final String message, final Object...args) {
			this.failure = failure;
			this.failureMessage=String.format(message,args);
			return this;
		}

		UpdateReport updateFailed(final IOException failure) {
			this.failure = failure;
			this.failureMessage="";
			return this;
		}

		UpdateReport warn(final String message, final Object... args) {
			this.warnings.add(String.format(message,args));
			return this;
		}

		public boolean enhancerUpdated() {
			return this.event!=null;
		}

		public boolean notificationSent() {
			return enhancerUpdated() && this.failure==null;
		}

		public String updateFailure() {
			return
				this.failureMessage+
				(this.failureMessage.isEmpty()?"":"\n")+
				Throwables.getStackTraceAsString(this.failure);
		}

		public Event curatedEvent() {
			return this.event;
		}

		public List<String> warnings() {
			return Collections.unmodifiableList(this.warnings);
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
			final UpdateReport report = updateEnhancer(event);
			notifyUpdate(report);
			return report;
		} finally {
			Console.remove();
		}
	}

	private void notifyUpdate(final UpdateReport report) {
		if(report.curatedEvent()!=null) {
			report.curatedEvent().setInstance(this.collector.getInstance());
			report.curatedEvent().setTimestamp(System.currentTimeMillis());
			try {
				this.collector.notify(report.curatedEvent());
			} catch (final IOException e) {
				report.updateFailed(e);
			}
		}
	}

	private UpdateReport updateEnhancer(final Event event) {
		UpdateReport report=null;
		if(event instanceof CommitterCreatedEvent) {
			report=createCommitters((CommitterCreatedEvent)event);
		} else if(event instanceof CommitterDeletedEvent) {
			report=deleteCommitters((CommitterDeletedEvent)event);
		} else if(event instanceof RepositoryCreatedEvent) {
			report=createRepositories((RepositoryCreatedEvent)event);
		} else if(event instanceof RepositoryDeletedEvent) {
			report=deleteRepositories((RepositoryDeletedEvent)event);
		} else if(event instanceof RepositoryUpdatedEvent) {
			report=updateRepository((RepositoryUpdatedEvent)event);
		} else {
			report=new UpdateReport();
			report.updateFailed(new IOException("Unsupported event type "+event.getClass().getSimpleName()));
		}
		return report;
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

	private UpdateReport updateRepository(final RepositoryUpdatedEvent event) {
		final UpdateReport report = new UpdateReport();
		final RepositoryUpdatedEvent curated=new RepositoryUpdatedEvent();
		curated.setRepository(event.getRepository());

		curated.getContributors().addAll(event.getContributors());
		curated.getContributors().retainAll(getCommitters());

		final List<String> dismissed=Lists.newArrayList(event.getContributors());
		dismissed.removeAll(curated.getContributors());
		if(!dismissed.isEmpty()) {
			report.warn("Dismissing activity for non-existing contributors (%s)",Joiner.on(", ").join(dismissed));
		}

		final RepositoryState state = findRepository(event.getRepository());
		for(final String id:event.getDeletedBranches()) {
			if(state.deleteBranch(id)) {
				curated.getDeletedBranches().add(id);
			} else {
				report.warn("Could not delete branch %s of repository %s",state.getId(),id);
			}
		}
		for(final String id:event.getDeletedCommits()) {
			if(state.deleteCommit(id)) {
				curated.getDeletedCommits().add(id);
			} else {
				report.warn("Could not delete commit %s of repository %s",state.getId(),id);
			}
		}

		if(!curated.getContributors().isEmpty()) {
			final Set<String> contributingContributors=Sets.newHashSet();

			final Iterator<String> candidateContributors =
					Iterators.
						cycle(curated.getContributors());

			for(final String id:event.getNewBranches()) {
				final String contributor=candidateContributors.next();
				if(state.createBranch(id)) {
					curated.getNewBranches().add(id);
					contributingContributors.add(contributor);
				} else {
					report.warn("Could not create branch %s for repository %s",state.getId(),id);
				}
			}

			for(final String id:event.getNewCommits()) {
				final String contributor=candidateContributors.next();
				if(state.createCommit(id,contributor)) {
					curated.getNewCommits().add(id);
					contributingContributors.add(contributor);
				} else {
					report.warn("Could not create commit %s for repository %s",state.getId(),id);
				}
			}

			curated.getContributors().retainAll(contributingContributors);
		} else {
			if(!event.getNewBranches().isEmpty()) {
				report.warn("Dismissing new branches (%s) as no existing contributors were specified",Joiner.on(", ").join(event.getNewBranches()));
			} else if(!event.getNewCommits().isEmpty()) {
				report.warn("Dismissing new commits (%s) as no existing contributors were specified",Joiner.on(", ").join(event.getNewCommits()));
			}
		}
		if (!curated.getDeletedBranches().isEmpty() ||
			!curated.getDeletedCommits().isEmpty()  ||
			!curated.getNewBranches().isEmpty()     ||
			!curated.getNewCommits().isEmpty()) {
			report.curatedEvent(curated);
		}
		return report;
	}

	/**
	 * NOTE: What happens to all the committers related to the repositories?
	 */
	private UpdateReport deleteRepositories(final RepositoryDeletedEvent event) {
		final UpdateReport report = new UpdateReport();
		final RepositoryDeletedEvent curated=new RepositoryDeletedEvent();
		for(final Integer repositoryId:event.getDeletedRepositories()) {
			if(this.repositories.remove(repositoryId)!=null) {
				curated.getDeletedRepositories().add(repositoryId);
				Console.currentConsole().log("Deleted repository %s",repositoryId);
			} else {
				report.warn("Repository %s does not exist",repositoryId);
			}
		}
		if(!curated.getDeletedRepositories().isEmpty()) {
			report.curatedEvent(curated);
		}
		return report;
	}

	private UpdateReport createRepositories(final RepositoryCreatedEvent event) {
		final UpdateReport report = new UpdateReport();
		if(!this.committers.isEmpty()) {
			final RepositoryCreatedEvent curated=new RepositoryCreatedEvent();
			final Iterator<String> committerIds = Iterators.cycle(this.committers.keySet());
			for(final Integer repositoryId:event.getNewRepositories()) {
				if(!this.repositories.containsKey(repositoryId)) {
					final RepositoryState repository =
						new ImmutableRepositoryState(
							repositoryId,
							this.committers.get(committerIds.next()));
					repository.createBranch("1");
					this.repositories.put(repositoryId, repository);
					curated.getNewRepositories().add(repositoryId);
				} else {
					report.warn("Repository %s already exists",repositoryId);
				}
			}
			if(!curated.getNewRepositories().isEmpty()) {
				report.curatedEvent(curated);
			}
		}
		return report;
	}

	/**
	 * NOTE: What happens to all the artifacts associated to this committers?
	 */
	private UpdateReport deleteCommitters(final CommitterDeletedEvent event) {
		final UpdateReport report = new UpdateReport();
		final CommitterDeletedEvent curated=new CommitterDeletedEvent();
		for(final String committerId:event.getDeletedCommitters()) {
			final CommitterState committer = this.committers.remove(committerId);
			if(committer!=null) {
				curated.getDeletedCommitters().add(committerId);
				Console.currentConsole().log("Deleted committer %s (%s)",committerId,committer.getName());
			} else {
				report.warn("Committer %s does not exist",committerId);
			}
		}
		if(!curated.getDeletedCommitters().isEmpty()) {
			report.curatedEvent(curated);
		}
		return report;
	}

	private UpdateReport createCommitters(final CommitterCreatedEvent event) {
		final UpdateReport report = new UpdateReport();
		final CommitterCreatedEvent curated=new CommitterCreatedEvent();
		for(final String committerId:event.getNewCommitters()) {
			if(!this.committers.containsKey(committerId)) {
				final CommitterState committer = new ImmutableCommitterState(committerId);
				this.committers.put(committerId, committer);
				curated.getNewCommitters().add(committerId);
			} else {
				report.warn("Committer %s already exists",committerId);
			}
		}
		if(!curated.getNewCommitters().isEmpty()) {
			report.curatedEvent(curated);
		}
		return report;
	}

	public static GitLabEnhancer newInstance(final GitCollector collector) {
		return new GitLabEnhancer(collector);
	}

}