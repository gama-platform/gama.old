/**
* Name: Anisotropic diffusion (Toroidal)
* Author: Benoit Gaudou
* Description: This model is used to show how to construct an anisotropic diffusion through a grid. The cell at the center of the grid emit a pheromon at each step, which is spread 
*     through the grid thanks to the diffusion mechanism, using a particular matrix of diffusion, in a toroidal world.
* Tags: diffusion, matrix, math, elevation
*/

model anisotropic_diffusion_torus

global torus: true {
	int size <- 64; // better to have a pow of 2 for the size of the grid
  	geometry shape <- envelope(square(size) * 10);
  	cells selected_cells;
  	matrix<float> mat_diff <- matrix([
									[4/9,2/9,0/9],
									[2/9,1/9,0.0],
									[0/9,0.0,0.0]]);
	init {
		selected_cells <- location as cells;
	}
	reflex new_Value {
		ask selected_cells{
			phero <- 1.0;
		}  
	}

	reflex diff {
		diffuse var: phero on: cells matrix: mat_diff method:dot_product;	
	}
}


grid cells height: size width: size  {
	float phero  <- 0.0;
	rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
	float grid_value update: phero * 100;
} 


experiment diffusion type: gui {
	output {
		display a type: 3d antialias:false{
			camera 'default' location: {277.7744,923.8543,660.6005} target: {320.0,320.0,0.0};
			grid cells elevation: true triangulation: true;
		}
	}
}
