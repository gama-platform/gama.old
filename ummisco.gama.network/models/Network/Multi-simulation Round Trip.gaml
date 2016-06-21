/**
* Name: Multi Simulation Network Round Trip
* Author: Arnaud Grignard
* Description: Model to show how to use network skillls in a multi-simulation
* Tags: multi_simulation, network
*/

model multi_simulation_Network_Round_Trip

global skills:[network]{
	string simulationName <-"sender";
	init {
		if(simulationName = "sender"){
		  do connect to:"localhost" with_name:"sender";
		  create NetworkingAgent number:1 returns: networkAgt{ 	
		    color <- #black;	
			shape <-cube(5);
		  }
			//write "creation: "	 + serializeAgent(first(networkAgt));
			//write "creation: "	 + serialize(first(networkAgt).shape);
		  
		  /*create NormalAgent{
		  	color <- #blue;	
			shape <-cube(5);
		  }*/
		}
		if(simulationName = "reciever"){
		  do connect to:"localhost" with_name:"reciever";
		}
	}
	
	reflex updateSimulation when:has_more_message(){
		message mess <- fetch_message();
		write simulationName + ": "+ mess.contents ;
	}
	
	action teletransportation (NetworkingAgent a, string s){
		
	  write "teleport";//	 + serializeAgent(a);	
	  do send to:s contents:a;
	 // write serialize(a);
	}
}



species NetworkingAgent skills:[moving]{
   rgb color;
   reflex updateState when:every(10){
   	if(simulationName = "sender"){
   			write "teleportation from sender to reciever";
   			ask world{
	          do teletransportation(myself,"reciever");	
	        } 
	        do die;	
   	}
   	else{
   			write "teleportation from reciever to sender";
   			ask world{
	          do teletransportation(myself,"sender");	
	        } 
	        do die;		
   	}
   }
   
   reflex up{
   	//create NormalAgent number:1;
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
	
	//we define a init block to create new simulations
	init {
		//we create a second simulation (the first simulation is always created by default) with the given parameters
		create simulation with: [simulationName::"reciever"];
	}
	output {
		display map type:opengl {
			species NetworkingAgent aspect:base;
			species NormalAgent aspect:base;
		}
	}
}
