/**
* Name: Generate diffusion matrix with parameters
* Author: Julien Mazars
* Description: This model shows how to create a diffusion matrix by using the 4 parameters variation, proportion, radius and min-value. 
*     Manipulate the parameters to see in real time the result. The number displayed in each cells are the ratio of the initial signal 
*     diffused at the end of a step.
* Tags: diffusion, math, matrix
*/

model kernelmatrixgeneration

global {
	// parameters
	float variation <- 0.0;
	float proportion <- 1.0;
	int radius <- 1;
	float min_value <- 0.0;
	int cycle_length <- 1;
	
	// global variables
	int cell_max_size <- 51;
	int x_min <- cell_max_size;
	int x_max <- 0;
	int y_min <- cell_max_size;
	int y_max <- 0;
	
	float world_size <- 100.0;
	geometry shape <- cube(world_size);
	
	buffer_cells selected_cells;
	
	// init the emiter cell as the one in the center of the world.
	init {
		selected_cells <- location as buffer_cells;
	}
	
	reflex update {
		x_min <- cell_max_size;
		x_max <- 0;
		y_min <- cell_max_size;
		y_max <- 0;
		// copy the values of the buffer_cells (of the previous step) to the grid which will be displayed.
		ask cells {
			value <- (location as buffer_cells).value;
		}
		ask buffer_cells {
			// find the boundaries of the diffusion
			if (value != 0.0) {
				if (grid_x > x_max) {
					x_max <- grid_x;
				}
				if (grid_x < x_min) {
					x_min <- grid_x;
				}
				if (grid_y > y_max) {
					y_max <- grid_y;
				}
				if (grid_y < y_min) {
					y_min <- grid_y;
				}
			}
		}
		// re-initialize the value of the buffer grid to 0, and the value of the central cell to 1.
		ask buffer_cells {
			value <- 0.0;
			if (self = selected_cells) {
				value <- 1.0;
			}
		}
		// diffuse the value over the buffer grid, according to the parameters choosen.
		diffuse var:value on:buffer_cells variation:variation proportion:proportion radius:radius cycle_length:cycle_length min:min_value;
	}
}

// the buffer grid will be used to compute the diffusion at each step.
grid buffer_cells height:cell_max_size width:cell_max_size {
	// the diffused variable
	float value <- 0.0;
}

// this grid is the copy of the buffer grid at the end of each step. Indeed, we have to display the grid once the diffusion has been done.
grid cells height:cell_max_size width:cell_max_size {
	// the diffused variable
	float value <- 0.0;
	
	aspect base {
		// we only display the cells wich contains a non null value
		if (grid_x <= x_max and grid_x >= x_min and grid_y <= y_max and grid_y >= y_min)
		{
			// compute dynamically the size of the current cell.
			float size_cell <- world_size/(x_max-x_min+1);
			// compute dynamically the position of the current cell.
			point pos <- {(grid_x-x_min+1)*size_cell-0.5*size_cell,(grid_y-y_min+1)*size_cell-0.5*size_cell};
			// display each cell as a square. The color is linked to the value of the diffused variable.
			draw square(size_cell) color:hsb(value,1.0,1.0) border:#black at:pos;
			// display the ratio in each square.
			draw string(value) at: pos + {-size_cell/3,0,2} color: #black font: font("Helvetica", size_cell * #zoom * 2/3, #bold) perspective:true;
		}
	}
}

experiment my_experiment type:gui {
	parameter "proportion" var:proportion;
	parameter "variation" var:variation;
	parameter "min_value" var:min_value;
	parameter "radius" var:radius;
	parameter "cycle_length" var:cycle_length;
	
	init {
		// no need to have a faster display. Let's cool down your computer a bit ;)
		minimum_cycle_duration <- 200#ms;
	}
	output {
		display my_display type: 3d {
			species cells aspect:base;
		}
	}
}