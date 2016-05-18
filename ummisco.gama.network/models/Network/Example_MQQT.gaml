/**
* Name: MQQT_HelloWorld
* Author: Arnaud Grignard
* Description: Two clients are communicated throught the MQQT protocol.
* Tags: Network, MQQT
*/

model MQQT_HelloWorld

global {	
	init
	{
		create pong number:1
		{
			do connect to:"localhost" with_name:"pong";
		} 	
		create ping number:1
		{
			do connect to:"localhost" with_name:"ping";
			do send_message to:"pong" content:"This message is sent by ping";
		}	
	}
}

species ping skills:[network]
{
	reflex fetchData when:has_received_message()
	{	
		map mess <- fetch_message();
		write "ping fecth this message" + mess;
		map<string,string> tmp <- ["name"::"ping"];
		
		
	}
	reflex send
	{
		do send_message to:"pong" content:"This message is sent by ping";
	}
}

species pong skills:[network]
{
	reflex fetchData when:has_received_message()
	{
		map mess <- fetch_message();
		write "pong fecth this message" + mess;
		map<string,string> tmp <- ["name"::"pong"];
	}
}

experiment testdd type: gui {
	output {
	}
}
