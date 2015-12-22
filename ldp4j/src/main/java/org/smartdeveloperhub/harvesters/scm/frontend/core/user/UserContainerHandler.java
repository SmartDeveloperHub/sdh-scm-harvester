/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015 Center for Open Middleware.
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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm.ldp4j:scm-harvester-ldp4j:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.frontend.core.user;

import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ContainerHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.DirectContainer;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.smartdeveloperhub.harvesters.scm.frontend.core.Repository.RepositoryContainerHandler;
import org.smartdeveloperhub.harvesters.scm.frontend.core.Repository.RepositoryHandler;

@DirectContainer(
		id = UserContainerHandler.ID,
		memberHandler = UserHandler.class,
		membershipPredicate="http://www.smartdeveloperhub.org/vocabulary/scm#hasCommitter"
	)
public class UserContainerHandler  implements ContainerHandler {
	
	public static final String ID="UserContainerHandler";
	//public static final String path="committers/";
	public static final String NAME = "UserContainer";

	public DataSet get(ResourceSnapshot resource)
			throws UnknownResourceException, ApplicationRuntimeException {
		return
				DataSets.
					createDataSet(resource.name());
	}

	public ResourceSnapshot create(ContainerSnapshot container,
			DataSet representation, WriteSession session)
			throws UnknownResourceException, UnsupportedContentException,
			ApplicationRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}
}
