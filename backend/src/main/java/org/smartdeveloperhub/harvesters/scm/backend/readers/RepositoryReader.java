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
package org.smartdeveloperhub.harvesters.scm.backend.readers;

import java.io.IOException;
import java.util.List;

import org.smartdeveloperhub.harvesters.scm.backend.pojos.Branches;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Commits;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repositories;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public final class RepositoryReader {

	private final ObjectMapper mapper = new ObjectMapper();

	public Repositories readReposistories(final String repositoriesIS) throws IOException {
		final List<String> list =
			this.mapper.readValue(
				repositoriesIS,
				TypeFactory.
					defaultInstance().
						constructCollectionType(List.class,String.class));
		final Repositories repositories = new Repositories();
		repositories.setRepositoryIds(list);
		return repositories;
	}

	public Repository readRepository(final String repositoryIS, final String branchesIS, final String commitsIS) throws IOException {
		final Repository repository = this.mapper.readValue(repositoryIS, Repository.class);
		if (branchesIS!=null && !branchesIS.isEmpty()){
			final BranchReader branchReader = new BranchReader();
			final Branches branches = branchReader.readBranches(branchesIS);
			repository.setBranches(branches);
		}
		if (commitsIS!=null && !commitsIS.isEmpty()){
			final CommitReader commitReader = new CommitReader();
			final Commits commits = commitReader.readCommits(commitsIS);
			repository.setCommits(commits);
		}
		return repository;
	}

}
