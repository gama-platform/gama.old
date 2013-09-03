model Perfect_Gas


/**
 *  Perfect_Gas
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: Ball are evolving in a cube and the gravity is null
 *
 */
 
 
global {
	
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:200  ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 10;
	float size_of_the_wall <- 10.0;
	int offset<-10;

	Physical3DWorld world2;
	init {
	    
	    
		create ball number: 1000{
			set radius <-2;
			set location <-  {offset + rnd(width_of_environment-offset*2), offset + rnd(height_of_environment- offset*2),offset + rnd(width_of_environment-offset*2)};
			set mass <-0.001;
			set collisionBound <-  ["shape"::"sphere","radius"::radius];
		}
		
		create floor 
		{   
			set location <- {width_of_environment/2,height_of_environment/2,0};
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 , "y":: height_of_environment/2, "z"::size_of_the_wall];
			set shape <- rectangle({width_of_environment,height_of_environment});
			set mass <-0.0;
		}
		
		//Sky
		create floor{
			set location <- {width_of_environment/2,height_of_environment/2,width_of_environment};
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 , "y":: height_of_environment/2, "z"::size_of_the_wall];
			set shape <- rectangle({width_of_environment,height_of_environment});
			set mass <-0.0;
		}

		//down wall
		create wall{
			set location <- {width_of_environment/2,height_of_environment,0};
			set height <- width_of_environment;
			set shape <- rectangle({width_of_environment,2});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: size_of_the_wall, "z"::height];
			set mass <-0.0;
		}
		//upper wall
		create wall{
			set location <- {width_of_environment/2,0,0};
			set height <- width_of_environment;
			set shape <- rectangle({width_of_environment,2});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: size_of_the_wall, "z"::height];
			set mass <-0.0;
		}
		//left wall
		create wall{
			set location <- {0,height_of_environment/2,0};
			set height <- width_of_environment;
			set shape <- rectangle({2,height_of_environment});
			set collisionBound <-  ["shape"::"floor","x"::size_of_the_wall, "y":: height_of_environment/2, "z"::height];
			set mass <-0.0;			
		}
		//right wall
		create wall{
			set location <- {width_of_environment,height_of_environment/2,0};
			set height <- width_of_environment;
			set shape <- rectangle({2,height_of_environment});
			set collisionBound <-  ["shape"::"floor","x"::size_of_the_wall, "y":: height_of_environment/2, "z"::height];
			set mass <-0.0;
			
		}
		
		
		create Physical3DWorld {
			set gravity <- false;
		}
		set world2 <- first(Physical3DWorld as list);
		ask world2 {set registeredAgents <-  (ball as list) + (floor as list) + (wall as list);}
		
	
		
	}
		reflex computeForces  {
			ask world2 {do computeForces timeStep : 0.1;}
		} 
			
} 

environment width: width_of_environment height: height_of_environment; 

entities {
 
    species floor skills: [physical3D]{
    	
    	aspect default {
			draw shape color: rgb("black") empty:true;
		}
    }
    species wall skills: [physical3D]{
    	rgb color;
    	float height;
    	aspect default {
			draw shape color: rgb("black") depth:height empty:true;
		}
    }
 	
	species ball skills: [physical3D] {  
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
}
experiment perfect_gas type: gui {
output {
	display Cube type:opengl ambient_light:100 background:rgb(20,79,127) draw_env:false{
		species floor transparency:0.5;
		species wall transparency:0.5;
	    species ball aspect:sphere;			
	}
}
}

