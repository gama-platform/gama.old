/**
* Name: MQTT_PING_PONG
* Author: Nicolas Marilleau and Arnaud Grignard
* Description: The simple PING PONG model based on MQTT protocol.
* Tags: Network, MQTT
*/

model MQTT_PING_PONG

global {	
	list<string> clients <-["ping","pong"];
	init
	{
		
		//create Ping agent
		create NetworkingAgent number:1{
			name <-clients[0];
			dest <- clients[1];
			/**
			 * Demo connection based on the demo gama server. 
			 * Using the demo gama server requires an available internet connection. Depending on your web access, It could be slow down the simulation. 
			 * It is a free and unsecure server.
			 * Using YOUR server is thus adviced. You can download free solution such as ActiveMQ (http://activemq.apache.org) 
			 */
			do connect  with_name:name;
			
			//default ActiveMQ mqtt login is "admin", the password is "password"
			//do connect to:"localhost" with_name:name login:"admin" password:"password";
		}
		
		//create Pong Agent
		create NetworkingAgent number:1{
		    name <-clients[1];
		    dest <- clients[0];
			do connect  with_name:name;
			
			//send a message to the destination
			do send to:dest contents:"This message is sent by " + name + " to " + dest;
		}
	}
}

species NetworkingAgent skills:[network]{
	string name;
	string dest;
	reflex fetch when:has_more_message()
	{	
		//read a message
		message mess <- fetch_message();
		//display the message 
		write name + " fecth this message: " + mess.contents;	
		//send a message
		do send to:dest contents:"This message is sent by " + name + " to " + dest;
	}

}

experiment start type: gui {
	output {
	}
}
