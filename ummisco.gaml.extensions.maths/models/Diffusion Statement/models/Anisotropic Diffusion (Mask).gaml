/**
* Name: Anisotropic diffusion with mask
* Author: Benoit Gaudou
* Description: This model is used to show how an anisotropic diffusion can be used with a mask. The cell at the center of the grid emit a pheromon at each step, which is spread
*     through the grid thanks to the diffusion mechanism. A mask is used to restrict the diffusion to a "corridor" (the white part of the bmp image)
* Tag: Diffusion, Matrix, Elevation, Mask
*/

model diffusion

global {
	int grid_size <- 51;
  	geometry shape <- envelope(square(grid_size) * 10);
  	cells selected_cells;
  	// Load the image mask as a matrix. The white part of the image is the part where diffusion will work, and the black part is where diffusion will be blocked.
  	matrix mymask <- file("../includes/mask.bmp") as_matrix({grid_size,grid_size});
  	// Declare the anisotropic matrix (diffuse from the center)
  	matrix<float> math_diff <- matrix([
									[1/9,1/9,1/9],
									[1/9,1/7,1/9],
									[1/9,1/9,1/9]]);
	// Initialize the emiter cell as the cell at the center of the word
	init {
		selected_cells <- location as cells;
	}
	reflex new_Value {
		ask selected_cells {
			phero <- 1.0;
		}
	}

	reflex diff {
		// Declare a diffusion on the grid "cells". The value of the diffusion will be store in the new variable "phero" of the cell.
		diffusion var: phero on: cells mat_diffu: math_diff mask: mymask;	
	}
}


grid cells height: grid_size width: grid_size {
	// "phero" is the variable storing the value of the diffusion
	float phero <- 0.0;
	// the color of the cell is linked to the value of "phero".
	rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
	// Update the "grid_value", which will be used for the elevation of the cell
	float grid_value update: min([100, phero * 100]);
} 


experiment diffusion type: gui {
	output {
		display a type: opengl {
			// Display the grid with elevation
			grid cells elevation: true triangulation: true;
		}
	}
}
