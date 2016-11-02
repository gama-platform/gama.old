/**
* Name: Socket_TCP_HelloWorld_Server
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, TCP, Socket
*/
model Socket_TCP_HelloWorld_Server


global
{
	init
	{
		create Networking_Server
		{
			do connect to: "localhost" protocol: "tcp_server" port: 3001 with_name: "Server";
		}

	}

}

species Networking_Server skills: [network]
{
	string name;
	string dest;
	reflex receive
	{   write "mailbox";
		if (length(mailbox) > 0)
		{
			write mailbox;
		}

	}

	reflex send
	{
		loop id over: network_groups
		{
			do send to: id contents: "I am Server " + name + " I give order to " + id;
		}

	}

}

experiment "TCP Server Test" type: gui
{
	output
	{
	}

}
