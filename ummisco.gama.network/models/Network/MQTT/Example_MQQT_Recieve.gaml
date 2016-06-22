/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_Recieve

global {	
	list<string> clients <-["send","recieve"];
	init
	{
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
