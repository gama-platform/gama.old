/**
* Name: MQTT_PING_PONG
* Author: Nicolas Marilleau and Arnaud Grignard
* Description: The simple PING PONG model based on MQTT protocol.
* Tags: Network, MQTT
*/


/**
 * Demo connection based on the demo gama server. 
 * Using the demo gama server requires an available internet connection. Depending on your web access, It could be slow down the simulation. 
 * It is a free and unsecure server.
 * Using YOUR server is thus adviced. You can download free solution such as ActiveMQ (http://activemq.apache.org) 
 */


model PING_PONG_model

global {	
	list<string> clients <-["ping","pong"];
	init
	{	
		write "The default connect accesess a default remote server in France. An internet connection is thus requiered." color: #red;
		write "To connect to your local/remote server, change the parameters of the connect statement" color: #blue;
		//create Ping agent
		create PING_PONG with:[name::"ping",dest::"pong"]{
			do connect with_name:name;
		}
		//create Pong agent
		create PING_PONG with:[name::"pong",dest::"ping"]{
			do connect with_name:name ;
			do send to:dest contents:"This message is sent by " + name + " to " + dest;
		}
		//default ActiveMQ MQTT login is "admin", the password is "admin" and the port is 1883
		//do connect to:"localhost" with_name:name login:"admin" password:"admin" port: 1883;
	}
}

species PING_PONG skills:[network]{
	string name;
	string dest;
	reflex fetch when:has_more_message()
	{	
		loop while:has_more_message()
		{
			//read a message
			message mess <- fetch_message();
			//display the message 
			write name + " fecth this message: " + mess.contents;	
			//send a message
			do send to:dest contents:"This message is sent by " + name + " to " + dest;
		}
	}
}

experiment start type: gui {
	output {
	}
}