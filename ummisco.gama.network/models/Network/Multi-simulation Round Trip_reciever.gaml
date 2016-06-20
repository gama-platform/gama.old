/**
* Name: Multi Simulation Network Round Trip
* Author: Arnaud Grignard
* Description: Model to show how to use network skillls in a multi-simulation
* Tags: multi_simulation, network
*/

model multi_simulation_Network_Round_Trip

global skills:[network]{
	init {
		  do connect to:"localhost" with_name:"reciever";
	}
	
	reflex updateSimulation when:has_received_message(){
		map mess <- fetch_message();
	}
	
	action teletransportation (NetworkingAgent a, string s){
	  do send to:s content:a;
	}
}



species NetworkingAgent skills:[moving]{
   rgb color;
   reflex updateState when:every(10){
   			write "teleportation from reciever to sender";
   			ask world{
	          do teletransportation(myself,"sender");	
	        } 
	        do die;		
   	}
   aspect base{
   	draw shape color:color;
   }	
}

species NormalAgent{
   rgb color;
   aspect base{
   	draw square(10) color:color;
   }	
}

experiment main type: gui {
	//definition of a minimal duration for each cycle. As the model is very simple, it can run too fast to observe the results, so we slow it down.
	float minimum_cycle_duration <- 0.1;
	
	output {
		display map type:opengl {
			species NetworkingAgent aspect:base;
			species NormalAgent aspect:base;
		}
	}
}
