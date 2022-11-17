/**
* Name: NewModel
* Moving Agent
* Author: Lucas GROSJEAN
* Tags: 
*/

model OLZ


global 
{		
	geometry shape <- rectangle(100,100);
	int gridWidth <- 2;
	int gridHeight <- 2;
	
	init
	{
		seed <- 12.0;
		create movingAgent number: 50;
	}
}


species movingAgent skills:[moving]
{	
	rgb col;
	point target;
	int random_number;
	
	init
	{
		location <- any_location_in(world);
		target <- any_location_in(world);
		col <- #black;
		speed <- 1.0;
		real_speed <- 1.2;
		random_number <- rnd(1000);
	}
	aspect classic
	{
		draw circle(0.5) color: col;
	}
	
	reflex move when: target != location
	{
		random_number <- rnd(1000);
		real_speed <- 1.2 + rnd(0.5,0.7);
		
		//write real_speed;
		do goto speed: real_speed target:target;
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

grid cell width: gridWidth height: gridHeight neighbors: 4
{ 
	int size_OLZ <- 10;
	
	int cell_index <- grid_y + (grid_x * gridHeight);
	
	geometry OLZ_top_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {0,(size_OLZ / 2),0};
	geometry OLZ_bottom_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {0,-(size_OLZ / 2),0};
	
	geometry OLZ_left_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {size_OLZ / 2,0,0};
	geometry OLZ_right_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {-(size_OLZ / 2),0,0};

	geometry OLZ_top_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {0,(size_OLZ / 2),0}) translated_by {0,-(size_OLZ / 2),0};
	geometry OLZ_bottom_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight ) translated_by {0,-(size_OLZ / 2),0}) translated_by {0,(size_OLZ / 2),0};
	
	geometry OLZ_left_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {size_OLZ / 2,0,0}) translated_by {-(size_OLZ / 2),0,0};
	geometry OLZ_right_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {-(size_OLZ / 2),0,0}) translated_by {(size_OLZ / 2),0,0};
	
	geometry inner_OLZ <- OLZ_top_inner + OLZ_bottom_inner + OLZ_left_inner + OLZ_right_inner;
	geometry outer_OLZ <- OLZ_top_outer + OLZ_bottom_outer + OLZ_left_outer + OLZ_right_outer;
	
	int rank;
	
	aspect olz
	{
		draw ""+cell_index color: #red;
		draw self.shape color:rgb(0,0,0,0.5) border:#black;	
		
		/*draw OLZ_left_inner color: rgb(#red, 127);
		draw OLZ_right_inner color: rgb(#blue, 127);
		draw OLZ_top_inner color: rgb(#green, 127);
		draw OLZ_bottom_inner color: rgb(#orange, 127);*/
		
		if(grid_x = 0 and grid_y = 1)
		{
			draw OLZ_left_outer color: rgb(#red, 127);
			draw OLZ_right_outer color: rgb(#blue, 127);
			draw OLZ_top_outer color: rgb(#green, 127);
			draw OLZ_bottom_outer color: rgb(#orange, 127);
		}
	}
	
	aspect decoupage
	{
		draw ""+cell_index color: #red;
		draw self.shape color:rgb(0,0,0,0.5) border:#black;	
	}
}


experiment movingExp type: gui 
{
	output{
		display "Modèle de base" type: opengl
		{
			species movingAgent aspect: classic;
		}
		display "Découpage du modèle" type: opengl
		{
			species cell aspect: decoupage;
			species movingAgent aspect: classic;
		}
		display "OLZ" type: opengl
		{
			species cell aspect: olz;
			species movingAgent aspect: classic;
		}
	}
}

experiment decoupage type: gui 
{
	output{
		display "Découpage du modèle" type: opengl
		{
			species cell aspect: decoupage;
			species movingAgent aspect: classic;
		}
	}
}

experiment displayOLZ type: gui 
{
	output{
		display "Découpage du modèle" type: opengl
		{
			species cell aspect: olz;
			species movingAgent aspect: classic;
		}
	}
}

