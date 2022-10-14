/**
* Name: MQTT__Send
* Author: Nicolas Marilleau and Arnaud Grignard
* Description: Two clients are communicated throught the MQTT protocol.
* Tags: Network, MQTT
*/

model MQTT_SendAgent

global skills:[network]{	
	init
	{   
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Receive_Agent.gaml, too show how agents receive messages.";
		/**
		 * Demo connection based on the demo gama server. 
		 * Using the demo gama server requires an available internet connection. Depending on your web access, It could be slow down the simulation. 
		 * It is a free and unsecure server.
		 * Using YOUR server is thus adviced. You can download free solution such as ActiveMQ (http://activemq.apache.org) 
		 */
		do connect  with_name:"sender";
		
		// default ActiveMQ MQTT login is "admin", the password is "admin" and the port is 1883
		// do connect to:"localhost" with_name:"sender" login:"admin" password:"admin" port: 1883;
		
		create NetworkingAgent number:10{	
			color <- rnd_color(255);	
			shape <-sphere(5);	
		}
	}
	reflex sendAgent{
		write "send agent on the network";
		do send to:"receiver" contents:(9 among NetworkingAgent);	
	}
}

species NetworkingAgent skills:[moving]{
   rgb color;
   reflex update{
     do wander;
   }	
   aspect base{
   	draw shape color:color;
   }
}

experiment Network_sender type: gui {
	output {
		display view type:3d{
			species NetworkingAgent aspect:base;
		}
	}
}
