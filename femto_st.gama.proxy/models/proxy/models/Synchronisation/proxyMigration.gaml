/**
* Name: proxyUpdate
* move an agent across different simulation + updates all his distant agent
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation
*/

model proxyMigration

global
{
	int simulation_id <- 0;
	bool possess_agent <- false;
	int neighbor_id <- 1;
	int number_of_other_simulation <- 5;
	
	bool display_true <- false;
	 
	init
	{
		create commAgent;
		
		if(simulation_id = 0)
		{	
			create movingAgent with: [ location :: any_location_in(world), target :: any_location_in(world)] returns: globalAgent;

			create followingAgent with: [target :: movingAgent[0].location, targetAgent:: movingAgent[0], location :: any_location_in(world)];	

			possess_agent <- true;
			neighbor_id <- 1;
			display_true <- true;
		}else
		{
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
		draw line(location, target) color: col;
		if(display_true)
		{
			draw circle(5) color: col;
		}else
		{
			draw circle(2) color: col;
		}
	}
	
	reflex move when: target != location
	{
		do goto speed: speed target:target;
	}
	
	reflex target when: target = location
	{
		target <- any_location_in(world);
	}
}

species followingAgent parent: movingAgent
{	
	movingAgent targetAgent;
	
	init
	{
		col <- #black;
		speed <- targetAgent.speed - (targetAgent.speed / 20);
	}
	
	aspect classic
	{		
		draw line(location, target) color: col;
		draw circle(2) color: col;
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
				do migrateAgent(content["agent"]);
				possess_agent <- true;
				do send to: "comm" + content["sender"] contents: ["type" :: "received"];
				display_true <- true;
				
			}else if(request = "received")
			{
				do setAgentAsDistant(movingAgent[0]);
				display_true <- false;
			}
		}	
	}
	
	reflex when: (cycle mod 10 = 0 and cycle != 0) and possess_agent
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
