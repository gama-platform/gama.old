/**
* Name: Multi Simulation Network
* Author: Arnaud Grignard
* Description: Model to show how to use network skillls in a multi-simulation
* Tags: multi_simulation, network
*/

model multi_simulation_Network

global skills:[network]{
	string simulationName <-"sender";

	init {
		if(simulationName = "sender"){
		  do connect to:"localhost" with_name:"sender";
		  create NetworkingAgent number:10{	
		    color <- rnd_color(255);	
			shape <-circle(5);	
		  }
		}
		if(simulationName = "reciever"){
		  do connect to:"localhost" with_name:"reciever";
		}
	}
	
	reflex updateSimulation{
		if(simulationName = "sender"){
		  do send to:"reciever" contents:9 among NetworkingAgent;	
		}
		if(simulationName = "reciever"){
		  if(has_more_message()){
		      message mess <- fetch_message();
		   }	
		}
	}
}


species NetworkingAgent skills:[moving]{
   rgb color;
   reflex update{
     do wander;
   }	
   aspect base{
   	draw shape color:color;
   }	
}

experiment main type: gui {
	//definition of a minimal duration for each cycle. As the model is very simple, it can run too fast to observe the results, so we slow it down.
	float minimum_cycle_duration <- 0.1;
	
	//we define a init block to create new simulations
	init {
		//we create a second simulation (the first simulation is always created by default) with the given parameters
		create simulation with: [simulationName::"reciever"];
		
	}
	output {
		display map type:opengl{
			species NetworkingAgent;
		}
	}
}
