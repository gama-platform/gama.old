/**
* Name: Socket_TCP_HelloWorld
* Author: Arnaud Grignard & HUYNH Quang-Nghi
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, Socket, TCP
*/

model SocketTCP_HelloWorld

global {	

	init
	{
		create NetworkingAgent number:1{
			would_like_to_send<- name+" " + cycle + " sent to " + dest;
		   do connect to:"localhost" protocol:"udp_server" port:9876 with_name:"Server";
		}
		
//		create NetworkingAgent number:1{
//			would_like_to_send<-"I am Server " + name + " I give order to " ;		
//			do connect to:"localhost" protocol:"tcp_client" port:"3001" with_name:"Client";
//		}

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
		loop id over:network_groups{
			write id;
			do send_message to:id content:would_like_to_send;
		}
	}
}

experiment Server_testdd type: gui {
	output {
	}
}
