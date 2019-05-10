/**
* Name: UDP Emitter Example.gaml
* Author: Benoit Gaudou & Nicolas Marilleau
* Description: An UDP emitter that sends the coordinates of an agent toward an UDP server.
* 				One server example (in Processing) is provided in the model library in the UDPServer folder.
* Tags: Network, Socket, UDP
*/

model SocketUDP_Emitter

global {	

	int port <- 9876;
	string url <- "localhost";

	init {
		write "Before running this model, run an UDP server (on the same port)." color: #red;
		write "1 server example is provided using Processing 3.";
		write "Processing 3 can be downloaded from: https://processing.org/";
		write "First run one server (e.g. UDPServer.pde), then launch and run the GAMA simulation" color: #red;
		
		
		create NetworkingAgent number:1 {
		   do connect to: url protocol: "udp_emitter" port: port ;
		}		
	} 
}

species NetworkingAgent skills:[network] {

	reflex move {
		location <- any_location_in(world);
	}

	reflex send {
		write "sending message ";
		do send contents: "" + location.x + ";" + location.y;
	}
	
	aspect default {
		draw circle(1) color: #red border: #blue;
	}
}

experiment Client_testdd type: gui {

	output {
		display "My display" { 
			species NetworkingAgent;
		}

	}
}