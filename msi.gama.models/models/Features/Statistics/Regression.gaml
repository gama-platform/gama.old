/**
 *  regression
 *  Author: Patrick Taillandier
 *  Description: shows how to use the regression feature of GAMA
 */

model example_regression

global {
	regression location_fct;
	point pt_test <- {50,50};
	float val <- -1.0;
	init {
		loop i from: 0 to: 18{
			if (i != 10) {
				create dummy with:[location::{i * 5 + 2 - rnd(4), i*5 + 2 - rnd(4), i*5 + 2 - rnd(4)}];	
			}
		}
	}
	reflex do_regression {
		matrix<float> instances <- 0.0 as_matrix {3,length(dummy)};
		loop i from: 0 to: length(dummy) -1 {
			dummy ag <- dummy[i];
			instances[1,i] <- ag.location.x;
			instances[2,i] <- ag.location.y;
			instances[0,i] <- ag.location.z;
		}
		location_fct  <- build(instances);
		write "learnt function: " + location_fct;
		
		val <-  predict(location_fct, [50,50]);
		write "value : " + val;
	}
}

species dummy {
	aspect default {
		draw sphere(2) color: #blue;
	}
}

experiment main type: gui {
	parameter "Point to test" var: pt_test ;
	output {
		display map type: opengl {
			species dummy;
			graphics "new Point " {
				if (location_fct != nil) {
					draw sphere(2) color: #red at: pt_test + {0,0,val};
				}
				
			}
		}
	}
}
