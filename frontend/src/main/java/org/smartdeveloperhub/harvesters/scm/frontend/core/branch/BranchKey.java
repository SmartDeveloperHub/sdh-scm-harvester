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
package org.smartdeveloperhub.harvesters.scm.frontend.core.branch;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.MoreObjects;

public final class BranchKey implements Comparable<BranchKey>, Serializable {

	private static final long serialVersionUID = 786423257542885122L;

	private final Integer repoId;
	private final String branchId;

	public BranchKey(final Integer repoId, final String branchId) {
		this.repoId = repoId;
		this.branchId = branchId;
	}

	public String getBranchId() {
		return this.branchId;
	}

	public Integer getRepoId() {
		return this.repoId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.repoId,this.branchId);
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result=false;
		if(obj instanceof BranchKey) {
			final BranchKey that=(BranchKey)obj;
			result=
				Objects.equals(this.repoId,that.repoId) &&
				Objects.equals(this.branchId,that.branchId);
		}
		return result;
	}

	@Override
	public int compareTo(final BranchKey key) {
		int result=this.repoId.compareTo(key.repoId);
		if(result==0) {
			result=this.branchId.compareTo(key.branchId);
		}
		return result;
	}

	@Override
	public String toString() {
		return
			MoreObjects.
				toStringHelper(getClass()).
					omitNullValues().
					add("repoId",this.repoId).
					add("branchId", this.branchId).
					toString();
	}

}
