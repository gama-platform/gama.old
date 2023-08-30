/**
* Name: Socket_TCP_HelloWorld_Server
* Author: HUYNH Quang Nghi
* Description: Simplest TCP server to test with external tools. 
* You can run the python script "client.py" to connect to it
* Tags: Network, TCP, Socket
*/
model Socket_TCP_HelloWorld_Server

global {

	init {
		create Server {
			do connect protocol: "tcp_server" port: 3001 with_name: name raw: true;
		}

	}

}

species Server skills: [network] parallel: true {
	string dest;
	rgb color;

	reflex receive when: has_more_message() {
		loop while: has_more_message() {
			message mm <- fetch_message();
			write name + " received : " + mm.contents color: color;
			do send to: mm.sender contents: ("I am Server Leader " + name + ", I give order to server_group");
		}

	}

}

experiment "TCP Server Test" type: gui {
	float minimum_cycle_duration <- 0.25;
	output {
	}

}
