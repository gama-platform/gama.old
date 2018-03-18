/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_Receive

global {	
	list<string> clients <-["send","receive"];
	init {
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Send.gaml, so that an agent can send messages.";
		
		create NetworkingAgent number:1{
		    name <-clients[1];
		    dest <- clients[0];
			do connect to:"localhost" with_name:name;
		}
	}
}

species NetworkingAgent skills:[network]{
	string name;
	string dest;
	reflex fetch when:has_more_message()
	{	
		message mess <- fetch_message();
		write name + " fecth this message: " + mess.contents;	
	}
}

experiment testdd type: gui {
	output {
	}
}
