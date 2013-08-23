model pool

global {
	int width_of_environment parameter: 'Dimensions' init:200 ; 
	int height_of_environment parameter: 'Dimensions' init:300  ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 10;
	rgb colorwood <- rgb([178,112,62]);
	my_world world2;
	init {
		//White ball
		create ball
		{
			set location<-{width_of_environment/2,4*height_of_environment/5, 5.0} ;
			set mass <- 3.0;
			//Set a random velocity between 500 and 1000
			set velocity <- list([0.0, - float(500 + rnd(500)), 0.0]);
			set collisionBound <-  ["shape"::"sphere","radius"::5];
		}
		
		
		let i <- 0;
		let deltaI <-0;
		let initX <- 75; 
		let initY <- height_of_environment/8;
		create ball number:15{
			
			set location<- {initX + (i - deltaI) * 10,initY, 5.0} ;
			set heading<-90;
			set speed<-0;
			set mass <- 3.0;
			set collisionBound <-  ["shape"::"sphere","radius"::5];
			set i <- i+1;
		
			if ((i mod 2) = 0){
				set color <- rgb ('red');
				
			}
			else{
				set color <- rgb ('yellow');
			}
			if(i=5){
				set initX <- initX + 5;
				set initY <- initY + 9; 
				set deltaI <- 5; 
			}
			if(i=9){
				set initX <- initX + 5;
				set initY <- initY + 9;
				set deltaI <- 9; 
				
			}
			if(i=12){
				set initX <- initX + 5;
				set initY <- initY + 9;
				set deltaI <- 12; 
				
			}
			if(i=14){
				set initX <- initX + 5;
				set initY <- initY + 9;
				set deltaI <- 14; 
				
			}
		}

		create floor 
		{
			set location <- {width_of_environment/2,height_of_environment/2,0};
			set shape <- rectangle({width_of_environment-24,height_of_environment - 24});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 -12, "y":: height_of_environment/2 -12, "z"::0];	
			set mass <-0.0;
		}
		create floor 
		{
			set location <- {width_of_environment/2,6,0};
			set shape <- rectangle({width_of_environment - 24,12});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 - 12, "y":: 6, "z"::0];
			set mass <-0.0;
		}
		create floor 
		{
			set location <- {width_of_environment/2,height_of_environment - 6,0};
			set shape <- rectangle({width_of_environment - 24,12});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2 - 12, "y":: 6, "z"::0];
			set mass <-0.0;
		}
		
		create floor 
		{
			set location <- {6,height_of_environment/4+3,0};
			set shape <- rectangle({12,height_of_environment/2 -18});
			set collisionBound <-  ["shape"::"floor","x"::6, "y":: height_of_environment/4-9, "z"::0];
			set mass <-0.0;
		}
		

		create floor 
		{
			set location <- {6,3*height_of_environment/4-3,0};
			set shape <- rectangle({12,height_of_environment/2 -18});
			set collisionBound <-  ["shape"::"floor","x"::6, "y":: height_of_environment/4-9, "z"::0];
			set mass <-0.0;
		}
		
		create floor 
		{
			set location <- {width_of_environment-6,height_of_environment/4+3,0};
			set shape <- rectangle({12,height_of_environment/2 -18});
			set collisionBound <-  ["shape"::"floor","x"::6, "y":: height_of_environment/4-9, "z"::0];
			set mass <-0.0;
		}
		
		create floor 
		{ 
			set location <- {width_of_environment-6,3*height_of_environment/4-3,0};
			set shape <- rectangle({12,height_of_environment/2 -18});
			set collisionBound <-  ["shape"::"floor","x"::6, "y":: height_of_environment/4-9, "z"::0];
			set mass <-0.0;
		}
		
		
		
		//down wall
		create wall{
			set location <- {width_of_environment/2,height_of_environment,0};
			set shape <- rectangle({width_of_environment,2});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: 1, "z"::10];
			set mass <-0.0;
			set color <- colorwood;
		}
		//upper wall
		create wall{
			set location <- {width_of_environment/2,0,0};
			set shape <- rectangle({width_of_environment,2});
			set collisionBound <-  ["shape"::"floor","x"::width_of_environment/2, "y":: 1, "z"::10];
			set mass <-0.0;
			set color <- colorwood;
			
		}
		//left wall
		create wall{
			set location <- {0,height_of_environment/2,0};
			set shape <- rectangle({2,height_of_environment});
			set collisionBound <-  ["shape"::"floor","x"::1, "y":: height_of_environment/2, "z"::10];
			set mass <-0.0;
			set color <- colorwood;
			
		}
		//right wall
		create wall{
			set location <- {width_of_environment,height_of_environment/2,0};
			set shape <- rectangle({2,height_of_environment});
			set collisionBound <-  ["shape"::"floor","x"::1, "y":: height_of_environment/2, "z"::10];
			set mass <-0.0;
			set color <- colorwood;
			
		}
				
		create my_world {
			set gravity <-true;
		}
		set world2 <- first(my_world as list);
		ask world2 {set registeredAgents <-  (ball as list) + (floor as list) + (wall as list);}
		
	}
		reflex computeForces  {
			ask world2 {do computeForces timeStep : 1;}
		} 
			
}  

environment width: width_of_environment height: height_of_environment; 

entities {
	
	species my_world parent: Physical3DWorld{}
 
    species floor skills: [physical3D]{
    	aspect default {
			draw shape color: rgb([10,114,63]) border:rgb([10,114,63]) ;
		}
    }
    species wall skills: [physical3D]{
    	rgb color;
    	aspect default {
			draw shape color: color depth:10;
		}
    }
 
	species ball skills: [physical3D] {  
		rgb color;
		int size  <- size_of_agents;
		int range  <- range_of_agents; 
		float speed  <- speed_of_agents;  
		int heading <- rnd(359);
		
		aspect default {
			draw circle (10) color: color depth:1;
		}
		
		aspect sphere{
			draw sphere(5); 
		}
		
	}
}
experiment pool type: gui {
output {
	display Circle refresh_every: 1 type:opengl tesselation:true ambient_light:100{
		//image name:'background' file:'../images/billard2.jpg' ;
		species floor aspect: default;
		species wall aspect: default;
	    species ball aspect:sphere;			
	    
	}
}
}

