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
		
		create Networking_Server number:4
		{
			do connect protocol: "tcp_server" port: 3001 with_name: "Server"+id;
			do join_group with_name:"test";
			id<-id+1;
		}

	}

}

species Networking_Server skills: [network]
{
	string dest;
	reflex receive when:has_more_message()
	{   
		loop while:has_more_message()
		{
			message mm <- fetch_message();
			write mm.contents;
		}

	}

	reflex send when: every(3#cycle)
	{
		do send to: "test" contents: ("I am Server " + name + " I give order to Client");
	}

}

experiment "TCP Server Test" type: gui
{
	output
	{
	}

}
