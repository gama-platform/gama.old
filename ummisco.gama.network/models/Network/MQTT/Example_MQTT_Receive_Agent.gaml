/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_Receive

global skills:[network] {	
	init {   
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Send_Agent.gaml, too show how agents can send messages.";
		
		do connect to:"localhost" with_name:"receiver";
	}
	reflex recieveAgent when:has_more_message(){
		write "fetch agent on the network";
		message mess <- fetch_message();
		write name + " fecth this message: " + mess.contents;	
	}
}

species NetworkingAgent{
   rgb color;	
   aspect base{
   	draw shape color:color;
   }
	
}

experiment testdd type: gui {
	output {
		display view{
			species NetworkingAgent aspect:base;
		}
	}
}
