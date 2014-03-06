/**
 *  diffusion
 *  Author: bgaudou
 *  Description: 
 */

model diffusion

global {
	int taille <- 51;
  	geometry shape <- envelope(square(taille) * 10);
  	cells selected_cells;
  	quick_cells selected_quick_cells;
  	matrix<float> math_diff <- matrix([
									[2/9,2/9,1/9],
									[2/9,1/9,0.0],
									[1/9,0.0,0.0]]);

	init {
		selected_cells <- location as cells;
		selected_quick_cells <- location as quick_cells;
	}
	reflex new_Value {
		ask(selected_cells){
			phero <- 1.0;
		}
		ask(selected_quick_cells){
			phero <- 1.0;
		}		
	}

	reflex diff {
		diffusion var: phero on: cells mat_diffu: math_diff;	
		diffusion var: phero on: quick_cells mat_diffu: math_diff cycle_length: 10;			
	}
}

entities {
	grid cells height: taille width: taille {
		float phero  <- 0.0;
		rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
		//float grid_value update: phero * 100;
	} 
	
	grid quick_cells height: taille width: taille {
		float phero  <- 0.0;
		rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
		//float grid_value update: phero * 100;
	} 
}

experiment diffusion type: gui {
	output {
		display a type: opengl {
			grid cells elevation: phero * 100 triangulation: true;
		}
		display quick type: opengl {
			grid quick_cells elevation: phero * 100 triangulation: true;
		}
	}
}
