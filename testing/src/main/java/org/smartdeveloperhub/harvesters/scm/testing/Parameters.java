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

import org.smartdeveloperhub.harvesters.scm.testing.handlers.MoreHandlers.APIVersion;
import org.smartdeveloperhub.harvesters.scm.testing.util.AppAssembler;
import org.smartdeveloperhub.harvesters.scm.testing.util.Application;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.MoreObjects;

public final class Parameters {

	public static final class PortValidator implements IValueValidator<Integer> {

		@Override
		public void validate(final String name, final Integer value) throws ParameterException {
			if(value<1) {
				throw new ParameterException("Parameter "+name+" port value cannot be lower than 0 (found "+value+")");
			} else if(value>65535) {
				throw new ParameterException("Parameter "+name+" port value cannot be greater than 65535 (found "+value+")");
			}
		}

	}

	public static final class APIVersionValidator implements IParameterValidator {

		@Override
		public void validate(final String name, final String value) throws ParameterException {
			try {
				APIVersion.valueOf(value);
			} catch (final Exception e) {
				throw new ParameterException(
					"Value '" + value+ "' is not a valid GitLab Enhancer API version. "+
					"Valid values for parameter "+name+" are: '"+APIVersion.v1.name()+"' "+
					"and '"+APIVersion.v2.name()+"'", e);
			}
		}

	}

	public static final class APIVersionConverter implements IStringConverter<APIVersion> {

		@Override
		public APIVersion convert(final String value) {
			return APIVersion.valueOf(value);
		}

	}

	@Parameter(names = { "-h", "--host" }, description = "Host where the service will be available")
	String host = "localhost";

	@Parameter(names = { "-p", "--port" }, description = "Port to be used by the service")
	Integer port = 8080;

	@Parameter(names = { "-bh", "--broker-host" }, description = "Host where the RabbitMQ broker to be used for sending notifications is available")
	String brokerHost = "localhost";

	@Parameter(names = { "-bp", "--broker-port" }, description = "Port used by the RabbitMQ broker to be used for sending notifications", validateValueWith=Parameters.PortValidator.class)
	Integer brokerPort = 5672;

	@Parameter(names = { "-v", "--virtual-host" }, description = "Virtual host to be used for the sending notifications")
	String virtualHost = "/";

	@Parameter(names = { "-e", "--exchange-name" }, description = "Exchange name to be used for the sending notifications")
	String exchangeName = "git.collector.mock";

	@Parameter(names = { "-a", "--api-version"}, description = "Version of the GitLab Enhancer API to expose", validateWith=Parameters.APIVersionValidator.class, converter=Parameters.APIVersionConverter.class)
	APIVersion apiVersion = APIVersion.v2;

	@Parameter(names = { "-H", "--help" }, description = "Show this help", help=true)
	boolean help = false;

	@Override
	public String toString() {
		return
			MoreObjects.
				toStringHelper(getClass()).
					omitNullValues().
					add("host",this.host).
					add("port",this.port).
					add("brokerHost",this.brokerHost).
					add("brokerPort",this.brokerPort).
					add("virtualHost",this.virtualHost).
					add("exchangeName",this.exchangeName).
					add("apiVersion",this.apiVersion).
					toString();
	}

	static Parameters create(final String... args) {
		final Parameters parameters=new Parameters();
		try {
			final JCommander commander = new JCommander(parameters, args);
			commander.setProgramName(AppAssembler.applicationName(StandaloneTestingService.class));
			commander.setColumnSize(120);
			commander.setAllowParameterOverwriting(false);
			if(parameters.help) {
				commander.usage();
				System.exit(0);
			}
		} catch (final ParameterException e) {
			System.err.println("ERROR: "+e.getMessage());
			Application.logContext(args);
			System.exit(-1);
		}
		return parameters;
	}
}