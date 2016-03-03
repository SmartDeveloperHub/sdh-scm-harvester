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
package org.smartdeveloperhub.harvesters.scm.frontend.core.util;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.net.URI;

import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.scm.frontend.core.branch.BranchKey;
import org.smartdeveloperhub.harvesters.scm.frontend.core.commit.CommitKey;

public final class IdentityUtil {

	private IdentityUtil() {
	}

	private static <T> T extractNameId(final Name<?> name, final String entityName, final Class<? extends T> idClazz) {
		final Serializable id=name.id();
		checkState(idClazz.isInstance(id),"%s identifier should be a %s not a %s",entityName,idClazz.getName(),id.getClass().getCanonicalName());
		return idClazz.cast(id);
	}

	public static Name<URI> enhancerName(final URI target) {
		return NamingScheme.getDefault().name(target);
	}

	public static Name<Integer> repositoryName(final Integer repositoryId) {
		return NamingScheme.getDefault().name(repositoryId);
	}

	public static Integer repositoryId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Repository", Integer.class);
	}

	public static Name<String> userName(final String userId) {
		return NamingScheme.getDefault().name(userId);
	}

	public static String userId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"User",String.class);
	}

	public static Name<BranchKey> branchName(final BranchKey key) {
		return NamingScheme.getDefault().name(key);
	}

	public static BranchKey branchId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Branch",BranchKey.class);
	}

	public static Name<CommitKey> commitName(final CommitKey key) {
		return NamingScheme.getDefault().name(key);
	}

	public static CommitKey commitId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Commit",CommitKey.class);
	}

}
