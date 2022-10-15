/**
* Name:  Movement in 3D
* Author:  Arnaud Grignard - Tri Nguyen Huu
* Description: This model illustrates the different action and effect offered by the movingSkill3D (move,wander,goto and follow)
* Tags: 3d, agent_movement, graph, skill
*/



model Moving3DModel   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 250 step:10 category: 'Initialization'; 
	int envSize <-100; //max:100;
	int mazeSize <-10;
	int radius parameter: 'Radius' min: 1 <- 1 ;
	int directionSize parameter: 'direction size' min: 1 <- 10 ;
	string effectType <-"blob";// among:["","firework", "blob","direction"];
	string movingType <-"move";
	graph mazeGraph;
	geometry shape <- cube(envSize);
	bool trace <-false;
	string agentAspect <- "sphere" among:["circle", "sphere","direction"];

	init { 
		
		if(movingType = "move" or movingType = "complete"){
		  create movingAgent number: number_of_agents{
		  	if(effectType = "firework"){
		  	  location <- {envSize/2, envSize/2, envSize/2};	
		  	}
		  	else{
		  		location <- {rnd(envSize), rnd(envSize), rnd(envSize)};
		  	}
		    color<-#red;	
		  }	
		}
		  
		if(movingType = "wander" or movingType = "complete"){
			create wanderAgent number: number_of_agents{
			  location <- {rnd(envSize), rnd(envSize), rnd(envSize)};
			  color<-#green;	
			}
		}
		
		if(movingType = "goto" or movingType = "complete"){
			create gotoAgent number: number_of_agents{
			  location <- {rnd(envSize), rnd(envSize), rnd(envSize)};
			  myTarget <-{rnd(envSize),rnd(envSize),rnd(envSize)};
			  color<-#yellow;
			}
		}
		
		if(movingType = "gotoOnNetwork" or movingType = "complete"){
			
			loop i from:0 to:mazeSize{
		      loop j from:0 to: mazeSize{
			    loop k from:0 to:mazeSize{
			      create cell{
				    location <-{(i)*envSize/mazeSize,(j)*envSize/mazeSize, (k)*envSize/mazeSize};
			      }	
			    }	
	          }
		    }
		    create gotoAgentOnNetwork number: number_of_agents{
			  location <- {floor(rnd(envSize)/mazeSize)*10, floor(rnd(envSize)/mazeSize)*10, floor(rnd(envSize)/mazeSize)*10};
			  myTarget <-{0,0,0};
			  speed <-0.1;
			  color<-#orange;		
			}
		    mazeGraph <- as_distance_graph(cell, 10.0,edge_agent);
	
		}
	 }
}

species abstractAgent skills: [moving3D]{
	rgb color;
	aspect default {
		if(agentAspect = "sphere"){
			draw sphere(radius) color:color at:{location.x,location.y,location.z-radius/2};
		}
		if(agentAspect = "circle"){
			draw circle(radius) color:color;
		}
		if(agentAspect = "direction"){
		  draw sphere(radius) color:color at:{location.x,location.y,location.z-radius/2};
          draw line([{location.x,location.y,location.z},{location.x+directionSize*cos(pitch)*cos(heading),location.y+directionSize*cos(pitch)*sin(heading),location.z+directionSize*sin(pitch)}]) end_arrow:1.0 color:color;	
		}  	 	
    }
} 

species movingAgent  parent:abstractAgent{ 
	reflex move{
	  do move;
	}		
}

species wanderAgent parent:abstractAgent{ 
	reflex wander{
	  do wander amplitude:90.0;
	}
}

species gotoAgent parent:abstractAgent{ 
	point myTarget;
	string gotoType;
	reflex goto{
		if(effectType = "blob"){
			myTarget<-{rnd(envSize),rnd(envSize),rnd(envSize)};
		}
		do goto target:myTarget;	  
	}
}

species gotoAgentOnNetwork parent:abstractAgent{ 
	point myTarget;
	string gotoType;
	reflex goto{
	  	do goto target:myTarget on: mazeGraph;
	}		
}


    
species cell schedules:[]{
	
	aspect myPoint{
		draw sphere(0.01 * envSize/mazeSize) color:rgb(255,255,255,0.5) at:location ;
	}
	
}

species edge_agent schedules:[]{
	aspect base2 {
		draw shape color: rgb(255,255,255);
	}
}
	

experiment Moving  type: gui {
	parameter "Trace" var:trace <- false;
	parameter "Movement" var:movingType <- "move";
	parameter "Agent Aspect" var:agentAspect <- "direction";
	parameter "Effect type" var:effectType <- "" among:["","firework"];
	output {	
		display MovingAgent type:3d  background:rgb(10,40,55) {
			species movingAgent trace:trace;
		}
	}
}

experiment Wandering  type: gui {
	parameter "Trace" var:trace <- false;
	parameter "Movement" var:movingType <- "wander";
	parameter "Agent Aspect" var:agentAspect <- "direction";
	output {	
		display WanderingAgent type:3d  background:rgb(10,40,55) {
			species wanderAgent trace:trace;
		}
	}
}

experiment Goto  type: gui {
	parameter "Trace" var:trace <- false;
	parameter "Movement" var:movingType <- "goto";
	parameter "Agent Aspect" var:agentAspect <- "direction";
	parameter "Effect Type" var:effectType <- "" among:["","blob"];
	output {	
		display GotoAgent type:3d  background:rgb(10,40,55) {
			species gotoAgent trace:trace;
		}
	}
}

experiment GotoOnNetwork  type: gui {
	parameter "Trace" var:trace <- false;
	parameter "Movement" var:movingType <- "gotoOnNetwork";
	parameter "Agent Aspect" var:agentAspect <- "direction";
	output {
			
		display GotoOnNetworkAgent type:3d background:rgb(10,40,55) {
			species gotoAgentOnNetwork trace:trace;
			species cell aspect:myPoint;
			species edge_agent aspect: base2 ;
		}
	}
}

experiment Complete  type: gui {
	parameter "Trace" var:trace <- false;
	parameter "Movement" var:movingType <- "complete";
	parameter "Agent Aspect" var:agentAspect <- "direction";
	output {
			
		display GotoOnNetworkAgent type:3d background:rgb(10,40,55) {
			species movingAgent position:{0,0,0} trace:trace;
			species wanderAgent position:{envSize,0,0} trace:trace;
			species gotoAgent position:{envSize*2,0,0} trace:trace;
			species gotoAgentOnNetwork position:{envSize*3,0,0} trace:trace;
			species cell aspect:myPoint position:{envSize*3,0,0};
			species edge_agent aspect: base2 position:{envSize*3,0,0};
		}
	}
}







