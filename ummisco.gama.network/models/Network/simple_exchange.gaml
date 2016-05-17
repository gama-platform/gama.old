/**
* Name: test
* Author: nicolas
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model test

global {
	/** Insert the global definitions, variables and actions here */
	
	init
	{
		int i <- 0;
		create pong number:1
		{
			i<- i+1;
			do connect to:"localhost" with_name:"pong";
		}
	 	
		create ping number:1
		{
			i<- i+1;
			do connect to:"localhost" with_name:"ping";
			do send_message to:"pong" content:"coucou";
		}
		
	}
}

species ping skills:[network]
{
	reflex sendData when:has_received_message()
	{
		
		map toto <- fetch_message();
		write toto;
		map<string,string> tmp <- ["name"::"ping"];
		
	}
	reflex send
	{
		do send_message to:"pong" content:"coucou 2";
	}
}

species pong skills:[network]
{
	reflex sendData when:has_received_message()
	{
		map toto <- fetch_message();
		write toto;
		map<string,string> tmp <- ["name"::"pong"];
		//do send_message to:"ping" content:tmp;
	}

}

experiment testdd type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
	}
}
