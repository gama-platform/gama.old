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
		
	vegetation_cell my_cell <- one_of (vegetation_cell) ; 
		
	init { 
		location <- my_cell.location;
	}
		
	reflex basic_move { 
		my_cell <- one_of (my_cell.neighbors) ;
		location <- my_cell.location ;
	}

	aspect base {
		draw circle(size) color: color ;
	}
}

grid vegetation_cell width: 10 height: 10 neighbors: 4 {
	float max_food <- 1.0 ;
	rgb color <- rnd_color(255) ;
}

experiment "Record and keep same seed" record: true keep_seed: true {
	float seed <- 0.0;
	
	output {
		display main_display antialias:false{
			grid vegetation_cell border: #black ;
			species prey aspect: base;
		}
		display location_x type: 2d antialias:false{
			chart "grid_x of the prey" {
				data "grid_x" value: first(prey).my_cell.grid_x;
			}
		}	
		
	}
}

experiment "Record without keeping seed" record: true keep_seed: false {
	output {
		display main_display antialias:false{
			grid vegetation_cell border: #black ;
			species prey aspect: base;
		}
		display location_x type: 2d antialias:false{
			chart "grid_x of the prey" {
				data "grid_x" value: first(prey).my_cell.grid_x;
			}
		}	
		
	}
}
 