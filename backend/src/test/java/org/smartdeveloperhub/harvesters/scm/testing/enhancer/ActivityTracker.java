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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.testing.enhancer.Activity.Action;

final class ActivityTracker {

	private final class StateLocator extends StateVisitor {

		private String location;

		@Override
		void visitCommitter(final CommitterState state) {
			this.location=CURRENT.get().resolve("/users/%s", state.getId());
		}

		@Override
		void visitRepository(final RepositoryState state) {
			this.location=CURRENT.get().resolve("/projects/%s", state.getId());
		}

		@Override
		void visitBranch(final BranchState state) {
			this.location=CURRENT.get().resolve("/projects/%s/branches/%s",state.getRepositoryId(),state.getId());
		}

		@Override
		void visitCommit(final CommitState state) {
			this.location=CURRENT.get().resolve("/projects/%s/commits/%s",state.getRepositoryId(),state.getId());
		}

		<K,T> String resolve(final State<K,T> state) {
			this.location=null;
			if(state!=null) {
				state.accept(this);
			}
			return this.location;
		}
	}

	interface ActivityContext extends ActivityListener {

		String resolve(String path, Object... args);

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(ActivityTracker.class);

	private static ThreadLocal<ActivityContext> CURRENT=new ThreadLocal<ActivityContext>() {

		@Override
		protected ActivityContext initialValue() {
			return new ActivityContext() {
				@Override
				public void onActivity(final Activity<?> activity) {
				}
				@Override
				public String resolve(final String path, final Object... args) {
					return null;
				}
			};
		}

	};

	private ActivityTracker() {
	}

	private <K,T> void submitActivity(final State<K,T> state, final Action action) {
		final Activity<K> activity =
			Activity.
				<K>builder().
					action(action).
					entity(state.getEntity()).
					targetId(state.getId()).
					representation(state.getRepresentation()).
					targetLocation(new StateLocator().resolve(state)).
					build();
		LOGGER.debug(activity.getDescription());
		CURRENT.get().onActivity(activity);
	}

	void log(final String format, final Object... args) {
		final Activity<String> activity =
			Activity.
				<String>builder().
					action(Action.LOG).
					description(format,args).
					build();
		LOGGER.debug(activity.getDescription());
		CURRENT.get().onActivity(activity);
	}

	<K,T> void created(final State<K,T> state) {
		submitActivity(state,Action.CREATED);
	}

	<K,T> void updated(final State<K,T> state) {
		submitActivity(state,Action.UPDATED);
	}

	<K,T> void deleted(final State<K,T> state) {
		submitActivity(state,Action.DELETED);
	}

	static ActivityTracker currentTracker() {
		return new ActivityTracker();
	}

	static ActivityTracker useContext(final ActivityContext consumer) {
		if(consumer==null) {
			CURRENT.remove();
		} else {
			CURRENT.set(consumer);
		}
		return new ActivityTracker();
	}

	static void remove() {
		CURRENT.remove();
	}

}