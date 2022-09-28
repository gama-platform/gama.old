
/***
* Name: RunThread 
* Author: Patrick Taillandier
* Description: This model illustrates the possibility of GAMA to run a model or any action in a specific thread. 
* A skill called 'thread' allows to run in a thread the built-in 'thread_action' action. If overriden, this 'thread_action' is run in a thread.
* Two other built-in actions are provided: start_thread that starts the thread, and end_thread, that ends the thread.
* In this model, the skill is attached to the global species (with a fixed rate) and to a species of agents (with a fixed delay). It can be attached to any species, and multiple agents can run multiple threads.
* When the agents are killed, their thread is automatically stopped if it is running. 
* Tags: system, thread, skill
***/


model testThread 

global skills: [thread]{
	bool create_agents <- false; 
	init {			
		//create and start a new thread - the thread_action will be activated continuously with a delay of 2#s between each execution
		
		if (create_agents) {
			create thread_agent number: 2;
		}
		do run_thread interval: 2#s;
	}
	  
  
	
	//the action run in the thread 
	action thread_action {
		write "current time: " + #now;
	}	 
}

species thread_agent skills: [thread] {
	//create and start a new thread - the thread_action will be activated continuously at a fixed rate every 1#s by the 2 agents
	
	init {
		do run_thread every: 1#s;
	}
	
	//the action run in the thread
	action thread_action {
		write " > " + self + "current time: " + #now;
	}	
}
  
experiment "Run global thread" autorun: true;

experiment "Run several threads" autorun: true {
	action _init_ {
		create simulation with: [create_agents:: true];
	}
}