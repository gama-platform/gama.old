/**
* Name: HelloWorldBDI
* Author: Patrick Taillandier
* Description: Minimal model
* Tags: simple_bdi, plan, predicate, desire
*/

model HelloWorldBDI

global {
	init {
		create bdi_species;
	}
}

//add the simple_bdi architecture to the agents
species bdi_species control: simple_bdi {
	
	//define a new predicate that will be used as a desire
	predicate saying_hello_desire <- new_predicate("say hello");
	
	//at init, add the saying_hello_desire to the agent desire base
	init {
		do add_desire(saying_hello_desire);
	}
	
	//definition of a plan that allow to fulfill the  saying_hello_desire intention
	plan saying_hello intention: saying_hello_desire{
		write "Hello World!";
	}
}

experiment HelloWorldBDI type: gui ;