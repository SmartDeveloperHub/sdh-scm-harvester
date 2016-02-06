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

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.backend.pojos.Collector;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ReturnListener;

final class CollectorController {

	interface Cleaner {

		void clean(Channel channel) throws IOException;

	}

	private final class LoggingReturnListener implements ReturnListener {

		@Override
		public void handleReturn(final int replyCode, final String replyText, final String exchange, final String routingKey, final BasicProperties properties, final byte[] body) throws IOException {
			LOGGER.warn(
				"Message {} publication in {}:{} failed ({}): {}",
				properties.getHeaders().get(GITCOLLECTOR_CONTROLLER_MESSAGE),
				exchange,
				routingKey,
				replyCode,
				replyText);
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(CollectorController.class);

	private static final String EXCHANGE_TYPE="topic";
	private static final String GITCOLLECTOR_CONTROLLER_MESSAGE = "X-CollectorController-Message";

	private final String queueName;
	private final Collector collector;
	private final BlockingQueue<SuspendedNotification> propagationQueue;

	private final Lock write;

	private final Deque<Cleaner> cleaners;
	private final List<NotificationConsumer> callbacks;

	private final AtomicLong messageCounter;

	private String actualQueueName;

	private final ConnectionManager manager;

	CollectorController(final Collector collector, final String queueName, final BlockingQueue<SuspendedNotification> propagationQueue) {
		this.collector=collector;
		this.manager=new ConnectionManager(collector.getInstance(),collector.getBrokerHost(),collector.getBrokerPort(),collector.getVirtualHost());
		this.queueName=queueName;
		this.propagationQueue=propagationQueue;
		final ReadWriteLock lock=new ReentrantReadWriteLock();
		this.write=lock.writeLock();
		this.cleaners=Lists.newLinkedList();
		this.callbacks=Lists.newArrayList();
		this.messageCounter=new AtomicLong();
	}

	Collector collector() {
		return this.collector;
	}

	String queueName() {
		return this.queueName;
	}

	String actualQueueName() {
		return this.actualQueueName;
	}

	void connect() throws ControllerException {
		this.write.lock();
		try {
			this.manager.connect();
			declareExchange();
			prepareQueue();
		} finally {
			this.write.unlock();
		}
	}

	void disconnect() {
		this.write.lock();
		try {
			if(this.manager.isConnected()) {
				cleanUp();
				this.callbacks.clear();
				this.manager.disconnect();
			}
		} finally {
			this.write.unlock();
		}
	}

	void publishEvent(final Event event) throws IOException {
		publishEvent(EventUtil.marshall(event), event.getClass().getSimpleName());
	}

	void publishEvent(final String event, final String eventType) throws IOException {
		final String exchangeName=this.collector.getExchangeName();
		final String routingKey=Notifications.ROUTING_KEY_BASE+eventType;
		final Channel aChannel = this.manager.currentChannel();
		aChannel.addReturnListener(new LoggingReturnListener());
		try {
			LOGGER.debug("Publishing message to exchange '{}' and routing key '{}'. Payload: \n{}",exchangeName,routingKey,event);
			final Map<String, Object> headers=Maps.newLinkedHashMap();
			headers.put(GITCOLLECTOR_CONTROLLER_MESSAGE,this.messageCounter.incrementAndGet());
			headers.put(HttpHeaders.CONTENT_TYPE,Notifications.MIME);
			aChannel.
				basicPublish(
					exchangeName,
					routingKey,
					true,
					MessageProperties.MINIMAL_PERSISTENT_BASIC.builder().headers(headers).build(),
					event.getBytes());
		} catch (final IOException e) {
			this.manager.discardChannel(aChannel);
			LOGGER.warn("Could not publish message [{}] to exchange '{}' and routing key '{}': {}",event,exchangeName,routingKey,e.getMessage());
			throw e;
		} catch (final Exception e) {
			this.manager.discardChannel(aChannel);
			final String errorMessage = String.format("Unexpected failure while publishing message [%s] to exchange '%s' and routing key '%s' using broker %s:%s%s: %s",event,exchangeName,routingKey,this.collector.getBrokerHost(),this.collector.getBrokerPort(),this.collector.getVirtualHost(),e.getMessage());
			LOGGER.error(errorMessage);
			throw new IOException(errorMessage,e);
		}
	}

	private void prepareQueue() throws ControllerException {
		if(this.propagationQueue!=null) {
			this.actualQueueName = declareQueue();
			bindQueue(this.actualQueueName);
			try {
				final Channel currentChannel = this.manager.channel();
				final NotificationConsumer callback = new NotificationConsumer(currentChannel,this.propagationQueue);
				currentChannel.
					basicConsume(
						this.actualQueueName,
						false,
						callback
					);
				this.callbacks.add(callback);
			} catch (final IOException e) {
				throw new ControllerException("Could not register consumer for queue '"+this.actualQueueName+"'",e);
			}
		}
	}

	/**
	 * The a durable topic exchange is always declared
	 */
	private void declareExchange() throws ControllerException {
		try {
			this.manager.channel().exchangeDeclare(this.collector.getExchangeName(),EXCHANGE_TYPE,true);
		} catch (final IOException e) {
			if(!FailureAnalyzer.isExchangeDeclarationRecoverable(e)) {
				throw new ControllerException("Could not create "+this.queueName+" exchange named '"+this.collector.getExchangeName()+"'",e);
			}
		}
	}

	/**
	 * The declared queues are durable, exclusive, and auto-delete, and expire if no
	 * client uses them after 1 second.
	 */
	private String declareQueue() throws ControllerException {
		final String targetQueueName=Optional.fromNullable(this.queueName).or("");
		try {
			final Map<String, Object> args=
				ImmutableMap.
					<String, Object>builder().
						put("x-expires",1000).
						build();
			final DeclareOk ok = this.manager.channel().queueDeclare(targetQueueName,true,true,true,args);
			final String declaredQueueName = ok.getQueue();
			this.cleaners.push(CleanerFactory.queueDelete(declaredQueueName));
			return declaredQueueName;
		} catch (final IOException e) {
			throw new ControllerException("Could not create queue named '"+targetQueueName+"'",e);
		}
	}

	private void bindQueue(final String queueName) throws ControllerException {
		try {
			this.manager.channel().queueBind(queueName,this.collector.getExchangeName(),Notifications.ROUTING_KEY_PATTERN);
			this.cleaners.push(CleanerFactory.queueUnbind(this.collector.getExchangeName(),queueName,Notifications.ROUTING_KEY_PATTERN));
		} catch (final IOException e) {
			throw new ControllerException("Could not bind "+this.queueName+" queue '"+queueName+"' to exchange '"+this.collector.getExchangeName()+"' using routing key '"+Notifications.ROUTING_KEY_PATTERN+"'",e);
		}
	}

	private void cleanUp() {
		LOGGER.debug("Cleaning up broker ({})...",this.cleaners.size());
		while(!this.cleaners.isEmpty()) {
			final Cleaner cleaner=this.cleaners.pop();
			try {
				cleaner.clean(this.manager.channel());
				LOGGER.trace("{} completed",cleaner);
			} catch (final Exception e) {
				LOGGER.warn("{} failed. Full stacktrace follows",cleaner,e);
			}
		}
		LOGGER.debug("Broker clean-up completed.",this.cleaners.size());
	}

}