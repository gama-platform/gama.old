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
			do connect to: "localhost" protocol: "tcp_server" port: 3001 with_name: "Server"+id;
			do join_group with_name:"test";
			id<-id+1;
		}

	}

}

species Networking_Server skills: [network]
{
	string name;
	string dest;
	reflex receive
	{   
		write "mailbox";
		if (length(mailbox) > 0)
		{
			write mailbox;
		}

	}

	reflex send when: every(3.0)
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
