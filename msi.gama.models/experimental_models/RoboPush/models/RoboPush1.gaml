model robopush

global {
	int width_of_environment parameter: 'Dimensions' init:300 ; 
	int height_of_environment parameter: 'Dimensions' init:300  ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2 ; 
	int size_of_agents <- 10;
	PhysicalWorld world2;
	init {
		create stone number:10
		{ 
			set location <- {sizea + rnd (width_of_environment - 2 * sizea),sizea + rnd (height_of_environment - 2 * sizea)};
			set heading<-0;
			set speed<-0;
			set density <- 3.0;
			set velocity <- {0.0, 0.0};
		}
		create robot number:10
		{ 
			set location <- {sizea + rnd (width_of_environment - 2 * sizea),sizea + rnd (height_of_environment - 2 * sizea)};
			set heading<-0;
			set speed<-5;
			set density <- 3.0;
			set velocity <- {5.0, 0.0};
		}
		create billardbord
		{
			
			set velocity <- {0.0, 0.0};
			set density <- 1000000.0; 
			set shape <- rectangle({width_of_environment,2});
			set location <- {width_of_environment/2,1};
			set color <- rgb('red');
			
		}
		
		create billardbord
		{ 
			set velocity <- {0.0, 0.0};
			set density <- 1000000.0;
			set shape <- rectangle({2,height_of_environment});
			set location <- {width_of_environment-1,height_of_environment/2};
			set color <- rgb('green');
			
		}
		create billardbord
		{
			set velocity <- {0.0, 0.0};
			set density <- 1000000.0;
			set shape <-  rectangle({width_of_environment,2});
			set location <- {width_of_environment/2,height_of_environment - 1};
			set color <- rgb('pink');
			
		}
		create billardbord
		{
			set velocity <- {0.0, 0.0};
			set density <- 1000000.0;
			set shape <- rectangle({2,height_of_environment});
			set location <- {1,height_of_environment/2};
			set color <- rgb('yellow');			
		}
		
		create PhysicalWorld;
		create targetArea;
		let templist <-  (billardbord as list)+  (robot as list);
		set world2 <- first(PhysicalWorld as list);
//		ask world2 {set registeredAgents <-  (billardbord as list);}
		ask world2 {set registeredAgents <-  (billardbord as list)+  (robot as list);}
		ask world2 {set registeredAgents <-  (templist as list)+  (stone as list);}
		
	}
		reflex {
			ask world2 {do computeForces;}
		} 
			
} 

environment width: width_of_environment*2 height: height_of_environment*2; 

entities {
	
	species targetArea  {  
		geometry shape <- square(100);
		point location <- {width_of_environment/2.0, height_of_environment/2.0};
		rgb color <- rgb('red');
		aspect default {
//			draw shape: geometry color: color;
			draw shape: geometry color: color fill: true;
		}
		
	}
//	species billardbord  skills: [physical]{  
	species billardbord  skills: [physical]{  
		geometry shape <- rectangle({1,1});
		rgb color <- rgb('black');
		aspect default {
//			draw shape: geometry color: color;
			draw shape: geometry color: color fill: true;
		}
		
	}
	species stone skills: [physical] {  
		var color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		int sizea type: float <- size_of_agents;
		int range type: float <- range_of_agents; 
		int speed type: float <- speed_of_agents;  
		int heading <- rnd(359);
//		geometry shape <- rectangle({10, 10});//circle (10);// buffer(12);
		geometry shape <- circle (sizea);// buffer(12);
/** */
		init
		{
			set sizea value:rnd(20);
//			set location <- {rnd(width_of_environment),rnd(height_of_environment)};			
		}
		
		reflex go {
//			do move speed: speed; 
//			do update_physic physics_world: world2;
		}
		
		aspect default {
//			draw shape: geometry color: color;
			draw shape: circle size: sizea color: color;
		}
		
	}
	species robot skills: [physical] {  
		var color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		int sizea type: float <- size_of_agents;
		int range type: float <- range_of_agents; 
		int speed type: float <- speed_of_agents;  
		int heading <- rnd(359);
//		geometry shape <- rectangle({10, 10});//circle (10);// buffer(12);
		geometry shape <- circle (sizea);// buffer(12);
		point previousLoc <- {0,0};
		int cpt <- 5 update: cpt - 1;
/** */
		init
		{
			set motor <- {5 - rnd(10), 5 - rnd(10)};
//			set location <- {rnd(width_of_environment),rnd(height_of_environment)};			
		}
		
		reflex go {
			if ((location.x with_precision 2 = previousLoc.x with_precision 2   ) and (location.y with_precision 2 = previousLoc.y with_precision 2   ) and cpt < 0){
				set motor <- {5 - rnd(10), 5 - rnd(10)};
				set cpt <- 5; 
			}
			set previousLoc <- location;
			
//			do move speed: speed; 
//			do update_physic physics_world: world2;
		}
		
		aspect default {
//			draw shape: geometry color: color;
			draw shape: circle size: sizea color: color;
		}
		
	}

	


}

output {
	display Circle refresh_every: 1 {
		//species ball;
		species targetArea transparency: 0.2;
		species billardbord;
			species stone;			
			species robot;			
	}
}
