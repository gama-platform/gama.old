/**
* Name: Uniform diffusion with mask (Avoid mask)
* Author: Julien Mazars
* Description: This model is used to show how a uniform diffusion can be used with a mask. The cell at the center of the grid emit a pheromon at each step, which is spread 
*     through the grid thanks to the diffusion mechanism. A mask is used to restrict the diffusion to a "corridor" (the white part of the bmp image). The first display shows a 
*     diffusion avoiding the masked cells (the value is redistributed to the neighboring cells, to have a constant number of pheromon), the second display shows a diffusion 
*     without avoiding the masked cells (the value is diffused in the masked cell, and never rediffused again).
* Tags: diffusion, matrix, math, obstacle, elevation
*/

model diffusion_with_mask

global {
	int grid_size <- 64; // better to have a pow of 2 for the size of the grid
  	geometry shape <- envelope(square(grid_size) * 10);
  	cells_avoid_mask selected_cells1;
  	cells_diffuse_on_mask selected_cells2;
  	// Load the image mask as a matrix. The white part of the image is the part where diffusion will work, and the black part is where diffusion will be blocked.
  	matrix mymask <- file("../includes/complex_mask.bmp") as_matrix({grid_size,grid_size});
  	// Declare a uniform diffusion matrix
  	matrix<float> mat_diff <- matrix([
									[1/9,1/9,1/9],
									[1/9,1/9,1/9],
									[1/9,1/9,1/9]]);
	// Initialize the emiter cell as the cell at the center of the word
	init {
		selected_cells1 <- location as cells_avoid_mask;
		selected_cells2 <- location as cells_diffuse_on_mask;
	}
	reflex new_Value {
		ask selected_cells1 {
			phero <- 1.0;
		}
		ask selected_cells2 {
			phero <- 1.0;
		}
	}

	reflex diff {
		// Declare a diffusion on the grid "cells". The value of the diffusion will be store in the new variable "phero" of the cell.
		diffuse var: phero on: cells_avoid_mask matrix: mat_diff mask: mymask avoid_mask: true;
		diffuse var: phero on: cells_diffuse_on_mask matrix: mat_diff mask: mymask;	
	}
}


grid cells_avoid_mask height: grid_size width: grid_size {
	// "phero" is the variable storing the value of the diffusion
	float phero <- 0.0;
	// the color of the cell is linked to the value of "phero".
	rgb color <- (((mymask row_at grid_y) at grid_x) < -1) ? #black : hsb(phero,1.0,1.0) update: (((mymask row_at grid_y) at grid_x) < -1) ? #black : hsb(phero,1.0,1.0);
	// Update the "grid_value", which will be used for the elevation of the cell
	float grid_value update: phero * 100;
} 

grid cells_diffuse_on_mask height: grid_size width: grid_size {
	// "phero" is the variable storing the value of the diffusion
	float phero <- 0.0;
	// the color of the cell is linked to the value of "phero".
	rgb color <- (((mymask row_at grid_y) at grid_x) < -1) ? #black : hsb(phero,1.0,1.0) update: (((mymask row_at grid_y) at grid_x) < -1) ? #black : hsb(phero,1.0,1.0);
	// Update the "grid_value", which will be used for the elevation of the cell
	float grid_value update: phero * 100;
} 


experiment diffusion type: gui {
	output {
		layout #split;
		display diffusion_avoiding_mask type: 3d antialias:false{
			camera 'default' location: {-237.5703,1046.6397,370.0514} target: {320.0,320.0,0.0};
			// Display the grid with elevation
			grid cells_avoid_mask elevation: true triangulation: true;
		}
		display diffusion_on_mask type: 3d antialias:false{
			camera 'default' location: {-237.5703,1046.6397,370.0514} target: {320.0,320.0,0.0};
			// Display the grid with elevation
			grid cells_diffuse_on_mask elevation: true triangulation: true;
		}
	}
}
