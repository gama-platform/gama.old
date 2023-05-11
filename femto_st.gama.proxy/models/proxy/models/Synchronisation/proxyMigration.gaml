/**
* Name: migration
* Set an agent as a distantAgent + update his attributes from time to time
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation
*/

model proxyMigration

global
{
	int simulation_id <- 0;
	movingAgent globalMoving;
	bool possess_agent <- false;
	int neighbor_id <- 1;
	 
	init
	{
		seed <- 10.0;
		create commAgent;
		
		if(simulation_id != 1)
		{	
			commAgent[0].col <- #blue;
			create movingAgent with: [ location :: any_location_in(world), target :: any_location_in(world)] returns: globalAgent;
			globalMoving <- globalAgent[0];

			create followingAgent with: [target :: globalMoving.location, targetAgent:: globalMoving, location :: any_location_in(world)];	
			
			possess_agent <- true;
			neighbor_id <- 1;
		}else
		{
			commAgent[0].col <- #red;
			neighbor_id <- 0;
		}
	}
}

species movingAgent skills:[moving]
{	
	rgb col;
	point target;
	
	init
	{
		col <- #red;
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

species followingAgent parent: movingAgent
{	
	movingAgent targetAgent;
	
	init
	{
		col <- #black;
		speed <- targetAgent.speed - (targetAgent.speed / rnd(2, 5));
	}
	
	reflex move when: target != location
	{
		target <- targetAgent.location;
		do goto speed: speed target:target;
	}
	
	reflex target when: target = location
	{
		target <- targetAgent.location;
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
			//write("" + simulation_id + " received " + msg) color: col;
			map<string, unknown> content <- map(msg.contents);
			
			string request <- content["type"];
			//write("received request : " + request) color: col;
		
			if(request = "update" and globalMoving != nil)
			{
				//write("" + simulation_id + " update agent ") color: col;
				do updateProxy(globalMoving, content["agent"]);
				
			}else if(request = "migrate")
			{		
				//write("" + simulation_id + " migrate agent " + content["agent"]) color: col;
				do migrateAgent(content["agent"], content["typeAgent"]);
				globalMoving <- movingAgent[0];
				possess_agent <- true;
				
				do send to: "comm" + neighbor_id contents: ["type" :: "received"];
				
			}else if(request = "received")
			{
				do setAgentAsDistant(globalMoving);
				possess_agent <- false;
			}
		}	
	}
	
	reflex when: (cycle mod 10 = 0 and cycle != 0) and possess_agent
	{
		//write("migrate") color: col;
		do send to: "comm" + neighbor_id contents: ["agent" :: globalMoving, "typeAgent" :: "movingAgent", "type" :: "migrate"];
	}
	
	reflex when: possess_agent
	{
		//write("simulation(" + simulation_id + ") : update agent") color: col;
		do send to:"comm" + neighbor_id  contents:["type" :: "update", "agent" :: globalMoving];	
	}
}
experiment proxyMigration type: proxy 
{
	
	init
	{
		create simulation with: [simulation_id :: 1]; // 2nd simulation
	}
	output
	{
		display "proxyMigration"
		{
			species movingAgent aspect: classic;
			species followingAgent aspect: classic;
		}
	}
}
