/**
 *  Moving3D: This model illustrates the different action and effect offered by the movingSkill3D (move,wander,goto and follow)
 *  Author: Arnaud Grignard - Tri Nguyen Huu
 */


model Moving3DModel   

global {
	
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 500 step:10 category: 'Initialization'; 
	int envSize <-100; //max:100;
	int mazeSize <-10;
	int radius parameter: 'Radius' min: 1 <- 1 ;
	int directionSize parameter: 'direction size' min: 1 <- 10 ;
	string effectType <-"";// among:["","firework", "blob","direction"];
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
		    color<-°red;	
		  }	
		}
		
		if(movingType = "wander" or movingType = "complete"){
			create wanderAgent number: number_of_agents{
			  location <- {rnd(envSize), rnd(envSize), rnd(envSize)};
			  color<-°green;	
			}
		}
		
		if(movingType = "goto" or movingType = "complete"){
			create gotoAgent number: number_of_agents{
			  location <- {rnd(envSize), rnd(envSize), rnd(envSize)};
			  myTarget <-{0,0,0};
			  color<-°yellow;
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
			  color<-°orange;		
			}
		    mazeGraph <- as_distance_graph(cell, ["distance"::10.0,"species"::edge_agent]);
		}
	 }
}

species abstractAgent skills: [moving3D]{
	rgb color;
	aspect default {
		if(agentAspect = "sphere"){
			draw sphere(radius) color:color;
		}
		if(agentAspect = "circle"){
			draw circle(radius) color:color;
		}
		if(agentAspect = "direction"){
		  draw sphere(radius) color:color;
          draw line([{location.x,location.y,location.z},{location.x+directionSize*cos(pitch)*cos(heading),location.y+directionSize*cos(pitch)*sin(heading),location.z+directionSize*sin(pitch)}]) end_arrow:1.0;	
		}  	 	
    }
    
    aspect directionSpeed{
      draw sphere(radius) color:color;
      draw line([{location.x,location.y,location.z},{location.x*speed,location.y,location.z}]);	
    }
} 

species movingAgent  parent:abstractAgent{ 
	reflex move{
	  do move;
	}		
}

species wanderAgent parent:abstractAgent{ 
	reflex wander{
	  do wander amplitude:90;
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

species followAgent parent:abstractAgent{ 
	point myTarget;
	string gotoType;
	reflex goto{
	  if(gotoType = "goto"){
	  	do goto target:myTarget;
	  }
	  if(gotoType = "gotoOnNetwork"){
	  	do goto target:myTarget on: mazeGraph;
	  }
	  
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
	parameter var:trace <- false;
	parameter var:movingType <- "move";
	parameter var:agentAspect <- "direction";
	parameter var:effectType <- "" among:["","firework"] category: 'Initialization';
	output {	
		display MovingAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55) trace:trace{
			species movingAgent;
		}
	}
}

experiment Wandering  type: gui {
	parameter var:trace <- false;
	parameter var:movingType <- "wander";
	parameter var:agentAspect <- "direction";
	output {	
		display WanderingAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55) trace:trace{
			species wanderAgent;
		}
	}
}

experiment Goto  type: gui {
	parameter var:trace <- false;
	parameter var:movingType <- "goto";
	parameter var:agentAspect <- "direction";
	parameter var:effectType <- "" among:["","blob"] category: 'Initialization';
	output {	
		display GotoAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55) trace:trace{
			species gotoAgent;
		}
	}
}

experiment GotoOnNetwork  type: gui {
	parameter var:trace <- false;
	parameter var:movingType <- "gotoOnNetwork";
	parameter var:agentAspect <- "direction";
	output {
			
		display GotoOnNetworkAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55) trace:trace{
			species gotoAgentOnNetwork;
			species cell aspect:myPoint;
			species edge_agent aspect: base2 ;
		}
	}
}

experiment Complete  type: gui {
	parameter var:trace <- false;
	parameter var:movingType <- "complete";
	parameter var:agentAspect <- "direction";
	output {
			
		display GotoOnNetworkAgent type:opengl  ambient_light:10 diffuse_light:100  background:rgb(10,40,55) trace:trace{
			species movingAgent position:{0,0,0};
			species wanderAgent position:{envSize,0,0};
			species gotoAgent position:{envSize*2,0,0};
			species gotoAgentOnNetwork position:{envSize*3,0,0};
			species cell aspect:myPoint position:{envSize*3,0,0};
			species edge_agent aspect: base2 position:{envSize*3,0,0};
		}
	}
}







