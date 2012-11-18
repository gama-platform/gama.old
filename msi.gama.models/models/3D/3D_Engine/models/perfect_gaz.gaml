model pool

global {
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:200  ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 10;
	Physical3DWorld world2;
	init {
		/*create ball
		{ 
			set location <-  {101,50} add_z 200;
			set heading<-0;
			set speed<-1;
			set density <- 3.0;
			set velocity <- {0.0, 0.0};
			set collisionBound <-  ["shape"::"sphere","radius"::5];
		
		}
		create ball
		{
			set location<-{100,50} add_z 100;
			set heading<-90;
			set speed<-0;
			set density <- 3.0;
			set velocity <- {0.0, 0.0};
			set collisionBound <-  ["shape"::"sphere","radius"::5];
		}*/
		
		create ball number: 1000{
			set location <-  {rnd(width_of_environment),rnd(height_of_environment)} add_z 100;
			set heading<-0;
			set speed<-1;
			set density <- 3.0;
			set velocity <- {0.0, 0.0};
			set collisionBound <-  ["shape"::"sphere","radius"::5];
		}
		
		create floor 
		{
			set location <- {width_of_environment/2,height_of_environment/2};
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: height_of_environment/2, "z"::1];
			set mass <-0.0;
		}
		
		
	/* 	create billardbord
		{
			
			set velocity <- {0.0, 0.0};
			set density <- 10000.0;
			set shape <- rectangle({width_of_environment-1,1});
			set location <- {width_of_environment/2,height_of_environment/2};
			
		}
		
		create billardbord
		{ 
			set velocity <- {0.0, 0.0};
			set density <- 10000.0;
			set shape <- rectangle({width_of_environment-1,1});
			set location <- {width_of_environment/2,0};
			
		} 
		create billardbord
		{
			set velocity <- {0.0, 0.0};
			set density <- 10000.0;
			set shape <- rectangle({1,height_of_environment});
			set location <- {1,-height_of_environment};
			
		}
		create billardbord
		{
			set velocity <- {0.0, 0.0};
			set density <- 10000.0;
			set shape <- rectangle({1,height_of_environment});
			set location <- {width_of_environment-1,-height_of_environment};			
		}*/
		
		
		create Physical3DWorld;
		set world2 <- first(Physical3DWorld as list);
//		ask world2 {set registeredAgents <-  (billardbord as list);}
		ask world2 {set registeredAgents <-  (billardbord as list)+  (ball as list) + (floor as list);}
		
	}
		reflex computeForces  {
			ask world2 {do computeForces timeStep : 100;}
		} 
			
} 

environment width: width_of_environment*2 height: height_of_environment*2; 

entities {
 
    species floor skills: [physical3D]{
    	//geometry shape <- rectangle({width_of_environment,width_of_environment});
    	
    	aspect default {
			draw geometry: rectangle({width_of_environment,height_of_environment}) color: rgb('green');
		}
    }
 
	species billardbord  skills: [physical3D]{  
		geometry shape <- rectangle({1,1});
	}
	
	species ball skills: [physical3D] {  
		var color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		int size  <- size_of_agents;
		int range  <- range_of_agents; 
		float speed  <- speed_of_agents;  
		int heading <- rnd(359);

		geometry shape <- circle (10);// buffer(12);
		
		aspect default {
			draw shape: geometry color: color z:1;
		}
		
		aspect sphere{
			draw geometry: geometry (point(self.location)) color: rgb('blue') z:5;
		}
		
	}
}
experiment pool type: gui {
output {
	display Circle refresh_every: 1 type:opengl{
		species floor;
		species billardbord;
	    species ball aspect:sphere;			
	}
}
}

