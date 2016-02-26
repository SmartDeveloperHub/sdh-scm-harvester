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

final class Console {

	private static final class NullConsumer implements Consumer {
		@Override
		public void log(final String message) {
		}
	}

	private static final Logger LOGGER=LoggerFactory.getLogger(Console.class);

	private static ThreadLocal<Consumer> CURRENT=new ThreadLocal<Consumer>() {

		@Override
		protected Consumer initialValue() {
			return new NullConsumer();
		}

	};

	private Console() {
	}

	void log(final String format, final Object... args) {
		final String message = String.format(format, args);
		LOGGER.debug(message);
		CURRENT.get().log(message);
	}

	static Console currentConsole() {
		return new Console();
	}

	static Console logTo(final Consumer consumer) {
		if(consumer==null) {
			CURRENT.remove();
		} else {
			CURRENT.set(consumer);
		}
		return new Console();
	}

	static void remove() {
		CURRENT.remove();
	}

}