/**
* Name: Socket_TCP_HelloWorld_Client
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, TCP, Socket
*/
model Socket_TCP_HelloWorld_Client


global
{
	init
	{
		create Networking_Client number:1
		{
			do connect to: "localhost" protocol: "tcp_client" port: 3001 with_name: "Client";
			do register_to_group to:"test";
		}

	}

}

species Networking_Client skills: [network]
{
	string name;
	string dest;
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
		do send to: "Server0" contents: name + " " + cycle + " sent to server";
		do send to: "test" contents: name + " broadcast a message ";
	}

}

experiment "TCP Client Test" type: gui
{
	output
	{
	}

}
