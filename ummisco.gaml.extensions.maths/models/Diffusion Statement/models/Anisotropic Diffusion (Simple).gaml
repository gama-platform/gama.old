/**
* Name: Anisotropic diffusion with mask
* Author: Benoit Gaudou
* Description: This model is used to show how to construct an anisotropic diffusion through a grid. The cell at the center of the grid emit a pheromon at each step, which is spread
*     through the grid thanks to the diffusion mechanism, using a particular matrix of diffusion.
* Tags: Diffusion, Matrix, Elevation
*/

model diffusion

global {
	int taille <- 51;
  	geometry shape <- envelope(square(taille) * 10);
  	cells selected_cells;
  	
  	// Declare the anisotropic matrix (diffuse to the left-upper direction)
  	matrix<float> math_diff <- matrix([
									[2/9,2/9,1/9],
									[2/9,1/9,0.0],
									[1/9,0.0,0.0]]);

	// Initialize the emiter cell as the cell at the center of the word
	init {
		selected_cells <- location as cells;
	}
	reflex new_Value {
		ask selected_cells {
			phero <- 1.0;
			write phero;
		}
	}
	
	reflex diff {
		// Declare a diffusion on the grid "cells". The value of the diffusion will be store in the new variable "phero" of the cell.
		diffuse var: phero on: cells matrix: math_diff;	
	}
}


grid cells height: taille width: taille {
	// "phero" is the variable storing the value of the diffusion
	float phero  <- 0.0;
	// the color of the cell is linked to the value of "phero".
	rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
	// Update the "grid_value", which will be used for the elevation of the cell
	float grid_value update: phero * 100;
} 


experiment diffusion type: gui {
	output {
		display a type: opengl {
			// Display the grid with elevation
			grid cells elevation: true triangulation: true;
		}
	}
}
