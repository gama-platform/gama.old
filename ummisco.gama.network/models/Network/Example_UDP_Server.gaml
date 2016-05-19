/**
* Name: Socket_UDP_HelloWorld_Server
* Author: Arnaud Grignard & HUYNH Quang-Nghi
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, Socket, TCP
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
		map mess <- fetch_message();
		write ""+self+" "+mess;
		if(mess!=nil and length(mess.pairs)>0){			
			list<string> msgs<-mess.pairs[0].value;
			write msgs;
		}
//		write name + " fecth this message: " + mess;	
	}
	reflex send
	{
		
			would_like_to_send<- "Server "+ name+", at cycle" + cycle + " sent to client:" + dest;
			
			do send_message to:"id" content:would_like_to_send;
	}
}

experiment Server_testdd type: gui {
	output {
	}
}
