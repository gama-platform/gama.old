model pool

global {
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:200  ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 10;
	Physical3DWorld world2;
	init {

		create ball number: 200{
			set location <-  {rnd(width_of_environment),rnd(height_of_environment)} add_z 100;
            set radius <-2;
			set collisionBound <-  ["shape"::"sphere","radius"::radius];
			set mass <-1.0;
		}
		
		create floor 
		{
			set location <- {width_of_environment/2,height_of_environment/2};
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: height_of_environment/2, "z"::0];
			set mass <-0.0;
		}

		
		create Physical3DWorld{
		  set world2 <- first(Physical3DWorld as list);
		  ask world2 {set registeredAgents <-  (ball as list) + (floor as list);}	
		}
		
		
	}
		reflex computeForces  {
			ask world2 {do computeForces timeStep : 0.05;}
		} 
			
} 

environment width: width_of_environment height: height_of_environment; 

entities {
 
    species floor skills: [physical3D]{    	
    	aspect default {
			draw geometry: rectangle({width_of_environment,height_of_environment}) color: rgb([10,114,63]);
		}
    }
 
	species ball skills: [physical3D] {  
		var color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		int size  <- size_of_agents;
		int range  <- range_of_agents; 
		float speed  <- speed_of_agents;  
		float radius;
		int heading <- rnd(359);

		//geometry shape <- circle (radius);// buffer(12);
		
		aspect default {
			draw shape: geometry color: color z:1;
		}
		
		aspect sphere{
			draw geometry: geometry (point(self.location)) color: rgb([24,38,176]) z:radius;
		}
		
	}
}
experiment Rain type: gui {
output {
	display Rain refresh_every: 1 type:opengl{
		species floor;
	    species ball aspect:sphere;			
	}
}
}

