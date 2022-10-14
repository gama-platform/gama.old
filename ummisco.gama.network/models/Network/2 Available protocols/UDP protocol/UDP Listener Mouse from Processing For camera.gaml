/**
* Name: UDP Listener Mouse from Processing For camera.gaml
* Author: Arnaud Grignard, Benoit Gaudou & Nicolas Marilleau
* Description: A server is run and waits for messages that transmit coordinates of the camera.
* 			   The model library provides an example of such an external application:
* 				UDPMouseLocationSender / UDPMouseLocationSender.pde
* Tags: Network, Socket, UDP
*/
model SocketUDP_Server_Mouse_Listener_For_Camera

global skills: [network] {
	int port <- 9877;
	string url <- "localhost";
	point cam_loc <- {0, 0};

	init {
		write "After having launched this model, run the program UDPMouseLocationSender / UDPMouseLocationSender.pde with Processing 3. ";
		write "Processing 3 can be found here: https://processing.org/";
		write "Run the GAMA simulation, move on Processing and move the mouse on the gray small screen and observe the camera in GAMA" color: #red;
		do connect to: url protocol: "udp_server" port: port size_packet: 1024;
		create observedAgents number: 10;
	}

	reflex fetch when: has_more_message() {
		loop while: has_more_message() {
			message s <- fetch_message();
			list coordinates <- string(s.contents) split_with (";");
			location <- {int(coordinates[0]), int(coordinates[1])};
			cam_loc <- {int(coordinates[0]), int(coordinates[1])};
		}

	}

}

species observedAgents {
	int size <- rnd(10);

	aspect default {
		draw cube(size) color: #red border: #black;
	}

}

experiment Server_testdd type: gui {
	output {
		display d type: 3d {
			camera #default target: cam_loc dynamic: true;
			species observedAgents;
		}

	}

}
