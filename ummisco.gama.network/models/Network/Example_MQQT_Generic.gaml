/**
* Name: MQQT_HelloWorld
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld

global {	
	list<string> clients <-["ping","pong"];
	init
	{
		create NetworkingAgent number:1{
			name <-clients[0];
			dest <- clients[1];
			do connect to:"localhost" with_name:name;
		}
		create NetworkingAgent number:1{
		    name <-clients[1];
		    dest <- clients[0];
			do connect to:"localhost" with_name:name;
			do send to:dest contents:"This message is sent by " + name + " to " + dest;
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
	reflex send
	{
		do send to:dest contents:"This message is sent by " + name + " to " + dest;
	}
}

experiment testdd type: gui {
	output {
	}
}
