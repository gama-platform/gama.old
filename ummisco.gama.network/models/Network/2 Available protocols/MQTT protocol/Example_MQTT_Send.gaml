/**
* Name: MQTT__Send
* Author: Nicolas Marilleau and Arnaud Grignard
* Description: Two clients are communicated through the MQTT protocol.
* Tags: Network, MQTT
*/

model MQTT_HelloWorld_Send

global {	
	list<string> clients <-["sender","receiver"];
	init {
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Receive.gaml, too show how agents receive messages.";
		
		create NetworkingAgent number:1{
			name <-clients[0];
			dest <- clients[1];
			/**
			 * Demo connection based on the demo gama server. 
			 * Using the demo gama server requires an available internet connection. Depending on your web access, It could be slow down the simulation. 
			 * It is a free and unsecure server.
			 * Using YOUR server is thus adviced. You can download free solution such as ActiveMQ (http://activemq.apache.org) 
			 */
			do connect  with_name:"sender";
			
			// default ActiveMQ MQTT login is "admin", the password is "admin" and the port is 1883
			// do connect to:"localhost" with_name:"sender" login:"admin" password:"admin" port: 1883;
		}
	}
}

species NetworkingAgent skills:[network]{
	string name; 
	string dest;
	
	reflex send when: cycle mod 10  = 3
	{
		write "sending message: " + "This message a string from " + name;
		do send to:"sender" contents:"This message a string from " + name;
		do send to:"receiver" contents:"This message a string from " + name;
	}
	
	reflex send2 when: cycle mod 10  = 5
	{
		int a <- 0;		
		write "sending message: " + a;
		do send to:"sender" contents:a;
		do send to:"receiver" contents:a;		
	}	

	reflex send3 when: cycle mod 10  = 8
	{
		write "sending message: " + self;
		do send to:"sender" contents:self;
	}
	
	reflex receive
	{
		write "length mail box "  + mailbox collect(each.contents);
	}
}

experiment Network_sender type: gui {
	output {
	}
}
