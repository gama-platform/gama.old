/**
* Name: Circle
* Author: 
* Description: This model shows the movement of cells trying to do a circle shape with themselves 
* 	considering the other cells. The second experiment shows a bigger circle using more cell agents.
* Tags: skill
*/
model circle_model

global { 
	//Number of agents to create
	int number_of_agents min: 1 <- 50 ;
	//Radius of the circle that the cells will make
	int radius_of_circle min: 10 <- 1000 ;
	//Repulsion strength of one cell to the others
	int repulsion_strength min: 1 <- 5 ;
	//Size of the environment
	int width_and_height_of_environment min: 10 <- 3000 ; 
	//Range of the agents
	int range_of_agents min: 1 <- 25 ;
	//Speed of the agents
	float speed_of_agents min: 0.1  <- 2.0 ; 
	//Size of the agents
	int size_of_agents <- 100;
	//Center of the considered circle created by the cells
	point center const: true <- {width_and_height_of_environment/2,width_and_height_of_environment/2};
	geometry shape <- square(width_and_height_of_environment);
	init { 
		//Creation of the cell agents
		create cell number: number_of_agents;
	}  
}  
  
//Species cell which represents the cell agents, using the skill moving
species cell skills: [moving] {  
	//Color of the cell, randomly chosen
	rgb color const: true <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
	//Size of the cell
	float size const: true <- float(size_of_agents);
	//Range of the cell
	float range const: true <- float(range_of_agents); 
	//Speed of the cell
	float speed const: true <- speed_of_agents;   
	//Heading of the cell, the direction it 'watches'
	float heading <- rnd(360.0);
	
	//Reflex to make the cell agent fo to the center, calling the derivated action move
	reflex go_to_center {
		heading <- (((self distance_to center) > radius_of_circle) ? self towards center : (self towards center) - 180);
		do move speed: speed; 
	}
	//Reflex to flee of the other cells agents, which will help to design the circle shape
	reflex flee_others {
		cell close <- one_of ( ( (self neighbors_at range) of_species cell) sort_by (self distance_to each) );
		if close != nil {
			heading <- (self towards close) - 180;
			float dist <- self distance_to close;
			do move speed: dist / repulsion_strength heading: heading;
		}
	}
	
	aspect default { 
		draw circle(size)  color: color;
	}
}


experiment main type: gui  {
	parameter "Size of Agents" var: size_of_agents <- 100;
	parameter 'Number of Agents' var: number_of_agents <- 300;
	parameter 'Radius of Circle' var: radius_of_circle min: 10 <- 15000;
	parameter 'Strength of Repulsion' var: repulsion_strength min: 1 <- 50;
	parameter 'Dimensions' var: width_and_height_of_environment  min: 10 <- 40000;
	parameter 'Range of Agents' var: range_of_agents min: 1 <- 250;
	parameter 'Speed of Agents' var: speed_of_agents min: 0.1 <- 100.0 ;

	output {
		display Circle  {
			species cell;
		}
	}
}

