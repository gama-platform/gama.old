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
		   do connect to:"localhost" protocol:"tcp_server" port:"3001" with_name:"Server";
		}
		
//		create NetworkingAgent number:1{
//			do connect to:"localhost" protocol:"tcp_client" port:"3001" with_name:name;
//		}

	} 
}

species NetworkingAgent skills:[network]{
	string name;
	string dest;
	reflex fetch //when:has_received_message()
	{	
		map mess <- fetch_message();
		write name + " fecth this message: " + mess;	
	}
	reflex send
	{
		do send_message to:dest content:"I am Server " + name + " I give order to " + dest;
	}
}

experiment Server_testdd type: gui {
	output {
	}
}
