/**
* Name: Socket_TCP_HelloWorld_Server
* Author: Arnaud Grignard
* Description: Two clients are communicating throught the Socket WebSocket protocol.
* Tags: Network, TCP, Socket
*/
model WebSocket_HelloWorld

global{
	int id <- 0;
	string type <- "server";

	init {
		if (type = "server") {
			do create_server;
		}

		if (type = "client") {
			do create_client;
		}

	}

	action create_server {
		create Server number: 2 {
			do connect protocol: "websocket_server" port: 3001 with_name:name force_network_use:true;
			do join_group with_name: "server_group";
			id <- id + 1;
			color <- rnd_color(255);
		}

		ask one_of(Server) {
			isLeader <- true;
		}

	}

	action create_client {
		create Client number: 2 {
		// replace the "localhost" address by the IP address of the other computer 
			do connect to: "localhost" protocol: "websocket_client" port: 3001 with_name: name  force_network_use:true;
			do join_group with_name: "client_group";
			id <- id + 1;
			color <- rgb(rnd(255)); 
		}

	}
	reflex space{
		write "";
		write "";
		write "";
		write "";
	}
}

species Server skills: [network]  parallel:true{
	string dest;
	rgb color;
	bool isLeader <- false;

	reflex receive when: has_more_message() {
		loop while: has_more_message() {
			message mm <- fetch_message();
			write name + " received : " + mm.contents color: color;
		}

	}

	reflex send when: isLeader {
		do send to: "client_group" contents: ("I am Server Leader " + name + ", I give order to client_group at " + cycle);
		do send to: "server_group" contents: ("I am Server Leader " + name + ", I give order to server_group");
	}

}

species Client skills: [network]  parallel:true{
	rgb color;

	reflex receive when: has_more_message() {
		loop while: has_more_message() {
			message mm <- fetch_message();
			write name + " received : " + mm.contents color: color;
		}

	}

	reflex send {
		do send to: "server_group" contents: name + " at " + cycle + " sent to server_group a message";
	}

}

experiment "WebSocket Server and Client" type: gui {
	float minimum_cycle_duration <- 0.25;

	init {
		create simulation with: [type:: "client"];
	}

	output {
	}

}
