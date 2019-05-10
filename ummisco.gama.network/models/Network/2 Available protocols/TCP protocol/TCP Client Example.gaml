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
		create Networking_Client number:3
		{
			do connect to: "localhost" protocol: "tcp_client" port: 3001 with_name: "Client";
			do join_group with_name:"test";
		}

	}

}

species Networking_Client skills: [network]
{
	string name;
	string dest;
	reflex receive
	{
			write "** name ********************";
			loop while:has_more_message()
			{
				message mm <- fetch_message();
				write mm.contents;
			}
			write "** name ********************";
		
	}

	reflex send
	{
		//do send to: "Server0" contents: name + " " + cycle + " sent to server";
		//do send to: "test" contents: name + " broadcast a message ";
	}

}

experiment "TCP Client Test" type: gui
{
	output
	{
	}

}
