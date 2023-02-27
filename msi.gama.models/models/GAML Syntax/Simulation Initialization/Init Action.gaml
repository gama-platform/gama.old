/**
* Name: InitAction
* Author: Alexis Drogoul
* Description:  This simple example shows how to use the _init_ callback action to build a simulation with some parameters, without declaring them as parameters
* Tags: GAML
*/

model InitAction

global {
	int agent_number <- 100;
	rgb agent_color <- #red;
	bool 2d <- false;
	
	init {
		create my_agents number: agent_number;
	}
}

species my_agents {
	
	int elevation <- rnd(30);
	
	
	aspect default {
		if (2d){
			draw square(5) color: agent_color;			
		}
		else {
			draw sphere(5) color: agent_color at:{location.x, location.y, elevation};			
		}
	}
	
}

experiment InitAction type: gui {
	
	action _init_ {
		map<string, unknown> params <- user_input_dialog([enter("Number of agents",100), enter("Color",#red), enter("2D",true)]);
		create InitAction_model with: [agent_number::int(params["Number of agents"]), agent_color::rgb(params["Color"]), 2d::bool(params["2D"])];
	}
	
	output {
		display Simple type:3d{
			species my_agents aspect:default;			 
		}
	}
	
}

