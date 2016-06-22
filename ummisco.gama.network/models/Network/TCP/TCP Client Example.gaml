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
		create Networking_Client
		{
			do connect to: "localhost" protocol: "tcp_client" port: "3001" with_name: "Client";
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
		do send to: "send" contents: name + " " + cycle + " sent to server";
	}

}

experiment "TCP Client Test" type: gui
{
	output
	{
	}

}
