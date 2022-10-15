/**
* Name: Regression
* Author: Patrick Taillandier
* Description: A model which shows how to use the regression 
* Tags: regression, 3d, statistic
*/

model example_regression

global {
	//Regression variable that will store the function
	regression location_fct;
	float x_val <- 50.0;
	float y_val <- 50.0;
	
	float val <- -1.0;
	init {
		loop i from: 0 to: 18{
			if (i != 10) {
				create dummy with:[location::{i * 5 + 2 - rnd(4), i*5 + 2 - rnd(4), i*5 + 2 - rnd(4)}];	
			}
		}
	}
	
	//Reflex to compute the regression
	reflex do_regression {
		matrix<float> instances <- 0.0 as_matrix {3,length(dummy)};
		loop i from: 0 to: length(dummy) -1 {
			dummy ag <- dummy[i];
			instances[0,i] <- ag.location.x;
			instances[1,i] <- ag.location.y;
			instances[2,i] <- ag.location.z;
		}
		//Compute the function of regression
		location_fct  <- build(instances);
		write "learnt function: " + location_fct;
		
		//Predict the value using the function resulting before
		val <-  predict(location_fct, [x_val, y_val]);
		write "value : " + val;
		
		//T test
		list<float> actuals <- dummy collect (each.location.z);
		list<float> predictions;
		ask dummy { predictions <+ predict(location_fct, [location.x,location.y]); }
		float p_value <- t_test(actuals,predictions);
		write "p value : " + p_value;
	}
}

species dummy {
	aspect default {
		draw sphere(2) color: #blue;
	}
}

experiment main type: gui {
	parameter "Point to test, x value" var: x_val ;
	parameter "Point to test, y value" var: y_val ;
	output {
		display map type: 3d {
			species dummy;
			graphics "new Point " {
				if (location_fct != nil) {
					draw sphere(2) color: #red at: {x_val,y_val,val};
					
					//Draw the function as a line
					draw line([{100,100,predict(location_fct, [100,100])},{-10,-10,predict(location_fct, [-10,-10])}]) color: #black;
				}
				
			}
		}
	}
}
