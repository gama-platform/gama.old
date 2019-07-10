/**
* Name: UDP Listener Mouse from Processing.gaml
* Author: Arnaud Grignard, Benoit Gaudou  & Nicolas Marilleau
* Description: A server is run and waits for messages that transmit coordinates of the agent.
* 			   The model library provides an example of such an external application:
* 				UDPMouseLocationSender / UDPMouseLocationSender.pde
* Tags: Network, Socket, UDP
*/

model SocketUDP_Server_Mouse_Listener

global {	

	int port <- 9877;
	string url <- "localhost";	
	
	init {
		write "After having launched this model, run the program UDPMouseLocationSender / UDPMouseLocationSender.pde with Processing 3. ";
		write "Processing 3 can be found here: https://processing.org/";
		write "Run the GAMA simulation, move the mouse on the gray small screen of Processing 3 and observe the move of the agent in GAMA" color: #red;
		
		create NetworkingAgent number: 1 {
		   do connect to: url protocol: "udp_server" port: port ;
		}		
	} 
}

species NetworkingAgent skills:[network] {
	
	reflex fetch when:has_more_message() {	
		loop while:has_more_message()
		{
			message s <- fetch_message();
			list coordinates <- string(s.contents) split_with(";");
			location <- {int(coordinates[0]),int(coordinates[1])};
		}
	}
	
	aspect default {
		draw circle(1) color: #red border: #black; 
	}
}

experiment Server_testdd type: gui {
	output {
		display d {
			species NetworkingAgent;	
		}
	}
}
