/**
* Name: Moving cells
* Author: Arnaud Grignard
* Description: Second part of the tutorial : Tuto3D
* Tags: grid, agent_movement
*/
model Tuto3D   

global {
	int nb_cells <- 100;
	int environment_size <- 100;
	geometry shape <- cube(environment_size);

	init {
		create cell number: nb_cells {
			location <- {rnd(environment_size), rnd(environment_size), rnd(environment_size)};
		}
	}
}

species cell skills: [moving3D] {
	reflex move {
		do move;
	}

	aspect default {
		draw sphere(environment_size * 0.01) color: #blue;
	}
}

experiment Tuto3D type: gui {
	parameter "Initial number of cells: " var: nb_cells min: 1 max: 1000 category: "Cells";
	output {
		display View1 type: 3d {
			graphics "env" {
				draw cube(environment_size) color: #black wireframe: true;
			}
			species cell;
		}
	}
}