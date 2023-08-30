/**
* Name: dispose
* 
* Test how proxy agent are disposed
* 
* Author: Lucas Grosjean
* Tags: HPC, Proxy
*/


model dispose

import "../Models_to_distribute/MovingAgent.gaml"


global skills: [ProxySkill]
{
	init
	{
		create movingAgent;
		write("nb : " + length(movingAgent));
		
		ask movingAgent
		{
			do die;
		}
		
		write("number agents : " + length(movingAgent));
		assert(length(movingAgent) = 0);
		
		create movingAgent;
		write("number agents : " + length(movingAgent));
		
		assert(length(movingAgent) = 1);
	}
	
	reflex when: cycle = 10
	{
	 	ask experiment
	 	{	 		
	 		do die;	
	 	}
	}
}
experiment dispose2
{
}
experiment dispose type: proxy 
{
}