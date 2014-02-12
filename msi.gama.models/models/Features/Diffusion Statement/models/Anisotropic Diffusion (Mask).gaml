/**
 *  diffusion
 *  Author: bgaudou
 *  Description: 
 */

model diffusion

global {
	int grid_size <- 51;
  	geometry shape <- envelope(square(grid_size) * 10);
  	cells selected_cells;
  	matrix mymask <- file("../includes/mask.bmp") as_matrix({grid_size,grid_size});
  	matrix<float> math_diff <- matrix([
									[1/9,1/9,1/9],
									[1/9,1/7,1/9],
									[1/9,1/9,1/9]]);
	init {
		selected_cells <- location as cells;
	}
	reflex new_Value {
		ask selected_cells {
			phero <- 1.0;
		}
	}

	reflex diff {
		diffusion var: phero on: cells mat_diffu: math_diff mask: mymask;	
	}
}

entities {
	grid cells height: grid_size width: grid_size {
		float phero <- 0.0;
		rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
		float grid_value update: min([100, phero * 100]);
	} 
}

experiment diffusion type: gui {
	output {
		display a type: opengl {
			grid cells elevation: true triangulation: true;
		}
	}
}
