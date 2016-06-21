/**
* Name: Multi Simulation Network
* Author: Arnaud Grignard
* Description: Model to show how to use network skillls in a multi-simulation
* Tags: multi_simulation, network
*/

model multi_simulation_Network

global skills:[network]{
	string simulationName <-"sender";
	point targetleft <-{0,50};
	point targetright <-{100,50};
	init {
		if(simulationName = "sender"){
		  do connect to:"localhost" with_name:"sender";
		  create NetworkingAgent number:1{	
		    location <-targetleft ;
		    target_loc <- targetright;
		    color <- #black;	
			shape <-circle(5);
			senderSim<-true;
			goforward<-true;
			is_arrived<-false;
		  }
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
	  do send to:s contents:a;
	}
}

species NetworkingAgent skills:[moving]{
   point target_loc;
   rgb color;
   bool senderSim;
   bool goforward;
   bool is_arrived;
   
   reflex updateState{
   	write "senderSim" + senderSim;
   	if(senderSim){
   		if(location = targetright and goforward=true){
   			write "teleportation from sender to reciver";
   			location <- targetleft;
   			target_loc<-targetright;
   			senderSim<-false;
   			goforward<-true;
   			ask world{
	          do teletransportation(myself,"reciever");	
	        } 
	        do die;
   		}
   	}
   	else{
   	    if(location = targetright){
   			target_loc<-targetleft;
   			goforward<-false;
   		}
   		if(location = targetleft and goforward =false){
   			write "teleportation from reciever to sender";
   			location <- targetright;
   			target_loc<-targetleft;
   			senderSim<-true;
   			goforward<-false;
   			ask world{
	          do teletransportation(myself,"sender");	
	        } 
	        do die;
   		}
   			
   	}
   }
      
   reflex update{
     do goto target:target_loc speed:10;
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
		display map type:opengl {
			species NetworkingAgent;
		}
	}
}
