/**
* Name: SocketExample
* Author: damienphilippon
* Description: A simple model showing how to create a model using Client Server architecture. 
* Tags: Tag1, Tag2, TagN
*/

model SocketExample

/* Insert your model definition here */

global
{
	string ip_server<-"127.0.0.1";
	int port_serveur<-3001;
	init
	{
		create server number:1
		{
			port<-port_serveur;
			do open_socket();
			write 'Server ON';
		}
		create client number:1
		{
			ip_server<-ip_server;
			port<-port_serveur;
			pseudo<-"Client";
			myClientID<- connect_server();
			write 'Client ON';
		}
	}
}
species client skills:[socket]
{
	string pseudo;
	string myClientID;
	reflex update when:(cycle>1)
	{
		do send_to_server msg:pseudo+": Hi it's me "+pseudo;
		string msg<-listen_server();
		if(msg!=nil)
		{
			write msg;
		}
	}	
}
species server skills:[socket]
{
	reflex update when:(cycle>1)
	{
		loop aClient over: clients
		{
			string msg <- get_from_client (cID::aClient) ;
			if(msg!=nil)
			{	
				write msg;
				do send_to_client cID:aClient msg:"Server: Hi I'm the server";
			}
		}
	}
}
experiment socket_example type:gui
{
	
}