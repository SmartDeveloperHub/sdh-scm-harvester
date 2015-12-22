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
package org.smartdeveloperhub.harvesters.scm.frontend.core.publisher;

import java.util.ArrayList;

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.scm.frontend.core.GitLabHarvester;
import org.smartdeveloperhub.harvesters.scm.frontend.core.user.UserContainerHandler;

public class UserPublisherThread extends Thread {
	
	 private static final Logger LOGGER=LoggerFactory.getLogger(UserPublisherThread.class); 
	
	 private Thread t;
	 private String threadName = "UserPublisherThread";
	 BackendController controller;
	 GitLabHarvester gitLabHarvester;
	 
	 public UserPublisherThread(BackendController controller) {
			super();
			this.controller = controller;
		}
		 
		public void run(){
			LOGGER.info(threadName +" is running...");
			long startTime = System.currentTimeMillis();
									
			try{					
				ApplicationContext ctx = ApplicationContext.getInstance();
				publishUserResources(ctx);
										
			} catch (Exception e) {
				LOGGER.error("Could not update repository information resource",e);
			} finally {
				LOGGER.debug("Finalized update repository process");
			}
				
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			LOGGER.info("- thread elapsed time (ms)..: {}",elapsedTime);
				
		}	
		
		 public void start ()
		   {
			 LOGGER.info("Starting " +  threadName); 
		    if (t == null)
		      {
		         t = new Thread (this, threadName);
		         t.start ();
		      }
		   }
		 
			public void publishUserResources(ApplicationContext ctx) throws Exception{
				try(WriteSession session = ctx.createSession()){				
					Name<String> userContainerName = NamingScheme.getDefault().name(UserContainerHandler.NAME);
					ContainerSnapshot userContainerSnapshot = session.find(ContainerSnapshot.class, userContainerName ,UserContainerHandler.class);			
					if(userContainerSnapshot==null) {
						LOGGER.warn("User Container does not exits");
						return;
					}
					
					ArrayList<String> userIds = controller.getUsers();	
					for (String userId:userIds){			
						Name<String> userName = NamingScheme.getDefault().name(userId);			
						ResourceSnapshot userSnapshot = userContainerSnapshot.addMember(userName);
						//LOGGER.debug("Published resource for user {} @ {} ({})",userId, userSnapshot.name(),userSnapshot.templateId());
					}
					
					session.modify(userContainerSnapshot);	
					session.saveChanges();
				}
				
			}	
}
