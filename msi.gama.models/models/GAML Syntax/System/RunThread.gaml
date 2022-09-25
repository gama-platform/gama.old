
/***
* Name: RunThread 
* Author: Patrick Taillandier
* Description: This model illustrates the possibility of GAMA to run a model or any action in a specific thread. 
* A skill called 'thread' allows to run in a thread the built-in 'thread_action' action. If overriden, this 'thread_action' is run in a thread.
* Two other built-in actions are provided: start_thread that starts the thread, and end_thread, that ends the thread.
* In this model, the skill is attached to the global species and to a species of agents. It can be attached to any species, and multiple agents can run multiple threads.
* When the agents are killed, their thread is automatically stopped if it is running
* Tags: system, thread, skill
***/


model testThread

global skills: [thread]{
	bool create_agents <- false;
	init {			
		//create and start a new thread - the runnable_action model will be activated continuously every 10ms
		
		if (create_agents) {
			create thread_agent number: 2;
		} else {
			do run_thread every: 1#s;
		}	
	}
	

	
	//the action that will be concerned by the new thread
	action thread_action {
		write "current time: " + #now;
	}	
}

species thread_agent skills: [thread] {
	init {
		do run_thread every: 1#s;
	}
	
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