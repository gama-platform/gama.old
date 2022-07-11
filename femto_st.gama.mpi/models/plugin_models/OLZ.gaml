/**
* Name: NewModel
* Moving Agent
* Author: Lucas GROSJEAN
* Tags: 
*/


model OLZ

global 
{		
	geometry shape <- rectangle(50,50);
	int gridX <- 1;
	int gridY <- 4;
	
	init
	{
		seed <- 12.0;
		create movingAgent number: 5;
	}
}


species movingAgent skills:[moving]
{	
	rgb col;
	point target;
	
	init
	{
		location <- any_location_in(world);
		target <- any_location_in(world);
		col <- #black;
		speed <- 1.0;
		real_speed <- 1.2;
	}
	aspect classic
	{
		draw circle(0.5) color: col;
	}
	
	reflex move when: target != location
	{
		do goto target:target;
	}
	
	reflex target when: target = location
	{
		target <- any_location_in(world);
	}
}

species standingAgent
{	
	agent closest;
	aspect classic
	{
		draw circle(0.5) color: #red;
	}
}

species followingAgent parent: movingAgent
{
	movingAgent targetAgent;
	
	init
	{
		col <- #blue;
		real_speed <- 1.2;
		speed <- 1.2;
		targetAgent <- one_of(movingAgent);
		target <- targetAgent.location;
	}
	aspect classic
	{
		draw circle(0.5) color: col;
	}	
	
	/*reflex move when: target != location
	{
		target <- targetAgent.location;
		do goto target: target;
	}
	
	reflex target when: target = location
	{
		targetAgent <- one_of(movingAgent);
		target <- targetAgent.location;
	}	
	
	action reTarget
	{
		targetAgent <- one_of(movingAgent);
		target <- targetAgent.location;
	}*/
}

grid cell width: gridX height: gridY neighbors: 4
{ 
	int size_OLZ <- 5;
	
	geometry OLZ_top_inner <- shape - rectangle(world.shape.width / gridX, world.shape.height / gridY) translated_by {0,(size_OLZ / 2),0};
	geometry OLZ_bottom_inner <- shape - rectangle(world.shape.width / gridX, world.shape.height / gridY) translated_by {0,-(size_OLZ / 2),0};
	
	geometry OLZ_left_inner <- shape - rectangle(world.shape.width / gridX, world.shape.height / gridY) translated_by {size_OLZ / 2,0,0};
	geometry OLZ_right_inner <- shape - rectangle(world.shape.width / gridX, world.shape.height / gridY) translated_by {-(size_OLZ / 2),0,0};

	geometry OLZ_top_outer <- (shape - rectangle(world.shape.width / gridX, world.shape.height / gridY) translated_by {0,(size_OLZ / 2),0}) translated_by {0,-(size_OLZ / 2),0};
	geometry OLZ_bottom_outer <- (shape - rectangle(world.shape.width / gridX, world.shape.height / gridY ) translated_by {0,-(size_OLZ / 2),0}) translated_by {0,(size_OLZ / 2),0};
	
	geometry OLZ_left_outer <- (shape - rectangle(world.shape.width / gridX, world.shape.height / gridY) translated_by {size_OLZ / 2,0,0}) translated_by {-(size_OLZ / 2),0,0};
	geometry OLZ_right_outer <- (shape - rectangle(world.shape.width / gridX, world.shape.height / gridY) translated_by {-(size_OLZ / 2),0,0}) translated_by {(size_OLZ / 2),0,0};

	geometry outer_OLZ_area <- 5 around(shape);
	geometry inner_OLZ_area <- 5 around(shape - 5);

	int rank;
	
	aspect olz
	{
		draw self.shape color:rgb(0,0,0,0.5) border:#black;	
		//draw outer_OLZ_area color: #red;
		//draw inner_OLZ_area color: #green;
		
		/*draw OLZ_left_inner color: rgb(#red, 127);
		draw OLZ_right_inner color: rgb(#blue, 127);
		draw OLZ_top_inner color: rgb(#green, 127);
		draw OLZ_bottom_inner color: rgb(#orange, 127);*/
		
		if(grid_x = 0 and grid_y = 0)
		{
			draw OLZ_left_outer color: rgb(#red, 127);
			draw OLZ_right_outer color: rgb(#blue, 127);
			draw OLZ_top_outer color: rgb(#green, 127);
			draw OLZ_bottom_outer color: rgb(#orange, 127);
		}
	}
}


experiment movingExp type: gui 
{
	output{
		display "displayK" type: opengl
		{
			species cell aspect: olz;
			species movingAgent aspect: classic;
		}
	}
}

