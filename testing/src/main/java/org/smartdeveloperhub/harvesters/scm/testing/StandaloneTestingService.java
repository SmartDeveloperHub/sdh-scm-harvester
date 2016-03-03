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
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-testing-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing;

import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.serverSentEvents;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.sse.ServerSentEventHandler;

import java.io.IOException;
import java.util.Scanner;

public class StandaloneTestingService {

	public static void main(final String... args) {
		final ServerSentEventHandler consoleHandler = serverSentEvents();
		final TestingService service =
			TestingService.
				builder().
					port(8080).
					exchangeName("git.collector.mock").
					listener(new ServerSentEventConsumer(consoleHandler)).
					addEndpoint("/frontend/",resourceHandler().addWelcomeFiles("index.html")).
					addEndpoint("/frontend/assets/{asset}",resourceHandler()).
					addEndpoint("/frontend/console",consoleHandler).
					build();

		try {
			String build = System.getProperty("service.build","");
			if(!build.isEmpty()) {
				build="-b"+build;
			}
			System.out.printf(
				"Testing GitLab Enhancer Service v%s%s%n",
				System.getProperty("service.version"),
				build);
			service.start();
			awaitTerminationRequest();
			service.shutdown();
		} catch (final IOException e) {
			System.err.println("Could not start service. Full stacktrace follows:");
			e.printStackTrace(System.err);
		}
	}

	private static ResourceHandler resourceHandler() {
		return resource(
			new ClassPathResourceManager(
				StandaloneTestingService.class.getClassLoader()));
	}

	private static void awaitTerminationRequest() {
		System.out.println("Hit <ENTER> to exit...");
		try(final Scanner scanner = new Scanner(System.in)) {
			String readString = scanner.nextLine();
			while(readString != null) {
				if (readString.isEmpty()) {
					break;
				}
				if (scanner.hasNextLine()) {
					readString = scanner.nextLine();
				} else {
					readString = null;
				}
			}
		}
	}

}