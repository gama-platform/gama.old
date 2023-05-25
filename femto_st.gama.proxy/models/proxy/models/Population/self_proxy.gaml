/**
* Name: self
* Test of statement "self" and "myself" with proxyAgent
* Author: Lucas Grosjean
* Tags: proxy, statement, self
*/

model self_proxy

global
{
	list proxys;
	
	init
	{
		create dummy_proxy;
	}
}

species dummy_proxy skills: [ProxySkill]
{
	
	init
	{
		write("self proxy init " + isProxy(self));
	}
	
	reflex
	{
		write "--------------------------------------";
		write("self proxy " + isProxy(self));
		write("proxy agent[0] " + isProxy(dummy_proxy[0]));
		
		ask dummy_proxy[0]
		{
			write("myself proxy " + isProxy(myself));	
		}
		write "--------------------------------------";
	}
	
	reflex
	{
		let list_of_all_agents <- agents;
		write("agents with self : " + list_of_all_agents);
		let list_of_all_agents_without_self <- agents - self;
		write("agents without self : " + list_of_all_agents_without_self);
		
		
		assert list_of_all_agents != list_of_all_agents_without_self;
	}
}

experiment self_proxy type: proxy 
{
}