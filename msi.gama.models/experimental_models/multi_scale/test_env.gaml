model test_env

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	const gridsize type: int init: 50;
	
	init {
		/*
		do action: write {
			arg name: message value: name + ' with gridsize = ' + gridsize; 
		}
		*/
		
		create species: moving_ball number: 1;
	}
}

entities {
	species moving_ball skills: [moving] {
		var shape type: geometry init: circle (1.0);
		
		reflex move_around {
			do action: wander {
				arg name: speed value: 1;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}		
	}
	
	species ball skills: situated {
		var shape type: geometry init: square (1.0);
		
		aspect default {
			draw shape: geometry color: rgb ('blue');
		}
	}
	
	species group skills: situated {
		// need ball_delegation's AgentManager be intialized before initializing "shape" variable
		var shape type: geometry value: polygon ( (list (ball_delegation)) collect (each.location) );
		
		const grid_topo_size type: int init: 10;
		
		// need grid_topo's AgentManager be initialized before initializing "grid_topo_agents" variable.
		var grid_topo_agents type: list of: grid_topo init: list (grid_topo);
		
		delegation ball_delegation species: ball {
			
		}
		
		// need "grid_topo_size" and "shape" be intialized before initializing grid_topo's AgentManager
		topology grid_topo width: grid_topo_size height: grid_topo_size {
			
		}
		
		aspect default {
			draw shape: geometry color: rgb ('pink');
		}
	}
	
}

environment width: gridsize height: gridsize {
	// have to initialize "gridsize" variable before initializing my_grid's AgentManager
	grid my_grid width: gridsize height: gridsize { // PROBLEM: (gridsize == 0) as the AgentManager is initialized before (gridsize) variables!!!
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			grid my_grid;
			species moving_ball;
			
			species group transparency: 0.5 {
				micro_layer ball_delegation;
			}
		}
	}
}
