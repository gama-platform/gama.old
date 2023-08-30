/**
* Name: proxyUpdate
* create an exact copy of an agent on a different processor
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation
*/

model proxyUpdate

import "../Models_to_distribute/MovingAgent.gaml"

global
{
	int simulation_id <- 0;
	int neighbor_id <- 1;
	int number_of_other_simulation <- 1;
	
	bool possess <- false;
	
	 
	init
	{
		list var0 <- range(number_of_other_simulation);
		write(var0 - simulation_id);
		
		write("before " + var0);
		var0 <- reverse(var0);
		write("after " + var0);
		
		create commAgent with: [all_neighbors :: var0 - simulation_id];
		
		if(simulation_id = 0)
		{	
			create movingAgent;
			neighbor_id <- 1;
			possess <- true;
		}else
		{
			neighbor_id <- simulation_id + 1;
			if(neighbor_id > number_of_other_simulation)
			{
				neighbor_id <- 0;
			}
		}
	}
}

species commAgent skills: [network, ProxySkill]
{
	rgb col;
	list all_neighbors;
	
	init
	{
		do connect protocol: "tcp_server" port: 3001 with_name: "comm"+simulation_id;
	}
	
	reflex when: simulation_id = 0
	{
		write("----------------cycle : " + cycle);
	}
	
	reflex receiveMovingAgent when: has_more_message()
	{	
		loop while: has_more_message()
		{
			message msg <- fetch_message();
			map<string, unknown> content <- map(msg.contents);
			
			string request <- content["type"];
			write("request : " + request);
			write("content : " + content);
			
			if(request = "copy")
			{		
				write("content[agent] : " + content["agent"]);
				do createCopyAgent(content["agent"]);
				
				do die;
			}
		}	
	}
	
	reflex when: (cycle mod 5 = 0 and cycle != 0) and possess
	{
		write("simulation id " + simulation_id);
		do send to: "comm" + neighbor_id contents: ["agent" :: movingAgent[0], "type" :: "copy", "sender" :: simulation_id];
		possess <- false;
	}
}
experiment proxyCopy type: proxy 
{
	init
	{
		loop simulation_id_index from: 1 to: number_of_other_simulation {    
			create simulation with: [simulation_id :: simulation_id_index]; // new simulation
		}
	}
	
	output synchronized: true
	{
		display "proxyCopy" toolbar: false type: 2d 
		{
			species movingAgent aspect: classic;
			species followingAgent aspect: classic;
		}
	}
}
