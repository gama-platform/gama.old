/**
* Name: proxyUpdate
* move an agent across different simulation + updates all his distant agent
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation
*/

model proxyUpdate

global
{
	int simulation_id <- 0;
	movingAgent globalMoving;
	bool possess_agent <- false;
	int neighbor_id <- 1;
	int number_of_other_simulation <- 10;
	
	bool display_true <- false;
	 
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
			create movingAgent with: [ location :: any_location_in(world), target :: any_location_in(world)] returns: globalAgent;
			globalMoving <- globalAgent[0];

			create followingAgent with: [target :: globalMoving.location, targetAgent:: globalMoving, location :: any_location_in(world)];	

			possess_agent <- true;
			neighbor_id <- 1;
			display_true <- true;
			
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
			
			if(request = "update" and globalMoving != nil)
			{			
				do updateProxy(content["agent"]);
				
			}else if(request = "migrate")
			{		
				do migrateAgent(content["agent"]);
				globalMoving <- movingAgent[0];
				possess_agent <- true;
				do send to: "comm" + content["sender"] contents: ["type" :: "received"];
				display_true <- true;
				
			}else if(request = "received")
			{
				do setAgentAsDistant(globalMoving);
				display_true <- false;
			}
		}	
	}
	
	reflex when: (cycle mod 10 = 0 and cycle != 0) and possess_agent
	{
		do send to: "comm" + one_of(all_neighbors) contents: ["agent" :: globalMoving, "type" :: "migrate", "sender" :: simulation_id];
		possess_agent <- false;
	}
	
	reflex when: possess_agent
	{
		loop i over: all_neighbors
		{
			write("globalMoving for " + i + " : " + globalMoving);
			do send to: "comm" + i contents:["type" :: "update", "agent" :: globalMoving];	
		}
	}
}
experiment proxyUpdate type: proxy 
{
	init
	{
		loop simulation_id_index from: 1 to: number_of_other_simulation {    
			create simulation with: [simulation_id :: simulation_id_index]; // new simulation
		}
	}
	
	output synchronized: true
	{
		display "proxyUpdate" toolbar: false type: 2d 
		{
			species movingAgent aspect: classic;
			species followingAgent aspect: classic;
		}
	}
}
