/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_Recieve

global skills:[network]{	
	init
	{   do connect to:"localhost" with_name:"reciever";
		
	}
	reflex recieveAgent when:has_received_message(){
		write "fetch agent on the network";
		map mess <- fetch_message();
		write name + " fecth this message: " + mess;	
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
