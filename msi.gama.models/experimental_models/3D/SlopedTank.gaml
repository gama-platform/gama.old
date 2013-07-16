model tank

global {
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:200  ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 10;
	int nb_balls <- 100;
	Physical3DWorld world2;
	init {
		create ball number: nb_balls{
			set location <-  {rnd(width_of_environment),rnd(height_of_environment),(rnd(10) + 100)};
			set heading<-0;
			set speed<-1;
			set density <- 3.0;
			set velocity <- list([0.0, 0.0, 0.0]);
			set radius <- 2;
			set collisionBound <-  ["shape"::"sphere","radius"::radius];
		}
		create floor 
		{
			set location <- {width_of_environment/2,height_of_environment/2,0};
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 , "y":: height_of_environment/2, "z"::0];
			set shape <- rectangle({width_of_environment,height_of_environment});
			set mass <-0.0;
			loop i from: 0 to: length(shape.points) - 1{ 
				set shape <- shape add_z_pt {i,i = 0 ? -5.0: (i = 1 ? -5.0: (i = 2 ? 5.0: (i = 3 ? 5.0: 5.0)))};
			}


		}
		
		//down wall
		create wall{
			set location <- {width_of_environment/2,height_of_environment,0};
			set shape <- rectangle({width_of_environment,2});
			set _z <- 50;
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: 1, "z"::_z];
			set mass <-0.0;
			set color <- rgb('red');
		}
		//upper wall
		create wall{
			set location <- {width_of_environment/2,0,0};
			set shape <- rectangle({width_of_environment,2});
			set _z <- 50;
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: 1, "z"::_z];
			set mass <-0.0;
			set color <- rgb('red');
			
		}
		//left wall
		create wall{
			set location <- {0,height_of_environment/2,0};
			set shape <- rectangle({2,height_of_environment});
			set _z <- 50;
			set collisionBound <-  ["shape"::"floor","x"::1, "y":: height_of_environment/2, "z"::_z];
			set mass <-0.0;
			set color <- rgb('red');
			
		}
		//right wall
		create wall{
			set location <- {width_of_environment,height_of_environment/2,0};
			set _z <- 50;
			set shape <- rectangle({2,height_of_environment});
			set collisionBound <-  ["shape"::"floor","x"::1, "y":: height_of_environment/2, "z"::_z];
			set mass <-0.0;
			set color <- rgb('red');
			
		}
		
		
		
		create Physical3DWorld;
		set world2 <- first(Physical3DWorld as list);
		ask world2 {set registeredAgents <-   (ball as list) + (floor as list) + (wall as list) ;}
//		ask world2 {set registeredAgents <-   (floor as list) ;}
		
	}
		reflex computeForces  {
			ask world2 {do computeForces timeStep : 0.00100* nb_balls;}
		} 
			
} 

environment width: width_of_environment*2 height: height_of_environment*2; 

entities {
 
    species floor skills: [physical3D]{
    	aspect default {
			draw geometry: shape color: rgb('green');
		}
    }
    species wall skills: [physical3D]{
    	rgb color;
    	float _z;
    	aspect default {
			draw geometry: shape color: color depth:50;
		}
    }

	
	species ball skills: [physical3D] {  
		rgb color;
		int size  <- size_of_agents;
		int range  <- range_of_agents; 
		float speed  <- speed_of_agents;  
		int heading <- rnd(359);

		float radius;
		
		geometry shape <- circle (10);// buffer(12);
		
		aspect default {
			draw shape color: color depth:1;
		}
		
		aspect sphere{
			draw circle(radius) color: rgb('blue');
		}
		
	}
}
experiment tank type: gui {
output {
	display Circle refresh_every: 1 type:opengl{
		species floor;
		species wall;
	    species ball aspect:sphere;			
	    
	}
}
}

