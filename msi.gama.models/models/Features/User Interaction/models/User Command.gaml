/**
 *  usercommand
 *  Author: patrick Taillandier
 *  Description: show how to use user_command
 */

model usercommand

global {
	int nbAgent <- 1;
	
	init {
		create cell number: nbAgent {
			color <-°green;
		}
	}
	user_command "Create an agent" {
   		create cell number: nbAgent with: [location::user_location]  {
   			color <-°green;
   		} 
	}
	user_command "Create agents" {
		 map input_values <- user_input(["Number" :: nbAgent, "shape" :: "circle"]);
     	 create cell number : int(input_values at "Number") with: [color:: °pink, is_square:: string(input_values at "shape") = "square"];
	}
	
}


species cell {
	rgb color;	
	bool is_square <- false;
	user_command "change color"action: change_color;
	user_command "change shape" action: change_shape;
	
	action change_color 
    {
     color <- color = °green ? °pink : °green;
    } 
    action change_shape
    {
       is_square <- not (is_square);
    }
	aspect default {
		draw is_square ? square(2): circle(1) color: color;
	}
}


experiment Displays type: gui {
	output {
		display map { 
			species simulation.cell;
		}
	}
}