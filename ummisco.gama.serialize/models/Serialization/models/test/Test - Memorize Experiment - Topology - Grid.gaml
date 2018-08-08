/**
* Name: Dynamic of the vegetation (grid)
* Author:
* Description: Second part of the tutorial : Predator Prey
* Tags: grid
*/

model prey_predator

global {
	init {
		create prey number: 1;
	}
}

species prey {
	float size <- 5.0 ;
	rgb color <- #blue;
		
	vegetation_cell myCell <- one_of (vegetation_cell) ; 
		
	init { 
		location <- myCell.location;
	}
		
	reflex basic_move { 
		myCell <- one_of (myCell.neighbors) ;
		location <- myCell.location ;
	}

	aspect base {
		draw circle(size) color: color ;
	}
}

grid vegetation_cell width: 10 height: 10 neighbors: 4 {
	float maxFood <- 1.0 ;
	rgb color <- rnd_color(255) ;
}

experiment prey_predator type: memorize keep_seed: true {
	
	output {
		display main_display {
			grid vegetation_cell lines: #black ;
			species prey aspect: base;
		}
	}
}

 