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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-frontend:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.publisher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.session.AttachmentSnapshot;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.scm.backend.notification.RepositoryUpdatedEvent;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.repository.RepositoryHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.IdentityUtil;

@RunWith(JMockit.class)
public class PublisherHelperTest {

	@Test
	public void verifyIsUtilityClass() {
		assertThat(Utils.isUtilityClass(PublisherHelper.class),equalTo(true));
	}

	@Test
	public void testPublishUsers(@Mocked final WriteSession session, @Mocked final ContainerSnapshot container) {
		new Expectations() {{
			session.find(ContainerSnapshot.class, IdentityUtil.userContainerName(), UserContainerHandler.class);this.result=container;
			container.addMember(IdentityUtil.userName("user1"));
			container.addMember(IdentityUtil.userName("user2"));
		}};
		PublisherHelper.
			publishUsers(session, Arrays.asList("user1","user2"));
	}

	@Test
	public void testUnpublishUsers(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) {
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.userName("user1"), UserHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.userName("user2"), UserHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			unpublishUsers(session, Arrays.asList("user1","user2"));
	}

	@Test
	public void testFindRepositoryContainer(@Mocked final WriteSession session, @Mocked final ContainerSnapshot container) {
		final URI target = URI.create("target");
		new Expectations() {{
			session.find(ContainerSnapshot.class, IdentityUtil.enhancerName(target), RepositoryContainerHandler.class);this.result=container;
		}};
		assertThat(
			PublisherHelper.
				findRepositoryContainer(session, target),
			sameInstance(container));
	}

	@Test
	public void testPublishRepositories(@Mocked final WriteSession session, @Mocked final ContainerSnapshot container, @Mocked final ResourceSnapshot resource) {
		final URI target = URI.create("target");
		new Expectations() {{
			session.find(ContainerSnapshot.class, IdentityUtil.enhancerName(target), RepositoryContainerHandler.class);this.result=container;
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(1), RepositoryHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(2), RepositoryHandler.class);this.result=null;
			container.addMember(IdentityUtil.repositoryName(2));this.result=resource;
			resource.createAttachedResource(ContainerSnapshot.class, RepositoryHandler.REPOSITORY_BRANCHES, IdentityUtil.repositoryName(2), BranchContainerHandler.class);
			resource.createAttachedResource(ContainerSnapshot.class, RepositoryHandler.REPOSITORY_COMMITS, IdentityUtil.repositoryName(2), CommitContainerHandler.class);
		}};
		PublisherHelper.
			publishRepositories(session, target, Arrays.asList(1,2));
	}

	@Test
	public void testUnpublishRepositories(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) {
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(1), RepositoryHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(2), RepositoryHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			unpublishRepositories(session, Arrays.asList(1,2));
	}

	@Test
	public void testUpdateRepositories$repositoryNotFound(@Mocked final WriteSession session, @Mocked final ResourceSnapshot repository, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setNewCommits(Arrays.asList("commit1","commit2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(1), RepositoryHandler.class);this.result=null;
		}};
		try {
			PublisherHelper.
				updateRepository(session, event);
			fail("Should fail if the repository does not exist");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Repository 1 does not exist"));
		}
	}

	@Test
	public void testUpdateRepositories$newCommits(@Mocked final WriteSession session, @Mocked final ResourceSnapshot repository, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setNewCommits(Arrays.asList("commit1","commit2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(1), RepositoryHandler.class);this.result=repository;
			repository.attachmentById(RepositoryHandler.REPOSITORY_COMMITS);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.commitName(new CommitKey(1,"commit1")));
			container.addMember(IdentityUtil.commitName(new CommitKey(1,"commit2")));
		}};
		PublisherHelper.
			updateRepository(session, event);
	}

	@Test
	public void testUpdateRepositories$newCommitsWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot repository, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setNewCommits(Arrays.asList("commit1","commit2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(1), RepositoryHandler.class);this.result=repository;
			repository.attachmentById(RepositoryHandler.REPOSITORY_COMMITS);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.commitName(new CommitKey(1,"commit1")));this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateRepository(session, event);
			fail("Should fail if cannot add commit");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not add new commits to repository 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateRepositories$newBranches(@Mocked final WriteSession session, @Mocked final ResourceSnapshot repository, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setNewBranches(Arrays.asList("branch1","branch2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(1), RepositoryHandler.class);this.result=repository;
			repository.attachmentById(RepositoryHandler.REPOSITORY_BRANCHES);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.branchName(new BranchKey(1,"branch1")));
			container.addMember(IdentityUtil.branchName(new BranchKey(1,"branch2")));
		}};
		PublisherHelper.
			updateRepository(session, event);
	}

	@Test
	public void testUpdateRepositories$newBranchesWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot repository, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setNewBranches(Arrays.asList("branch1","branch2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.repositoryName(1), RepositoryHandler.class);this.result=repository;
			repository.attachmentById(RepositoryHandler.REPOSITORY_BRANCHES);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.branchName(new BranchKey(1,"branch1")));this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateRepository(session, event);
			fail("Should fail if cannot add branches");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not add new branches to repository 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateRepository$deletedCommits(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setDeletedCommits(Arrays.asList("commit1","commit2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.commitName(new CommitKey(1,"commit1")), CommitHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.commitName(new CommitKey(1,"commit2")), CommitHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			updateRepository(session, event);
	}

	@Test
	public void testUpdateRepository$deletedCommitsWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setDeletedCommits(Arrays.asList("commit1","commit2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.commitName(new CommitKey(1,"commit1")), CommitHandler.class);this.result=resource;
			session.delete(resource);this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateRepository(session, event);
			fail("Should fail if cannot remove commits");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not remove commits from repository 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateRepository$deletedBranches(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setDeletedBranches(Arrays.asList("branch1","branch2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.branchName(new BranchKey(1,"branch1")), BranchHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.branchName(new BranchKey(1,"branch2")), BranchHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			updateRepository(session, event);
	}

	@Test
	public void testUpdateRepository$deletedBranchesWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final RepositoryUpdatedEvent event=new RepositoryUpdatedEvent();
		event.setInstance(target.toString());
		event.setRepository(1);
		event.setDeletedBranches(Arrays.asList("branch1","branch2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.branchName(new BranchKey(1,"branch1")), BranchHandler.class);this.result=resource;
			session.delete(resource);this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateRepository(session, event);
			fail("Should fail if cannot add branches");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not remove branches from repository 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

}
