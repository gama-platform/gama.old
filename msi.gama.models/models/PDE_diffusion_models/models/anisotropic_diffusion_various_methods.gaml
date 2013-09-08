/**
 *  diffusion
 *  Author: bgaudou
 *  Description: 
 */

model diffusion

global {
	int taille <- 51;
  	geometry shape <- envelope(square(taille) * 10);
  	
  	matrix<float> math_diff <- matrix([
									[2/9,2/9,1/9],
									[2/9,1/9,0.0],
									[1/9,0.0,0.0]]);

	reflex new_Value {
		ask(cells_dot where ((each.grid_x = int(taille/2)) and (each.grid_y = int(taille/2)))){
			phero <- 1.0;
		}
		ask(cells_convol where ((each.grid_x = int(taille/2)) and (each.grid_y = int(taille/2)))){
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
		
	 	aspect default {
	 		draw shape color: color depth: phero * 100;	 		
		} 
	} 
	
	grid cells_convol height: taille width: taille {
		float phero  <- 0.0;
		rgb color <- hsb(phero,1.0,1.0) update: hsb(phero,1.0,1.0);
		
	 	aspect default {
	 		draw shape color: color depth: phero * 100;	 		
		} 
	} 
}

experiment diffusion type: gui {
	output {
		display dot type: opengl {
			species cells_dot aspect: default;
		}
		display convol type: opengl {
			species cells_convol aspect: default;
		}
	}
}
