/**
* Name: MQQT_HelloWorld
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld

global {	
	list<string> clients <-["ping","pong"];
	init
	{
		create NetworkingAgent number:1{
			name <-clients[0];
			dest <- clients[1];
			//default activemq mqtt login is "admin", the password is "password"
			do connect to:"localhost" with_name:name login:"admin" password:"password";
		}
		create NetworkingAgent number:1{
		    name <-clients[1];
		    dest <- clients[0];
			//default activemq mqtt login is "admin", the password is "password"
			do connect to:"localhost" with_name:name login:"admin" password:"password";
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
