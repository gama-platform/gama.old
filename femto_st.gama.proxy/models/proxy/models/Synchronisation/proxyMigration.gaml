/**
* Name: proxyUpdate
* move an agent across different simulation + updates all his distant agent
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation
*/

model proxyMigration

import "../Models_to_distribute/MovingAgent.gaml"

global
{
	int simulation_id <- 0;
	bool possess_agent <- false;
	int neighbor_id <- 1;
	int number_of_other_simulation <- 5;
	bool sent <- false;
	 
	init
	{
		create commAgent;
		
		if(simulation_id = 0)
		{	
			
			create movingAgent;
			//create followingAgent with: [target :: movingAgent[0].location, targetAgent:: movingAgent[0], location :: any_location_in(world)];	

			possess_agent <- true;
			neighbor_id <- 1;
		}else
		{
			sent <- true;
			neighbor_id <- 0;
		}
	}
}

species commAgent skills: [network, ProxySkill]
{
	rgb col;
	
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
			
			if(request = "update" and length(movingAgent)!= 0)
			{			
				do updateProxy(content["agent"]);
				
			}else if(request = "migrate")
			{		
				do createCopyAgent(content["agent"]);
				possess_agent <- true;
				do send to: "comm" + content["sender"] contents: ["type" :: "received"];
				
			}else if(request = "received")
			{
				do setAgentAsDistant(movingAgent[0]);
			}
		}	
	}
	
	reflex when: (cycle mod 10 = 0 and cycle != 0) and possess_agent //and !sent
	{
		do send to: "comm" + neighbor_id contents: ["agent" :: movingAgent[0], "type" :: "migrate", "sender" :: simulation_id];
		possess_agent <- false;
	}
	
	
	reflex when: possess_agent
	{
		do send to: "comm" + neighbor_id contents:["type" :: "update", "agent" :: movingAgent[0]];	
		
	}
}
experiment proxyMigration type: proxy 
{
	init
	{
		create simulation with: [simulation_id :: 1]; // new simulation
	}
	output
	{
		display "proxyMigration" toolbar: false type: 2d
		{
			species movingAgent aspect: classic;
			species followingAgent aspect: classic;
		}
	}
}
