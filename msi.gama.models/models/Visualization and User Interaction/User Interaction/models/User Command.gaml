/**
* Name: User Command
* Author: Patrick Taillandier
* Description: Model which shows how to use the user commands in order to create agents in the display. The user has two possibilities : 
* 	create one agent, or create a number of agents, with the possibility to change their shape and a pink color. The user also has the possibility 
* 	to change the color and the shapes of the agents.
* Tags: gui
 */

model usercommand

global {
	//Number of agent to initialise
	int nbAgent <- 1;
	
	init {
		//Create the agent
		create cell number: nbAgent {
			color <-#green;
		}
	}
	
	//These commands are displayed in the world layer
	//User command to create an agent according to the location where the user right click
	user_command "Create an agent" {
   		create cell number: nbAgent with: [location::#user_location]  {
   			color <-#green;
   		} 
	}
	//User command to create a given number of agents according
	user_command "Create agents" {
		 map input_values <- user_input_dialog([enter("Number" , nbAgent), choose("shape", string, "circle", ["circle", "square"])]);
     	 create cell number: int(input_values at "Number") with: [color:: #pink, is_square:: string(input_values at "shape") = "square"];
	}
	
}

//Species that will be used
species cell {
	rgb color;	
	bool is_square <- false; 
	
	//These commands will be displayed in the cells layer, after right clicking on a agent
	user_command "change color"action: change_color;
	user_command "change shape" action: change_shape;
	
	//Action to change the color of the agent triggered by change color user command
	action change_color 
    {
     color <- color = #green ? #pink : #green;
    } 
	//Action to change the shape of the agent triggered by change shape user command
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
			species cell;
		}
	}
}