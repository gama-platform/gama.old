/**
* Name: Socket_TCP_HelloWorld_Client
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the Socket TCP protocol.
* Tags: Network, TCP, Socket
*/
model Socket_TCP_HelloWorld_Client


global {
	init {
		create Networking_Client number:3 {
			// replace the "localhost" address by the IP address of the other computer 
			do connect to: "localhost" protocol: "tcp_client" port: 3001 with_name: "Client";
			do join_group with_name:"client_group";
		}
	}

}

species Networking_Client skills: [network] {
	reflex receive when:has_more_message() {
		loop while:has_more_message() {
			message mm <- fetch_message();
			write mm.contents;
		}
	}

	reflex send when:every(4#cycle) { 
		do send to: "server_group" contents: name + " at " + cycle + " sent to Server a message";
	}
}

experiment "TCP Client" type: gui
{
	output
	{
	}

}
