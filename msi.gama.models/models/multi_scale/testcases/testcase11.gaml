/**
 * Purpose: Test the content of the "scheduling" variable.
 * 
 * Action(s):
 * 		1. Load the model.
 * 		2. Step the simulation.
 * 
 * Expected outcome:
 * 		1. A message is printed in the "Console" view in each step indicating that a B agent is running.
 * 
 * @deprecated "scheduling" variable is nolonger available. 
 */ 
model testcase11

global {
	init {
		create B;
	}
}

entities {
	species A {
		var x type: int init: 1;
	}
	
	species B parent: A {
		reflex { 
			do action: write {
				arg name: message value: 'agent: ' + name + ' with x = ' + (string (x));
			}
		}
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}