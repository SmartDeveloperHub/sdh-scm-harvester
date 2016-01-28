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
package org.smartdeveloperhub.harvesters.scm.frontend.core.commit;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.ext.ContainerHandler;
import org.ldp4j.application.ext.annotations.DirectContainer;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.smartdeveloperhub.harvesters.scm.frontend.core.util.Serviceable;

@DirectContainer(
	id = CommitContainerHandler.ID,
	memberHandler = CommitHandler.class,
	membershipPredicate="http://www.smartdeveloperhub.org/vocabulary/scm#hasCommit"
)
public class CommitContainerHandler extends Serviceable implements ContainerHandler {

	public static final String ID   = "CommitContainerHandler";
	public static final String PATH = "commits/";
	public static final String NAME = "CommitContainer";

	@Override
	public DataSet get(final ResourceSnapshot resource) {
		// For the time there is nothing to return
		return DataSets.createDataSet(resource.name());
	}

	@Override
	public ResourceSnapshot create(final ContainerSnapshot container, final DataSet representation, final WriteSession session) {
		trace("Requested commit creation from: %n%s",representation);
		throw super.unexpectedFailure("Commit creation is not supported");
	}

}
