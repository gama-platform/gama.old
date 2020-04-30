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
	
	init {
		create my_agents number: agent_number;
	}
}

species my_agents {
	aspect default {
		draw square(5) color: agent_color;
	}
}

experiment InitAction type: gui {
	
	action _init_ {
		map<string, unknown> params <- user_input([enter("Number of agents",100), enter("Color",#red), enter("2D",true)]);
		create InitAction_model with: [agent_number::int(params["Number of agents"]), agent_color::rgb(params["Color"])];
	}
	
	output {
		display Simple {
			species my_agents;
		}
	}
	
}

