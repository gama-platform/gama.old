/**
* Name: UDP Listener Mouse from Processing For camera.gaml
* Author: Arnaud Grignard & Benoit Gaudou
* Description: A server is run and waits for messages that transmit coordinates of the camera.
* 			   The model library provides an example of such an external application:
* 				UDPMouseLocationSender / UDPMouseLocationSender.pde
* Tags: Network, Socket, UDP
*/

model SocketUDP_Server_Mouse_Listener_For_Camera

global skills:[network] {	

	int port <- 9877;
	string url <- "localhost";	
	point cam_loc <- {0,0};
	
	init {
		write "After having launched this model, run the program UDPMouseLocationSender / UDPMouseLocationSender.pde with Processing 3. ";
		write "Processing 3 can be found here: https://processing.org/";
		write "Run the GAMA simulation, move on Processing and move the mouse on the gray small screen and observe the camera in GAMA" color: #red;
		
		do connect to: url protocol:"udp_server" port: port ;	
		
		create observedAgents number: 10;
	} 
	
	reflex camera_loc {
		if (length(mailbox) > 0) {
			write mailbox;
			message s <- last(mailbox);
			list coordinates <- string(s.contents) split_with(";");
			cam_loc <- {int(coordinates[0]),int(coordinates[1])};
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
		display d type: opengl 
				camera_look_pos: cam_loc
				camera_up_vector:{0.0,-1.0,0.0}{
			species observedAgents;	
		}
	}
}
