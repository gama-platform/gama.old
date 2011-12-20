/**
 * Purpose: Test the access of host's variable from the micro-agent context.
 * 
 * Action(s):
 * 		1. Load the model.
 * 		2. Step the simulation.
 * 
 * Expected outcome:
 * 		1. The host name and "x" value are printed correctly in the "Console" view.
 */
model testcase15

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
	}
	
	var scheduling type: list value: list (A);
	
}

entities {
	species A {
		var x type: int init: 1;
		var scheduling type: list value: list (B);
		
		init {
			create species: B number: 1;
		}
		
		species B {
			reflex {
				do action: write {
					arg name: message value: 'agent: ' + name +  ' with host: ' + (string (host)) + '; host.x = ' + (string ( (A (host)).x ));
					// TODO how to avoid explicitly casting the host type?
				}
			}
		}
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}