/**
* Name: MQQT_HelloWorld_Send
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld_Send

global {	
	list<string> clients <-["send","recieve"];
	init
	{
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
		write "sending message ";
		do send to:"send" contents:"This message a string" + name;
	}
	reflex send2 when: cycle mod 10  = 5
	{
		write "sending message ";
		int a <- 0;
		do send to:"send" contents:a;
	}
	

	reflex send3 when: cycle mod 10  = 8
	{
		write "sending message ";
		do send to:"send" contents:self;
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
