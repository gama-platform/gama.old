/**
 *  diffusion
 *  Author: bgaudou
 *  Description: 
 */

model diffusion

global {
	int taille <- 51;
  	geometry shape <- envelope(square(taille) * 10);
  	cells_dot selected_cells_dot;
  	cells_convol selected_cells_convol;
  	matrix<float> math_diff <- matrix([
									[2/9,2/9,1/9],
									[2/9,1/9,0.0],
									[1/9,0.0,0.0]]);

	init {
		selected_cells_dot <- location as cells_dot;
  		selected_cells_convol <- location as cells_convol;
	}
	reflex new_Value {
		ask(selected_cells_dot){
			phero <- 1.0;
		}
		ask(selected_cells_convol){
			phero <- 1.0;
		}		
	}

	reflex diff {
		diffusion var: phero on: cells_dot mat_diffu: math_diff method: "dot_product";	
		diffusion var: phero on: cells_convol mat_diffu: math_diff method: "convolution";			
	}
}

entities {
	grid cells_dot height: taille width: taille {
		float phero  <- 0.0;
		rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
		float grid_value update: phero * 100;
	} 
	
	grid cells_convol height: taille width: taille {
		float phero  <- 0.0;
		rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
		float grid_value update: phero * 100;
	} 
}

experiment diffusion type: gui {
	output {
		display dot type: opengl {
			grid cells_dot elevation: true triangulation: true;
		}
		display convol type: opengl {
			grid cells_convol elevation: true triangulation: true;
		}
	}
}
