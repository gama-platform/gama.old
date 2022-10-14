/**
* Name: Uniform diffusion
* Author: Benoit Gaudou
* Description: This model is used to show how a diffusion works with a uniform matrix of diffusion in a grid. The cell at the center of the grid emit a pheromon at each step, which is spread 
*     through the grid thanks to the diffusion mechanism. Without passing a diffusion matrix, the default diffusion matrix is a uniform matrix 3x3, with value 1/nb_neighbors.
* Tags: diffusion, matrix, math, elevation
*/

model uniform_diffusion

global {
	int size <- 128; // better to have a pow of 2 for the size of the grid
  	field cells <- field(size, size, 0.0);

	// Initialize the emiter cell as the cell at the center of the word
	reflex new_Value {
		
		cells[any_point_in(circle(25))] <- (100);
	}
	reflex diff {
		// Declare a diffusion on the grid "cells", with a uniform matrix of diffusion. 
		diffuse "trial" on: cells ;
	}
}

experiment diffusion type: gui {
	output  synchronized: true{
		display uniform_diffusion_in_8_neighbors_grid type: 3d camera:#from_up_front axes: false {
			mesh cells color: #green triangulation: true scale: 1 smooth: true ;
		}
	}
}
