/**
* Name: Balls without gravity
* Author: Arnaud Grignard
* Description: This is a model that shows how the physics engine work without gravity. Balls can collide each other and can't go further than the wall 
* and the ground agents.
* Tags: physics_engine, skill, 3d, spatial_computation
*/
model Perfect_Gas

 
 
global {
	//Bounds of the environment
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:200  ; 
	
	//Range, Speed ans Size of the agents
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 10;
	
	
	float size_of_the_wall <- 10.0;
	int offset<-10;
	geometry shape <- rectangle(width_of_environment, height_of_environment);

	//Physic engines that will compute the forces
	physic_world world2;
	init {
		create ball number: 1000{
			radius <-2;
			location <-  {offset+ rnd(width_of_environment-offset*2), offset+ rnd(height_of_environment- offset*2),offset+ rnd(width_of_environment-offset*2)};
			mass <-0.001;
			collisionBound <-  ["shape"::"sphere","radius"::radius];
		}
		
		create ground 
		{   
			location <- {width_of_environment/2,height_of_environment/2,0};
			collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 , "y":: height_of_environment/2, "z"::size_of_the_wall];
			shape <- rectangle(width_of_environment,height_of_environment);
			mass <-0.0;
		}
		
		//Sky
		create ground{
			location <- {width_of_environment/2,height_of_environment/2,width_of_environment};
			collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 , "y":: height_of_environment/2, "z"::size_of_the_wall];
			shape <- rectangle(width_of_environment,height_of_environment);
			mass <-0.0;
		}

		//down wall
		create wall{
			location <- {width_of_environment/2,height_of_environment,0};
			height <- float(width_of_environment);
			shape <- rectangle(width_of_environment,2);
			collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: size_of_the_wall, "z"::height];
			mass <-0.0;
		}
		//upper wall
		create wall{
			location <- {width_of_environment/2,0,0};
			height <- float(width_of_environment);
			shape <- rectangle(width_of_environment,2);
			collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: size_of_the_wall, "z"::height];
			mass <-0.0;
		}
		//left wall
		create wall{
			location <- {0.0,height_of_environment/2,0.0};
			height <- float(width_of_environment);
			shape <- rectangle(2,height_of_environment);
			collisionBound <-  ["shape"::"floor","x"::size_of_the_wall, "y":: height_of_environment/2, "z"::height];
			mass <-0.0;			
		}
		//right wall
		create wall{
			location <- {width_of_environment,height_of_environment/2,0};
			height <- float(width_of_environment);
			shape <- rectangle(2,height_of_environment);
			collisionBound <-  ["shape"::"floor","x"::size_of_the_wall, "y":: height_of_environment/2, "z"::height];
			mass <-0.0;
			
		}
		
		//Create the physic engine without gravity computed
		create physic_world {
			gravity <- false;
			world2 <- self;
		}
		
		//Add the agents to compute their forces
		ask world2 {agents <-  (ball as list) + (ground as list) + (wall as list);}
		
	}
	
	//Reflex to compute the forces at each step
	reflex computeForces  {
		ask world2 {do compute_forces step: 1;}
	} 
			
} 
//Species to represent the physic engine, derivated from the Physical3DWorld built-in species
species physic_world parent: physical_world ;
 
//Species to represent the ground using the physical3D skill
species ground skills: [physics]{
	aspect default {
		draw shape color: #black empty:true;
	}
}

//Species to represent the wall using the physical3D skill
species wall skills: [physics]{
	rgb color;
	float height;
    aspect default {
		draw shape color: #black depth:height empty:true;
	}
}
 	
//Species to represent the ball using the physical3D skill
species ball skills: [physics] {  
	rgb color;
	int radius;
	int size  <- size_of_agents;
	int range  <- range_of_agents; 
	float speed  <- speed_of_agents;  
	int heading <- rnd(359);

	aspect sphere{
		draw sphere(radius) color: rgb(135,201,255) ;
	}	
}

experiment perfect_gas type: gui {
	init{
		minimum_cycle_duration <-0.001;
	}
	output {
		display Cube type:opengl background:rgb(20,79,127) draw_env:false{
			species ground transparency:0.5;
			species wall transparency:0.5;
	    	species ball aspect:sphere;			
		}
	}
}

