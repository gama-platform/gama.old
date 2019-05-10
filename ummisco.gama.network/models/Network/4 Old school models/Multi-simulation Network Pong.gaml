/**
* Name: Multi Simulation Network
* Author: Arnaud Grignard
* Description: Model to show how to use network skillls in a multi-simulation
* Tags: multi_simulation, network
*/

model multi_simulation_Network

global skills:[network]{
	string RECEIVER <- "receiver" const: true;
	string SENDER <- "sender" const: true;
	
	string simulationName <- SENDER;
	point targetleft <- {0,50};
	point targetright <- {100,50};
	
	init {
		if(simulationName = SENDER){
		  	do connect to:"localhost" with_name:SENDER protocol:"tcp_client" port:3001;
		  	create NetworkingAgent number:1{	
		    	location <-targetleft ;
		    	target_loc <- targetright;
		    	color <- #black;	
				shape <-circle(5);
				senderSim<-true;
				goforward<-true;
//				is_arrived<-false;
		  	}
		}
		
		if(simulationName = RECEIVER){
		  	do connect to:"localhost" with_name: RECEIVER protocol:"tcp_server" port:3001;
		}
	}
	
	reflex updateSimulation when:has_more_message(){
		message mess <- fetch_message();
		write simulationName + ": "+ mess.contents ;
		write "Agent released.";
		release mess.contents as: NetworkingAgent in: world ;
	}
	
	action teletransportation (NetworkingAgent a, string s){
		write "Teletransportation of agent " + a + " to: " + s;
		//write experiment.simulation;
	  	do send to: s contents: a;
	//  	do send to: s contents: a;
	//  	write experiment.simulations;
	 // 	write (experiment.simulations accumulate (each.world.simulationName));
	}
}

species NetworkingAgent skills:[moving]{
   point target_loc;
   rgb color;
   bool senderSim;
   bool goforward;
//   bool is_arrived;
  
   reflex updateStateSender when: senderSim {
   		write "senderSim: " + senderSim;

		if((location with_precision (10) = targetright) and (goforward = true) ){
			write "teleportation from sender to receiver";
			location <- targetleft;
			target_loc<-targetright;
			senderSim<-false;
			// goforward<-true;
			ask world{
          		do teletransportation(myself,RECEIVER);	
        	} 
        	do die;
		} else if ((location with_precision (10) = targetleft) and (goforward = false)) {
			goforward <- false;
			target_loc <- targetright;			
		}
	}  
   
   reflex updateStateREceive when: not(senderSim) {
   		write "senderSim: " + senderSim;

    	if( (location with_precision (10) = targetright) and (goforward = true) ){
			target_loc<-targetleft;
			goforward<-false;
		} else if( (location with_precision (10) = targetleft) and (goforward = false)) {
			write "teleportation from receiver to sender";
			location <- targetright;
			target_loc<-targetleft;
			senderSim<-true;
			// goforward<-false;
			ask world{
        		do teletransportation(myself,SENDER);	
        	} 
        	do die;				
   		}
	}
      
   	reflex update{
    	do goto target:target_loc speed:10.0;
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
		create simulation with: [simulationName::SENDER];
	}
	output {
		display map type:opengl {
			species NetworkingAgent;
		}
	}
}
