/**
* Name: Socket_TCP_HelloWorld_Server
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, TCP, Socket
*/
model Socket_TCP_HelloWorld_Server


global
{
	int id <- 0;
	init
	{
		
		create Networking_server number:4
		{
			do connect protocol: "tcp_server" port: 3001 with_name: "Server"+id;
			do join_group with_name:"server_group";
			id<-id+1;
			color <- rnd_color(255);
		}
		
		ask one_of(Networking_server) {
			isLeader <- true;
		}

	}

}

species Networking_server skills: [network]
{
	string dest;
	rgb color;
	bool isLeader <- false;
	
	reflex receive when:has_more_message()
	{   
		loop while:has_more_message()
		{
			message mm <- fetch_message();
			write name + " received : " + mm.contents color: color;
		}

	}

	reflex send when: every(3#cycle) and isLeader
	{
		do send to: "server_group" contents: ("I am Server Leader " + name + " I give order to other Servers");		
		do send to: "client_group" contents: ("I am Server " + name + " I give order to Clients");
	}

}

experiment "TCP Server Test" type: gui
{
	output
	{
	}

}
