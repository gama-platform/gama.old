/**
* Name: MQTT_Receiver
* Author: Nicolas Marilleau and Arnaud Grignard
* Description: The simple receiver model based on MQTT protocol.
* Tags: Network, MQTT
*/

model MQTT_Receiver

global skills:[network] {	
	init {   
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Send_Agent.gaml, too show how agents can send messages.";
		/**
		 * Demo connection based on the demo gama server. 
		 * Using the demo gama server requires an available internet connection. Depending on your web access, It could be slow down the simulation. 
		 * It is a free and unsecure server.
		 * Using YOUR server is thus adviced. You can download free solution such as ActiveMQ (http://activemq.apache.org) 
		 */
		do connect  with_name:"receiver";
		
		// default ActiveMQ MQTT login is "admin", the password is "admin" and the port is 1883
		// do connect to:"localhost" with_name: "receiver" login:"admin" password:"admin" port: 1883;
	}
	reflex receiveAgent when:has_more_message(){
		write "fetch agent on the network";
		message mess <- fetch_message();
		write name + " fecth this message: " + mess.contents;	
	}
}

species NetworkingAgent{
   rgb color;	
   aspect base{
   	draw shape color:color;
   }
	
}

experiment Network_receiver type: gui {
	output {
		display view{
			species NetworkingAgent aspect:base;
		}
	}
}
