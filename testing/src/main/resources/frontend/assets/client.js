/*
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
/**
 * @param {string} data
 * @typedef {Object} Activity
 * @property {string} id
 * @property {number} timestamp
 * @property {string} action
 * @property {string} description
 * @property {string} entity
 * @property {Object} targetId
 * @property {string} targetLocation
 * @property {Object} representation
 */
function Activity(data) {
	var json=JSON.parse(data);
	this.id=json["id"];
	this.timestamp=json["timestamp"];
	this.action=json["action"];
	this.entity=json["entity"];
	this.targetId=json["targetId"];
	this.description=json["description"];
	this.targetLocation=json["targetLocation"];
	this.representation=json["representation"];
}

/**
 * @typedef {Object} ServerSentEvent
 * @property {string} id
 * @property {string} event
 * @property {string} data
 */

/**
 * @param {ServerSentEvent} event
 */
function processServerSentEvent(event) {
	var console = document.getElementById('console');
	var activity = new Activity(event.data);
	if(activity.action=="LOG") {
		console.innerHTML += activity.description+"\n";
	}
}

if (window.EventSource) {
	var source = new EventSource("console");
	source.onmessage = processServerSentEvent;
} else {
	alert("Your browser does not support Server Sent Events. (Use Chrome)");
}

window.onload = function() {
	createScript();

	// Get the modal
	var modal = document.getElementById('modal');

	// Get the <span> element that closes the modal
	var modalCloseButton = document.getElementById('button');

	// When the user clicks on <span> (x), close the modal
	modalCloseButton.onclick = function() {
		//modal.style.display = "none";
		modal.style.visibility ="hidden";
	};

	// When the user clicks anywhere outside of the modal, close it
	window.onclick = function(event) {
		if (event.target == modal) {
			//modal.style.display = "none";
			modal.style.visibility ="hidden";
		}
	};
};

function send(message, type) {
	if (!window.EventSource) {
		return false;
	}
	var http = new XMLHttpRequest();
	http.open("POST", "../collector", true);
	http.setRequestHeader("Content-type","application/psr.sdh.gitcollector+json");
	http.setRequestHeader("X-Event-Type", type);
	http.onreadystatechange = function() {
		if(http.readyState == 4) {
			var strWarnings="";
			var strFailure="";
			if(http.getResponseHeader("Content-Type") == "application/json") {
				var response=JSON.parse(http.responseText);
				if(response.warnings.length>0) {
					strWarnings+="<b>Warning:</b>";
					strWarnings+="<br>- "+response.warnings.join("<br>- ");
				}
				if(response.failure != undefined) {
					strFailure=response.failure;
				} else if(response.curatedEvent != undefined) {
					var checkbox=document.getElementById("displayEvent");
					if(checkbox.checked) {
						if(strWarnings.length>0) {
							strWarnings+="<br><br>";
						}
						strWarnings+="<b>Notification sent:</b><br>";
						strFailure=JSON.stringify(response.curatedEvent,null,2);
						if(response.sideEffects != undefined && response.sideEffects.length>0) {
							strFailure+="<br><br>";
							strFailure+="<b>Side-effect Notification(s) sent:</b><br>";
							for(j=0;j<response.sideEffects.length;j++) {
								strFailure+=JSON.stringify(response.sideEffects[j],null,2);
								strFailure+="<br>";
							}
						}
					}
				}
			} else {
				strFailure=http.responseText;
			}
			if((strWarnings+strFailure).length>0) {
				var body = document.getElementById('body');
				body.innerHTML = strWarnings+"<pre>"+strFailure+"</pre>";
				var modal = document.getElementById('modal');
				modal.style.visibility= "visible";
			}
		}
	};

	http.send(message);
	return false;
}

var digits= ['0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'];

function hexString(length) {
	var result="";
	var i;
	for(i=0;i<length;i++) {
		result+=digits[Math.floor((Math.random() * 16))];
	}
	return result;
}

function createScript() {
	var selector=document.getElementById("types");
	var index = selector.selectedIndex;
	var event;
	if(index==0) { // Create committers
		event= { "newCommiters" : [ "c"+hexString(3) ] }
	} else if(index==1) { // Delete committers
		event= { "deletedCommitters" : [ "c"+hexString(3) ] }
	} else if(index==2) { // Create repositories
		event= { "newRepositories" : [ 1 ] }
	} else if(index==3) { // Create branches
		event= {
			"repository" : 1 ,
			"newBranches" : [ "b"+hexString(6), "b"+hexString(6) ],
			"contributors" : [ "c"+hexString(3), "c"+hexString(3) ]
		}
	} else if(index==4) { // Create commits
		event= {
			"repository" : 1 ,
			"newCommits" : [ hexString(6), hexString(6) ],
			"contributors" : [ "c"+hexString(3), "c"+hexString(3) ]
		}
	} else if(index==5) { // Delete branches
		event= {
			"repository" : 1 ,
			"deletedBranches" : [ "b"+hexString(6), "b"+hexString(6) ]
		}
	} else if(index==6) { // Delete commits
		event= {
			"repository" : 1 ,
			"deletedCommits" : [ hexString(6), hexString(6) ]
		}
	} else if(index==7) { // Free-lunch update
		event= {
			"repository" : 1 ,
			"newBranches" : [ "b"+hexString(6), "b"+hexString(6) ],
			"deletedBranches" : [ "b"+hexString(6), "b"+hexString(6) ],
			"newCommits" : [ hexString(6), hexString(6) ],
			"deletedCommits" : [ hexString(6), hexString(6) ],
			"contributors" : [ "c"+hexString(3), "c"+hexString(3) ]
		}
	} else { // Delete repositories
		event= { "deletedRepositories" : [ 1 ] }
	}
	$('#msg').val(JSON.stringify(event,null,2))
}