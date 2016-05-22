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
	reflex send
	{
		write "sending message ";
		do send to:"send" contents:"This message is sent by " + name;
	}
	
	reflex receive
	{
		write "length mail box "  + length(mailbox);
		if(length(mailbox)>0 )
		{
					write "one element " + one_of(mailbox).contents;
						
		}
		
	}
}

experiment testdd type: gui {
	output {
	}
}
