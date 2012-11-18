model pool

global {
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:200  ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2 ; 
	int size_of_agents <- 10;
	PhysicalWorld world2;
	init {
		create ball
		{ 
			set location <- {30,53};
			set heading<-0;
			set speed<-1;
			set density <- 3.0;
			set velocity <- {5.0, 0.0};
		}
		create ball
		{
			set location<-{100,50};
			set heading<-90;
			set speed<-0;
			set density <- 3.0;
			set velocity <- {-1.0, 0.0};
			}
		create billardbord
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
		}
		
		
		create PhysicalWorld;
		set world2 <- first(PhysicalWorld as list);
//		ask world2 {set registeredAgents <-  (billardbord as list);}
		ask world2 {set registeredAgents <-  (billardbord as list)+  (ball as list);}
		
	}
		reflex computeForces {
			ask world2 {do computeForces;}
		} 
			
} 

environment width: width_of_environment*2 height: height_of_environment*2; 

entities {
//	species billardbord  skills: [physical]{  
	species billardbord  skills: [physical]{  
		geometry shape <- rectangle({1,1});
		
	}
	species ball skills: [physical] {  
		var color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		int size  <- size_of_agents;
		int range  <- range_of_agents; 
		float speed  <- speed_of_agents;  
		int heading <- rnd(359);
//		geometry shape <- rectangle({10, 10});//circle (10);// buffer(12);
		geometry shape <- circle (10);// buffer(12);
/** */
		init
		{
//			set location <- {rnd(width_of_environment),rnd(height_of_environment)};			
		}
		
		reflex go {
//			do move speed: speed; 
//			do update_physic physics_world: world2;
		}
		
		aspect default {
			draw shape: geometry color: color;
//			draw shape: circle size: 20 color: color;
		}
		
	}

	


}
experiment pool type: gui {
output {
	display Circle refresh_every: 1 {
		//species ball;
		species billardbord;
			species ball;			
	}
}
}

