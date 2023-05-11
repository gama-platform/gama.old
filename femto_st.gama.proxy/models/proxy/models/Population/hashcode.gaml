/**
* Name: hashcode
* model to test hashcode propagation
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation
*/

model hashcode

global
{
	int simulation_id <- 0;
	int neighbor_id <- 1;
	bool possess_agent;
	 
	init
	{
		seed <- 10.0;
		create commAgent;
		
		if(simulation_id != 1)
		{	
			commAgent[0].col <- #blue;
			create regularAgent;
			possess_agent <- true;
			
			neighbor_id <- 1;
		}else
		{
			commAgent[0].col <- #red;
			neighbor_id <- 0;
		}
	}
}

species regularAgent skills:[moving]
{
	rgb col;
	point target;
	
	init
	{
		col <- #red;
		target <- any_location_in(world);
	}
	
	aspect classic
	{		
		draw circle(2) color: col;
		draw line(location, target) color: col;
	}
	
	reflex move when: target != location
	{
		do goto speed: speed target:target;
	}
	
	reflex target when: target = location
	{
		target <- any_location_in(world);
		location <- any_location_in(world);
	}
}

species commAgent skills: [network, ProxySkill]
{
	rgb col;
	
	init
	{
		do connect protocol: "tcp_server" port: 3001 with_name: "comm"+simulation_id;
		do join_group with_name: "bufferProxy";
	}
	
	reflex receiveMovingAgent when: has_more_message()
	{	
		loop while: has_more_message()
		{
			message msg <- fetch_message();
			write("" + simulation_id + " received " + msg) color: col;
			map<string, unknown> content <- map(msg.contents);
			
			string request <- content["type"];
			write("received request : " + request) color: col;
		
			if(request = "migrate")
			{
				write("simulation(" + simulation_id + ") migrate agent ") color: col;
				do migrateAgent(content["agent"], content["typeAgent"]);
				possess_agent <- true;
				
				do send to: "comm" + neighbor_id contents: ["type" :: "received"];
			}else if(request = "received")
			{
				do setAgentAsDistant(regularAgent[0]);
				possess_agent <- false;
			}
		}	
	}
	
	reflex when: cycle mod 5 = 0 and cycle != 0 and possess_agent
	{
		do send to: "comm" + neighbor_id contents: ["agent" :: regularAgent[0], "typeAgent" :: "regularAgent", "type" :: "migrate"];
		possess_agent <- false;
	}
	
	reflex when: possess_agent
	{
		do checkHashCode(regularAgent[0], simulation_id);
	}
	
}
experiment hashcode type: proxy 
{
	
	init
	{
		create simulation with: [simulation_id :: 1]; // 2nd simulation
	}
	output
	{
		display "hashcode"
		{
			species regularAgent aspect: classic;
		}
	}
}
