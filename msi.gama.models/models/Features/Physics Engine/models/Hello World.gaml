model FallingHelloWorld

/**
 *  FallingHelloWorld
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: This is a hello world model that introduces the physics engine.
 *  This model define two species floor and ball with the "physical3D" skills
 *  that simply floor on a floor when starting the simulation. 
 * 
 *  Floor is a simple rectangle with a null mass
 *  Ball is a sphere with a given mass
 * 
 *
 */

global {
	int environment_size <- 500; 
 
	 
	int number_of_ball parameter: 'Number of ball' min:1 <- 100  category: 'Model'; 
	int ball_radius parameter: 'Ball radius' min:1 <- 25  category: 'Model'; 
	
	file imageRaster <- file('./../images/wood-floor.jpg') ;
	geometry shape <- square(environment_size);
	
	Physical3DWorld world2;
	init {
		create ball number: number_of_ball{
			location <-  {rnd(environment_size),rnd(environment_size),rnd(environment_size)};
            radius <-float(rnd(ball_radius)+1);
			collisionBound <-  ["shape"::"sphere","radius"::radius];
			mass <-1.0;
		}
		
		create ground {
			location <- {environment_size/2,environment_size/2,0};
			collisionBound <-  ["shape"::"floor","x"::environment_size/2, "y":: environment_size/2, "z"::0];
			mass <-0.0;
		}

		
		create Physical3DWorld{
		  world2 <- first(Physical3DWorld as list);
		  ask world2 {registeredAgents <-  (ball as list) + (ground as list);}	
		  world2.gravity <- true;
		}
	}
	
	reflex computeForces  {
	  ask world2 {do computeForces timeStep : 0.0005;}
	} 			
} 

species ground skills: [physical3D]{    	
	aspect image{
		draw imageRaster size: environment_size;
	}
}
 
species ball skills: [physical3D] {  
	rgb color <- rgb (217,229,143); 
	float radius;

	aspect sphere{
		draw sphere(radius) color: color ;
	}	
}
experiment Falling_Hello_world type: gui {
	output {
		display Rain  type: opengl background:rgb(0,58,64) draw_env:false{
			species ground aspect:image;
		    species ball aspect:sphere;			
		}
	}
}

