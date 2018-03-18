/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_SendAgent

global skills:[network]{	
	init
	{   
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Receive_Agent.gaml, too show how agents receive messages.";
		
		do connect to:"localhost" with_name:"sender";
		create NetworkingAgent number:10{	
			color <- rnd_color(255);	
			shape <-sphere(5);	
		}
	}
	reflex sendAgent{
		write "send agent on the network";
		do send to:"receiver" contents:(9 among NetworkingAgent);	
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

experiment testdd type: gui {
	output {
		display view type:opengl{
			species NetworkingAgent aspect:base;
		}
	}
}
