/**
 * Purpose: Test the execution order of a agent and its micro-agents.
 * 
 * Action(s):
 * 		1. Load the model.
 * 		2. Step the simulation.
 * 
 * Expected outcome:
 * 		1. Three lines are printed in the "Console" view in each simulation step indicating the execution order is: "world_species0", A0, B0. 
 */
model testcase12

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
		create species: B number: 1;
	}
	
	var scheduling type: list value: (list (A)) + (list (B));
	
	reflex {
		do action: write {
			arg name: message value: 'agent: ' + name + ' at time: ' + (string (time));
		}
	}
}

entities {
	species A {
		var x type: int init: 1;
		
		reflex {
			do action: write {
				arg name: message value: 'agent: ' + name + ' with x = ' + (string (x));
			}
		}
	}
	
	species B parent: A {
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}