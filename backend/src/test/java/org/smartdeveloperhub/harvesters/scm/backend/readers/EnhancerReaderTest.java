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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Enhancer;

import com.google.common.collect.Iterables;


public class EnhancerReaderTest extends ReaderTestHelper {

	private EnhancerReader sut;

	@Before
	public void setUp() {
		this.sut= new EnhancerReader();
	}

	@Test
	public void testReadEnhancer() throws Exception {
		final Collector defaultCollector = defaultCollector();
		final Enhancer defaultEnhancer = defaultEnhancer();
		defaultEnhancer.setCollectors(Arrays.asList(defaultCollector));

		final String serialize = serialize(defaultEnhancer);
		System.out.println(serialize);

		final Enhancer readEnhancer=
			this.sut.
				readEnhancer(
					defaultEnhancer.getId(),
					serialize(defaultEnhancer),
					serializeList(defaultEnhancer.getRepositories()),
					serializeList(defaultEnhancer.getUsers()));

		verifyEnhancer(defaultEnhancer, readEnhancer);
		final Collector readCollector = Iterables.getFirst(readEnhancer.getCollectors(), null);
		verifyCollector(defaultCollector, readCollector);
	}

	private void verifyEnhancer(final Enhancer defaultEnhancer,
			final Enhancer readEnhancer) {
		assertThat(readEnhancer.getAdditionalProperties(),equalTo(defaultEnhancer.getAdditionalProperties()));
		assertThat(readEnhancer.getId(),equalTo(defaultEnhancer.getId()));
		assertThat(readEnhancer.getName(),equalTo(defaultEnhancer.getName()));
		assertThat(readEnhancer.getVersion(),equalTo(defaultEnhancer.getVersion()));
		assertThat(readEnhancer.getStatus(),equalTo(defaultEnhancer.getStatus()));
		assertThat(readEnhancer.getRepositories(),equalTo(defaultEnhancer.getRepositories()));
		assertThat(readEnhancer.getUsers(),equalTo(defaultEnhancer.getUsers()));

		assertThat(readEnhancer.getCollectors(),hasSize(1));
	}

	private void verifyCollector(final Collector defaultCollector,
			final Collector readCollector) {
		assertThat(readCollector.getAdditionalProperties(),equalTo(defaultCollector.getAdditionalProperties()));
		assertThat(readCollector.getInstance(),equalTo(defaultCollector.getInstance()));
		assertThat(readCollector.getBrokerHost(),equalTo(defaultCollector.getBrokerHost()));
		assertThat(readCollector.getBrokerPort(),equalTo(defaultCollector.getBrokerPort()));
		assertThat(readCollector.getVirtualHost(),equalTo(defaultCollector.getVirtualHost()));
		assertThat(readCollector.getExchangeName(),equalTo(defaultCollector.getExchangeName()));
	}

	private Enhancer defaultEnhancer() {
		final Enhancer defaultEnhancer=new Enhancer();
		defaultEnhancer.setId("http://russell.dia.fi.upm.es:5000/api");
		defaultEnhancer.setName("Gitlab Enhancer");
		defaultEnhancer.setVersion("1.0.3");
		defaultEnhancer.setStatus("OK");
		defaultEnhancer.setRepositories(Arrays.asList("1","2"));
		defaultEnhancer.setUsers(Arrays.asList("u1","u2"));
		defaultEnhancer.setAdditionalProperty("notifications", "not-supported");
		return defaultEnhancer;
	}

	private Collector defaultCollector() {
		final Collector collector=new Collector();
		collector.setInstance("instance");
		collector.setBrokerHost("brokerHost");
		collector.setBrokerPort(1234);
		collector.setVirtualHost("/virtualHost");
		collector.setExchangeName("exchangeName");
		return collector;
	}

}
