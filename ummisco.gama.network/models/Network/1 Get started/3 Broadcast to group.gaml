/**
* Name: broadcast to a group of agents
* Author: Nicolas Marilleau
* Description: It is a simple model showing an agent (a teacher called Victoria) that broadcast a message to students.
* Tags: Network, MQTT
*/

/**
 * Demo connection based on the demo gama server. 
 * Using the demo gama server requires an available internet connection. Depending on your web access, It could be slow down the simulation. 
 * It is a free and unsecure server.
 * Using YOUR server is thus adviced. You can download free solution such as ActiveMQ (http://activemq.apache.org) 
 */


model broadcast_to_a_group_of_agents

global
{
	int nb_student <- 10;
	init
	{
		create Teacher with:[name::"Victoria"]{
			do connect  with_name:name ;
			//default ActiveMQ MQTT login is "admin", the password is "admin" and the port is 1883
			//do connect to:"localhost" with_name:name login:"admin" password:"admin" port: 1883;
		}

		create Student number:nb_student {
			// The name attribute of each agent being unique, we use it as an id in the connection to the server	
			do connect  with_name:name ;
			
	 		// Define here the groups of agents. An agent could join or leave several group.
	 		do join_group with_name:"students";
			//default ActiveMQ MQTT login is "admin", the password is "admin" and the port is 1883
			//do connect to:"localhost" with_name:name login:"admin" password:"admin" port: 1883;
		}		
	}
}

species Student skills:[network]{
	string name;
	reflex fetch when:has_more_message()
	{	
		loop while:has_more_message()
		{
			//read a message
			message mess <- fetch_message();
			//display the message 
			write name + " fecth this lesson: " + mess.contents;				
		}
	}
}

species Teacher skills:[network]{
	
	reflex send
	{
		//send a message
		do send to:"students" contents:"chapter "+ cycle;
	}

}

experiment start type: gui {
	output {
	}
}