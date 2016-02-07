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
package org.smartdeveloperhub.harvesters.scm.backend.notification;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

final class CollectorAggregator {

	private static final Logger LOGGER=LoggerFactory.getLogger(CollectorAggregator.class);

	private final NotificationListener listener;
	private final Multimap<String,Collector> collectors;
	private final Map<String,CollectorController> controllers;
	private final Multimap<String,String> instances;
	private final Map<String,String> queues;
	private final BlockingQueue<SuspendedNotification> queue;

	private final Deque<CollectorController> connected;

	private NotificationPump pump;

	private final String name;

	private CollectorAggregator(final String name, final NotificationListener listener) {
		this.name       =name;
		this.listener   =listener;
		this.collectors =LinkedListMultimap.create();
		this.instances  =LinkedListMultimap.create();
		this.queues     =Maps.newLinkedHashMap();
		this.controllers=Maps.newLinkedHashMap();
		this.queue      =new LinkedBlockingQueue<>();
		this.connected  =Lists.newLinkedList();
	}

	void connect(final List<Collector> collectors) throws IOException {
		LOGGER.info("Setting up collector aggregator for {}...",this.name);
		startNotificationPump();
		for(final Collector collector:collectors) {
			addCollector(collector, queueName(collector));
		}
		LOGGER.info("Collector aggregator for {} connected",this.name);
	}

	CollectorController controller(final String instance) {
		CollectorController result=null;
		final String queueName = this.queues.get(instance);
		if(queueName!=null) {
			result=this.controllers.get(queueName);
		}
		return result;
	}

	void disconnect() {
		LOGGER.info("Disconnecting {} collector aggregator...",this.name);
		shutdownGracefully();
		LOGGER.info("Collector aggregator for {} disconnected",this.name);
	}

	private void addCollector(final Collector collector, final String queueName) throws IOException {
		checkArgument(!this.instances.containsValue(collector.getInstance()));
		this.instances.put(queueName,collector.getInstance());
		this.queues.put(collector.getInstance(),queueName);
		if(this.collectors.put(queueName,collector)) {
			final CollectorController controller=startController(collector,queueName);
			this.controllers.put(queueName, controller);
			this.connected.add(controller);
		}
	}

	private CollectorController startController(final Collector collector, final String queueName) throws IOException {
		final CollectorController controller = new CollectorController(collector,queueName,this.queue);
		LOGGER.info("Connecting controller for collector {}...",collector.getInstance());
		try {
			controller.connect();
			return controller;
		} catch (final ControllerException e) {
			LOGGER.warn("Could not connect controller for collector {}. Full stacktrace follows",collector.getInstance(),e);
			shutdownGracefully();
			throw new IOException("Could not connect to controller "+collector.getInstance()+" broker ("+collector+")",e);
		}
	}

	private void startNotificationPump() {
		this.pump=new NotificationPump(this.queue,this.listener);
		this.pump.start();
	}

	private String queueName(final Collector collector) {
		final Integer hash =
			Objects.hash(
				collector.getBrokerHost(),
				collector.getBrokerPort(),
				collector.getVirtualHost(),
				collector.getExchangeName());
		return String.format("%s.collector.hash%8X",this.name,hash);
	}

	private void shutdownGracefully() {
		disconnectControllers();
		this.instances.clear();
		this.collectors.clear();
		this.queue.clear();
		stopNotificationPump();
	}

	private void stopNotificationPump() {
		this.pump.stop();
		this.pump=null;
	}

	private void disconnectControllers() {
		final Iterator<CollectorController> iterator = this.connected.descendingIterator();
		while(iterator.hasNext()) {
			iterator.next().disconnect();
		}
		this.connected.clear();
		this.controllers.clear();
	}

	static CollectorAggregator newInstance(final String name, final NotificationListener listener) {
		return new CollectorAggregator(name,listener);
	}
}
