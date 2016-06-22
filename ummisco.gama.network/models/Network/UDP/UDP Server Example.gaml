/**
* Name: Socket_UDP_HelloWorld_Server
* Author: Arnaud Grignard & HUYNH Quang-Nghi
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, Socket, UDP
*/

model SocketUDP_HelloWorld_Server

global {	

	init
	{
		create NetworkingAgent number:1{
		   do connect to:"localhost" protocol:"udp_server" port:9876 with_name:"Server";
		}
		
	} 
}

species NetworkingAgent skills:[network]{
	string name;
	string dest;
	string would_like_to_send<-"";
	reflex fetch //when:has_received_message()
	{	
		if (length(mailbox) > 0)
		{
			write mailbox;
		}

//		write name + " fecth this message: " + mess;	
	}
	reflex send
	{
		
		loop id over: network_groups
		{
			do send to: id contents: "I am Server " + name + " I give order to " + id;
		}
	}
}

experiment Server_testdd type: gui {
	output {
	}
}
