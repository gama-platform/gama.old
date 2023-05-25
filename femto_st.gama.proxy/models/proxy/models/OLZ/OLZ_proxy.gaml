/**
* Name: OLZ_proxy
* Add a distant agent in the other simulation when an agent cross the OLZ (OverLappingZone)
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation
*/

model proxyUpdate

global
{
	geometry shape <- rectangle(50, 60);

	int simulation_id <- 0;
	int neighbor_id <- 1;
	int number_of_other_simulation <- 1;
	
	bool possess_agent <- false;
	bool display_true <- false;
	
	list<agent> new_agents_in_OLZ;
	list<agent> agents_in_OLZ_previous_step;
	
	list<agent> agent_leaving_OLZ;
	
	list<int> neighbors;
	
	init
	{
		list var0 <- range(number_of_other_simulation);
		
		create worldAgent;
		create cell;
		
		if(simulation_id = 0)
		{	
			seed <- 78.2;
			create movingAgent with: [ location :: any_location_in(cell[0].shape), target :: any_location_in(cell[0].shape)];

			possess_agent <- true;
			neighbor_id <- 1;
			display_true <- true;
			
		}else
		{
			//seed <- 85.3;
			//create movingAgent with: [ location :: any_location_in(cell[0].shape), target :: any_location_in(cell[0].shape)];
			neighbor_id <- 0;
		}
		
		neighbors <- range(number_of_other_simulation) - simulation_id;
		create commAgent;
	}
}
species worldAgent
{
	init 
	{
		shape <- world.shape;
		location <- { world.shape.width / 2 , world.shape.height / 2};
	}
	
	aspect default
	{
		draw shape color: rgb(#grey, 125) border: #black;
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
		draw circle(2) color: col;
	}
	
	reflex move when: target != location
	{
		do goto speed: speed target:target;
	}
	
	reflex target when: target = location
	{
		target <- any_location_in(cell[0]);
	}
}

species followingAgent parent: movingAgent
{	
	movingAgent targetAgent;
	
	init
	{
		col <- #black;
		speed <- self.speed - (self.speed / 20);
		
		if(targetAgent = nil)
		{
			target <- any_location_in(world);
		}else
		{
			target <- targetAgent.location;	
		}
	}
	
	aspect classic
	{		
		draw line(location, target) color: col;
		draw circle(2) color: col;
	}
	
	reflex move when: target != location
	{
		if(target = nil)
		{		
			target <- targetAgent.location;
		}
		
		do goto speed: speed target:target;
	}
	
	reflex target when: target = location
	{
		if(targetAgent = nil)
		{
			target <- any_location_in(world);
		}else
		{		
			target <- targetAgent.location;
		}
	}
}

species commAgent skills: [network, ProxySkill]
{
	rgb col;
	map<int, list<agent>> distant_agents_to_update;
	
	init
	{
		do connect protocol: "tcp_server" port: 3001 with_name: "comm"+simulation_id;
		
		loop tmp over: neighbors
		{
			list<agent> li;
			distant_agents_to_update <- [tmp :: li];
		}
		write("map :: " + distant_agents_to_update[neighbor_id]);
	}	
	
	reflex
	{
		write "lenght moving : " + length(movingAgent);
	}
	
	reflex receiveMovingAgent when: has_more_message()
	{	
		loop while: has_more_message()
		{
			message msg <- fetch_message();
			map<string, unknown> content <- map(msg.contents);
			
			write("content : "+ content);
			
			string request <- content["type"];
			
			if(request = "update")
			{	
				do updateProxy(content["agentData"]);
				
			}else if(request = "migrate")
			{		
				write("migrate " + content["agent"]);
				agent newAgent <- migrateAgent(content["agent"]);
				write("newAgent : " + newAgent);
				do setAgentAsDistant(newAgent);
				
			}else if(request = "deleteDistant")
			{		
				write("delete distant");
				do deleteDistant(content["agentToDelete"]);
			}
		}	
	}
	
	reflex when: length(distant_agents_to_update) != 0 and simulation_id = 0
	{
		write("distant_agent_from_proxy : " + distant_agents_to_update);
		loop list_of_agent_to_update over: distant_agents_to_update
		{		
			write("agent to update : " + list_of_agent_to_update);
			loop agent_to_update over: list_of_agent_to_update
			{				
				do send to: "comm" + neighbor_id contents: ["agentData" :: agent_to_update, "type" :: "update", "sender" :: simulation_id];
			}
		}
	}
	
	reflex migrate when: length(new_agents_in_OLZ) != 0 and simulation_id = 0
	{
		loop agent_to_migrate over: new_agents_in_OLZ
		{			
			if(!(distant_agents_to_update[neighbor_id] contains agent_to_migrate))
			{
				write "migrate : " + agent_to_migrate + " of type : " + string(species_of(agent_to_migrate)); 
				do send to: "comm" + neighbor_id contents: ["agent" :: agent_to_migrate, "type" :: "migrate", "sender" :: simulation_id];
				
				if(distant_agents_to_update[neighbor_id] != nil)
				{
					if(!(distant_agents_to_update[neighbor_id] contains agent_to_migrate))
					{
						add agent_to_migrate to: distant_agents_to_update[neighbor_id];
					}
				}
			}
		}
	}
	
	reflex when: agent_leaving_OLZ != nil and simulation_id = 0
	{
		loop agent_to_delete_distant over: agent_leaving_OLZ
		{				
			do send to: "comm" + neighbor_id contents: ["agentToDelete" :: agent_to_delete_distant, "type" :: "deleteDistant", "sender" :: simulation_id];
			write("distant_agents_to_update bf" + distant_agents_to_update);
			remove agent_to_delete_distant from: distant_agents_to_update[neighbor_id];
			write("distant_agents_to_update ater " + distant_agents_to_update);
		}
	}
}

species cell
{ 
	int size_OLZ <- 8;
	geometry OLZ;
	
	init
	{
		shape <- rectangle(world.shape.width / 2 , world.shape.height);
			
		if(simulation_id = 0)
		{
			location <- {shape.width / 2, shape.height / 2};
			OLZ <- rectangle(size_OLZ, shape.height) translated_to {(world.shape.width / 2) - size_OLZ/2, world.shape.height / 2};
		}else
		{		
			location <- {world.shape.width - shape.width / 2, shape.height / 2};
			OLZ <- rectangle(size_OLZ, shape.height) translated_to {(world.shape.width / 2) - size_OLZ/2, world.shape.height / 2};
		}
	}
	
	reflex check_agent_in_OLZ when: simulation_id = 0
	{
		list<movingAgent> agents_in_OLZ <- movingAgent overlapping OLZ;
		//write("agents_in_OLZ : " + agents_in_OLZ);
		
		new_agents_in_OLZ <- agents_in_OLZ - agents_in_OLZ_previous_step;
		//write("new_agents_in_OLZ : " + new_agents_in_OLZ);
		
		agent_leaving_OLZ <- agents_in_OLZ_previous_step - agents_in_OLZ;
		write("agent_leaving_OLZ : " + agent_leaving_OLZ);
		
		agents_in_OLZ_previous_step <- agents_in_OLZ;
		
		
	}
	
	aspect default
	{
		draw ""+simulation_id color: #red;
		draw OLZ color: rgb(#blue, 0.2) border: #black;
		draw "OLZ" at: OLZ.location - 1 font: font('Default', 12, #bold) ;	
		if(simulation_id = 0)
		{
			//draw self.shape /*color: rgb(#blue, 0.2)*/ border: #black;	
		}
	}
}
experiment OLZ_proxy type: proxy 
{
	init
	{
		create simulation with: [simulation_id :: 1]; // new simulation
	}
	
	output
	{
		display "OLZ_proxy" toolbar: false type: 2d
		{
			species worldAgent;
			species cell;
			species movingAgent aspect: classic;
			species followingAgent aspect: classic;
		}
	}
}
