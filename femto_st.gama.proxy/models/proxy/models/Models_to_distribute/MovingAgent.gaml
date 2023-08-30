/**
* Name: MovingAgent
* Based on the internal empty template. 
* Author: lucas
* Tags: 
*/


model MovingAgent

species movingAgent skills:[moving]
{	
	rgb col <- #red;
	point target <- any_location_in(world);
	bool display_true <- false;
	
	aspect classic
	{		
		draw line(location, target) color: col;
		if(display_true)
		{
			draw circle(1) color: #blue;
		}else
		{
			draw circle(1) color: col;
		}
		
		draw name color: #black;
	}
	
	reflex move when: target != location
	{
		do goto speed: speed target:target;
	}
	
	reflex target when: target = location
	{
		target <- any_location_in(world.shape);
	}
}

species followingAgent parent: movingAgent
{	
	movingAgent targetAgent;
	
	init
	{
		col <- #black;
		speed <- speed - (speed / 20);
		
		if(targetAgent = nil)
		{
			targetAgent <- one_of(movingAgent);
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
		target <- targetAgent.location;
		do goto speed: speed target:target;
	}
	
	reflex target when: target = location
	{
		target <- targetAgent.location;
	}
}

experiment exp_proxy type: proxy
{
	init
	{
		create movingAgent;
		create followingAgent;
	}
	output{
		display movingDisplay type: 2d
		{
			species movingAgent aspect: classic;
			species followingAgent aspect: classic;
		}
	}
}

experiment exp
{
	init
	{
		create movingAgent;
		create followingAgent;
	}
	output{
		display movingDisplay type: 2d
		{
			species movingAgent aspect: classic;
			species followingAgent aspect: classic;
		}
	}
}