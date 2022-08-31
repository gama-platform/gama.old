
/***
* Name: RunThread 
* Author: Patrick Taillandier
* Description: This model illustrates the possibility of GAMA in terms of running a model in a specific thread
* A skill called thread was defined that allows to run the built-in runnable_action action. The runnable_action has to be overrided.
* Two other built-in actions are provided: start_thread that starts the thread, and end_thread, that ends the thread
* Tags: system, thread, skill
***/


model testThread

global skills: [thread]{
	init {
		//if there is one, stop the existing thread
		do end_thread;
			
		//create and start a new thread - the runnable_action model will be activated continuously every 10ms
		do start_thread continuous: true interval: 10;
	}
	
	reflex behavior {
		if cycle = 10000 {
			//end the thread
			do end_thread;
			do pause;
		}
	}
	
	//the action that will be concerned by the new thread
	action runnable_action {
		write "current time: " + #now;
	}	
}

experiment run_a_thread autorun: true;