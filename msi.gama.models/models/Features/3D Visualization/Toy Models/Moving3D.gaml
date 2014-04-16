/**
 *  Moving3D: This model illustrate the different action offered by the movingSkill3D (move,wander,goto and follow)
 *  Author: Arnaud Grignard - Tri Nguyen Huu
 */


model Moving3DModel   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 100 category: 'Initialization'; 
	int envSize <-100 max:100;
	int mazeSize <-10;
	int radius parameter: 'Radius' min: 1 <- 1 ;
	string movingType <-"move";
	graph mazeGraph;
	

	init { 
		if(movingType = "move"){
		  create movingAgent number: number_of_agents{
		    location <- {rnd(envSize), rnd(envSize), rnd(envSize)};	
		  }	
		}
		
		if(movingType = "wander"){
			create wanderAgent number: number_of_agents{
			  location <- {rnd(envSize), rnd(envSize), rnd(envSize)};	
			}
		}
		
		if(movingType = "goto"){
			create gotoAgent number: number_of_agents{
			  location <- {rnd(envSize), rnd(envSize), rnd(envSize)};
			  myTarget <-{rnd(envSize),rnd(envSize),rnd(envSize)};	
			}
		}
		
		if(movingType = "gotoOnNetwork"){
			
			loop i from:0 to:mazeSize{
		      loop j from:0 to: mazeSize{
			    loop k from:0 to:mazeSize{
			      create cell{
				    location <-{(i)*envSize/mazeSize,(j)*envSize/mazeSize, (k)*envSize/mazeSize};
			      }	
			    }	
	          }
		    }
		    create gotoAgent number: number_of_agents{
			  location <- {floor(rnd(envSize)/mazeSize)*10, floor(rnd(envSize)/mazeSize)*10, floor(rnd(envSize)/mazeSize)*10};
			  myTarget <-{0,0,0};
			  speed <-0.1;	
			}
		    mazeGraph <- as_distance_graph(cell, ["distance"::10.0,"species"::edge_agent]);
		}
	 }
} 

species movingAgent skills: [moving3D] { 
	reflex move{
	  do move3D;
	}
	aspect default {
	  draw sphere(radius) color:°yellow;	 	
    }	
}

species wanderAgent skills: [moving3D] { 
	reflex wander{
	  do wander3D amplitude:20;
	}
	aspect default {
	  draw sphere(radius) color:°green;	 	
    }	
}

species gotoAgent skills: [moving3D] { 
	point myTarget;
	reflex goto{
	  if(movingType = "goto"){
	  	do goto target:myTarget;
	  }
	  if(movingType = "gotoOnNetwork"){
	  	do goto target:myTarget on: mazeGraph;
	  }
	  
	}
	aspect default {
	  draw sphere(radius) color:°green;	 	
    }	
}

species followAgent skills: [moving3D] { 
	point myTarget;
	reflex goto{
	  if(movingType = "goto"){
	  	do goto target:myTarget;
	  }
	  if(movingType = "gotoOnNetwork"){
	  	do goto target:myTarget on: mazeGraph;
	  }
	  
	}
	aspect default {
	  draw sphere(radius) color:°green;	 	
    }	
}
    
species cell schedules:[]{
	
	aspect myPoint{
		draw sphere(0.01 * envSize/mazeSize) color:°yellow border:°yellow at:location ;
	}
	aspect myCube {
		draw cube(envSize/mazeSize) color:°yellow border:°yellow at:location empty:true;
	}	
}

species edge_agent schedules:[]{
	aspect base {
		draw shape color: °orange;
	}
}
	

experiment Moving  type: gui {
	parameter var:movingType <- "move";
	output {	
		display MovingAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55){
			species movingAgent;
		}
	}
}

experiment Wandering  type: gui {
	parameter var:movingType <- "wander";
	output {	
		display WanderingAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55){
			species wanderAgent;
		}
	}
}

experiment Goto  type: gui {
	parameter var:movingType <- "goto";
	output {	
		display GotoAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55){
			species gotoAgent;
		}
	}
}

experiment GotoOnNetwork  type: gui {
	parameter var:movingType <- "gotoOnNetwork";
	output {	
		display GotoOnNetworkAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55){
			species gotoAgent;
			species edge_agent refresh:false;
		}
	}
}






