/**
* Name: Uniform diffusion
* Author: Benoit Gaudou
* Description: This model is used to show how a diffusion works with a uniform matrix of diffusion in a grid. The cell at the center of the grid emit a pheromon at each step, which is spread
*     through the grid thanks to the diffusion mechanism.
* Tags: Diffusion, Matrix, Elevation
*/

model diffusion

global {
	int taille <- 51;
  	geometry shape <- envelope(square(taille) * 10);
  	cells selected_cells;
  	
  	// Define a uniform matrix of diffusion
  	matrix<float> math_diff <- matrix([
									[1/9,1/9,1/9],
									[1/9,1/9,1/9],
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
		// Declare a diffusion on the grid "cells", with a uniform matrix of diffusion. The value of the diffusion
		// will be store in the new variable "phero" of the cell.
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
