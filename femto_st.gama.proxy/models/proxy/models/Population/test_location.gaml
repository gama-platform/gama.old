/**
* Name: test_location
* 
* Test of location with proxy
* 
* Author: Lucas Grosjean
* Tags: Proxy
*/

model test_location

import "../Models_to_distribute/MovingAgent.gaml"

global skills: [ProxySkill]
{		
	geometry shape <- rectangle(80,80);
	geometry circ;
	
	init
	{		
		create movingAgent;
		create movingAgent;
		circ <- circle(15); 
	}
	
	reflex
	{
		let inside_moving <- (movingAgent inside circ);
		let outside_moving <- movingAgent where not (each intersects circ);
		
		write("movingAgent inside circ : " + inside_moving);
		write("movingAgent outside circ : " + outside_moving);
	}
}
experiment test_inside_proxy type: proxy 
{
	
	output{
		display inside_proxy type: 2d
		{
			graphics ""
			{
				draw circ;
			}
			species movingAgent aspect: classic;
		}
	}
}

experiment test_inside
{
	
	output{
		display inside type: 2d
		{
			graphics ""
			{
				draw circ;
			}
			species movingAgent aspect: classic;
		}
	}
}
