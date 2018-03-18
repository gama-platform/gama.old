/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_Send

global {	
	list<string> clients <-["send","receive"];
	init {
		write "A MQTT server should run." color: #red;
		write "Another instance of GAMA should run the model Example_MQTT_Receive.gaml, too show how agents receive messages.";
		
		int i <- 0;
		create NetworkingAgent number:1{
			name <-clients[0];
			dest <- clients[1];
			
			i <- i + 1;
			do connect to:"localhost" with_name:"send";
		}
	}
}

species NetworkingAgent skills:[network]{
	string name; 
	string dest;
	reflex send when: cycle mod 10  = 3
	{
		write "sending message: " + "This message a string" + name;
		do send to:"send" contents:"This message a string" + name;
		do send to:"receive" contents:"This message a string" + name;		
	}
	reflex send2 when: cycle mod 10  = 5
	{
		int a <- 0;		
		write "sending message: " + a;
		do send to:"send" contents:a;
		do send to:"receive" contents:a;		
	}
	

	reflex send3 when: cycle mod 10  = 8
	{
		write "sending message: " + self;
		do send to:"send" contents:self;
		do send to:"receive" contents:self;
	}
	
	reflex receive
	{
		write "length mail box "  + mailbox collect(each.contents);
	}
}

experiment testdd type: gui {
	output {
	}
}
