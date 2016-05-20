/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_Send

global {	
	list<string> clients <-["send","recieve"];
	init
	{
		create NetworkingAgent number:1{
			name <-clients[0];
			dest <- clients[1];
			do connect to:"localhost" with_name:name;
		}
	}
}

species NetworkingAgent skills:[network]{
	string name;
	string dest;
	reflex send
	{
		do send_message to:dest content:"This message is sent by " + name + " to " + dest;
	}
}

experiment testdd type: gui {
	output {
	}
}
