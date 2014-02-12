/**
 *  matdiffu
 *  Author: hqnghi
 *  Description: 
 */

model matdiffu 

global {
	int taille <- 51;
  	geometry shape <- envelope(square(taille) * 10);
  	cells selected_cells;
  	
  	matrix<float> math_diff <- matrix([
										[1/7, 0, 0, 0, 0],
										[1/7, 0, 0, 0, 0],
										[1/9,1.0,1.0,1.0,0.1],
										[1/7, 0, 0, 0, 0],
										[1/7, 0, 0, 0, 0]]);

	init {
		selected_cells <- location as cells;
	}
	reflex new_Value {
		ask selected_cells {
			phero <- 1.0;
		}
	}

	reflex diff {
		diffusion var: phero on: cells mat_diffu: math_diff;	
	}
}

entities {
	grid cells height: taille width: taille {
		float phero  <- 0.0;
		rgb color <- hsb(min([phero,1]),1.0,1.0) update: hsb(min([phero,1]),1.0,1.0);
		float grid_value update: min([100,phero * 100]);
	} 
}

experiment diffusion type: gui {
	output {
		display a type: opengl {
			grid cells elevation: true triangulation: true;
		}
	}
}
