/**
* Name: Anisotropic diffusion (Cycle length)
* Author: Benoit Gaudou
* Description: This model is used to show how to use diffusion on a grid, and how to accélérate the process by computing several times the diffusion at each step.
*     The cell at the center of the grid emit a pheromon at each step, which is spread through the grid thanks to the diffusion mechanism, using a particular matrix of diffusion. 
* Tags: Diffusion, Matrix, Elevation
*/

model diffusion

global {
	int taille <- 51;
  	geometry shape <- envelope(square(taille) * 10);
  	cells selected_cells;
  	quick_cells selected_quick_cells;
  	// Declare the anisotropic matrix (diffuse to the left-upper direction)
  	matrix<float> math_diff <- matrix([
									[1/9,1/9,1/9],
									[1/9,1/9,1/9],
									[1/9,1/9,1/9]]);

	// Initialize the emiter cell as the cell at the center of the word
	init {
		selected_cells <- location as cells;
		selected_quick_cells <- location as quick_cells;
	}
	reflex new_Value {
		ask(selected_cells){
			phero <- 1.0;
		}
		ask(selected_quick_cells){
			phero <- 10.0;
		}		
	}

	reflex diff {
		// Declare a diffusion on the grid "cells" and on "quick_cells". The diffusion declared on "quick_cells" will make 10 computations at each step to accelerate the process. 
		// The value of the diffusion will be store in the new variable "phero" of the cell.
		diffuse var: phero on: cells matrix: math_diff;	
		diffuse var: phero on: quick_cells matrix: math_diff cycle_length: 10;			
	}
}


grid cells height: taille width: taille {
	// "phero" is the variable storing the value of the diffusion
	float phero  <- 0.0;
	// The color of the cell is linked to the value of "phero".
	rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
} 

grid quick_cells height: taille width: taille {
	// "phero" is the variable storing the value of the diffusion
	float phero  <- 0.0;
	// The color of the cell is linked to the value of "phero".
	rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
} 


experiment diffusion type: gui {
	output {
		display a type: opengl {
			// Display the grid with elevation
			grid cells elevation: phero * 100 triangulation: true;
		}
		display quick type: opengl {
			// Display the grid with elevation
			grid quick_cells elevation: phero * 100 triangulation: true;
		}
	}
}
