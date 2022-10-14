/***
* Name: Tolerance
* Author: A. Drogoul
* Description: Allows a live manipulation of the tolerance used in simplifications and buffer value, and demonstrates their impact visually
***/

model Tolerance

global {
	
	int tolerance <- 0;
	int buffer <- 0;
	file complex <- file("../gis/water_body.shp");
	geometry shape <- envelope(envelope(complex));
	
	init {
		create shapes from: complex + (circle(1000) translated_to {1000,1000});
	}
}


species shapes {
	reflex expand  {
		shape <- shape + 1;
	}
}

experiment "Simplify this ! " {
	
	parameter "Simplification tolerance" category: "Change the value and observe the visual result" var:tolerance min: 0 max: 800 step: 1 {
		do update_outputs;
	}
	
	parameter "Buffer value" category: "Change the value and observe the visual result" var:buffer min: -100 max: 100 step: 1  {
		do update_outputs;
	}
	
	user_command "Close the simulation"  category:"Change the value and observe the visual result" color: #red {
		do die;
	}
	
	
	
	output {
		layout #split consoles: false tray: false tabs: false controls: false editors: false toolbars: false navigator: false;
		display my_display type:3d axes: false { 
			species shapes {
				draw shape color: #red;
			}
			species shapes {
				draw simplification(shape, tolerance) + buffer color: #blue;
			}

		}
		
	}	
}
