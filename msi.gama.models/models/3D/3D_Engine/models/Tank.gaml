model Tank

/**
 *  Tank
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: Ball are falling in a Tank with a floor and 4 walls.
 * 
 *  Floor is a simple rectangle with a null mass.
 *  Wall is a rectangle draw with a depth equal to its height with a null mass.
 *  Ball is a sphere with a given mass.
 * 
 *
 */

global {
	int width_of_environment parameter: 'Dimensions' init:100 ; 
	int height_of_environment parameter: 'Dimensions' init:100  ; 

    int nb_balls parameter: 'Number of Agents' min: 1  <- 500 ; 
	int size_of_agents parameter: 'Size of Agents' min: 1 <- 1;	
	int wall_height parameter: 'Wall height' min: 1  <- 25 ; 

	Physical3DWorld world2;
	init {
		create ball number: nb_balls{
			set location <-  {rnd(width_of_environment-size_of_agents),rnd(height_of_environment-size_of_agents),rnd(height_of_environment-size_of_agents)};
			set radius <- float(size_of_agents);
			set collisionBound <-  ["shape"::"sphere","radius"::radius];
		}
		create floor {
			set location <- {width_of_environment/2,height_of_environment/2,0};
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 , "y":: height_of_environment/2, "z"::0];
			set shape <- rectangle({width_of_environment,height_of_environment});
			set mass <-0.0;
		}
		//down wall
		create wall{
			set location <- {width_of_environment/2,height_of_environment,0};
			set shape <- rectangle({width_of_environment,2});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: 1, "z"::wall_height];
			set mass <-0.0;
		}
		//upper wall
		create wall{
			set location <- {width_of_environment/2,0,0};
			set shape <- rectangle({width_of_environment,2});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: 1, "z"::wall_height];
			set mass <-0.0;
		}
		//left wall
		create wall{
			set location <- {0,height_of_environment/2,0};
			set shape <- rectangle({2,height_of_environment});
			set collisionBound <-  ["shape"::"floor","x"::1, "y":: height_of_environment/2, "z"::wall_height];
			set mass <-0.0;			
		}
		//right wall
		create wall{
			set location <- {width_of_environment,height_of_environment/2,0};
			set shape <- rectangle({2,height_of_environment});
			set collisionBound <-  ["shape"::"floor","x"::1, "y":: height_of_environment/2, "z"::wall_height];
			set mass <-0.0;			
		}
		

		create Physical3DWorld;
		set world2 <- first(Physical3DWorld as list);
		ask world2 {set registeredAgents <-   (ball as list) + (floor as list) + (wall as list);}
		set world2.gravity <- true;
		
	}
		reflex computeForces  {
			ask world2 {do computeForces timeStep : 0.00100* nb_balls;}
		} 
			
} 

//environment width: width_of_environment height: height_of_environment; 

entities {
 
    species floor skills: [physical3D]{    	
    	aspect default {
			draw geometry: shape color: rgb(60,60,60);
		}
    }
    species wall skills: [physical3D]{
    	rgb color;
    	aspect default {
			draw geometry: shape color: rgb(40,40,40) depth:wall_height;
		}
    }
	species ball skills: [physical3D] {  
 		float radius;		
		aspect default{
			draw sphere(radius) color: rgb(4,158,189);
		}
	}
}
experiment tank type: gui {
	output {
		display Circle refresh_every: 1 type:opengl ambient_light:100 background:rgb(230,230,230){
			species floor;
			species wall;
	    	species ball;			    
		}
	}
}

