/**
* Name: self
* Test of statement "self" and "myself" with proxyAgent
* Author: Lucas Grosjean
* Tags: proxy, statement, self
*/

model self_proxy

import "../Models_to_distribute/MovingAgent.gaml"

global skills: [ProxySkill]
{
	list<agent> list_of_all_agents_without_self;
	list<agent> list_of_all_agents_without_myself;
	
	init
	{
		create movingAgent;
		create movingAgent;
		create movingAgent;
		create movingAgent;
		
		ask movingAgent
		{
			write("agents : " + agents);
			list_of_all_agents_without_self <- (agents - self);
			write("agents without self : " + list_of_all_agents_without_self);
			write("self dummy_proxy : " + self.name);
			write("dummy_proxy self  isProxy " + myself.isProxy(self));	
		}	
			
		ask movingAgent[0]
		{
			write("dummy_proxy myself isProxy " + myself.isProxy(self));	
			ask movingAgent
			{
				list_of_all_agents_without_myself <- (agents - myself);
				write("list_of_all_agents_without_myself : " + list_of_all_agents_without_myself);
			}
		}
		
		assert agents != list_of_all_agents_without_myself;
		assert agents != list_of_all_agents_without_self;
	}
}

experiment self_proxy type: proxy 
{
}

experiment self_exp
{
}