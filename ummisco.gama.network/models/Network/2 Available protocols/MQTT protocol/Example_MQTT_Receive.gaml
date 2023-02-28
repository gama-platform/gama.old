/**
* Name: MQTT_Receiver
* Author: Nicolas Marilleau and Arnaud Grignard
* Description: The simple receiver model based on MQTT protocol.
* Tags: Network, MQTT
*/

model MQTT_HelloWorld_Receive



global {	
	init {
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Send.gaml, so that an agent can send messages.";
		
		create NetworkingAgent number:1 {
			/**
			 * Demo connection based on the demo gama server. 
			 * Using the demo gama server requires an available internet connection. Depending on your web access, It could slow down the simulation. 
			 * It is a free and unsecure server.
			 * Using YOUR server is thus adviced. You can download free solution such as ActiveMQ (http://activemq.apache.org) 
			 */
			do connect  with_name:"receiver";
			
			// default ActiveMQ MQTT login is "admin", the password is "admin" and the port is 1883
			// do connect to:"localhost" with_name:"receiver" login:"admin" password:"admin" port: 1883;
		}
	}
}

species NetworkingAgent skills:[network]{
	string name;
	string dest;

	reflex fetch when:has_more_message() {	
		loop while: has_more_message() {
			message mess <- fetch_message();
			write "fetch this message: " + mess;				
		}
	}
}

experiment Network_receiver type: gui {
	output {
	}
}
