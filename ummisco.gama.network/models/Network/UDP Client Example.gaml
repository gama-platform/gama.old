/**
* Name: Socket_UDP_HelloWorld_Client
* Author: Arnaud Grignard & HUYNH Quang-Nghi
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, Socket, UDP
*/

model SocketUDP_HelloWorld_Client

global {	

	init
	{
		create NetworkingAgent number:1{
		   do connect to:"localhost" protocol:"udp_client" port:9876 with_name:"Client`";
		}
		
	} 
}

species NetworkingAgent skills:[network]{
	string name;
	string dest;
	string would_like_to_send<-"";
	reflex receive
	{
		if (length(mailbox) > 0)
		{
			write mailbox;
		}

	}

	reflex send
	{
		write "sending message ";
		do send to: "send" contents: name + " " + cycle + " sent to server";
	}
}

experiment Client_testdd type: gui {
	output {
	}
}
