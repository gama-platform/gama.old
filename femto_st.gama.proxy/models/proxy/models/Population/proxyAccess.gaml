/**
* Name: proxyAccess
* 
* Test self and index access with proxy
* 
* Author: Lucas Grosjean
* Tags: Proxy
*/


model proxyAccess

import "../Models_to_distribute/MovingAgent.gaml"



global
{
	
	init
	{
		create movingAgent;
		create movingAgent;
		
		write(movingAgent[0]);
		
		ask movingAgent[0]
		{
			write(self);
			write("agents : " + agents);
			write("agents without self : " + (agents - self));
		}
		write("agent without movingAgent[0] : " + (agents - movingAgent[0]));
	}
}

experiment access_proxy type: proxy
{
}

experiment access
{
}