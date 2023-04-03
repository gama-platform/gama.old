/**
* Name: schedullingagents
* Author: damienphilippon
* Description: If the schedules facet is not given, it will have its default value which is using the list of the species.
			As the list of the species is created the same way the agents are created, it should be ordered. So the console
			will be filled with messages with the agent with the number used to index them that should be ordered in a
			forward sort with without agent writing first
* Tags: 
*/

model schedullingagents

/* Insert your model definition here */
global
{
	init
	{
		int cpt <- 0;
		create without_scheduler number:10
		{
			cpt<-cpt+1;
			nb_generated<-cpt;
		}
		cpt<-0;
		create scheduler_species number:10
		{
			cpt<-cpt+1;
			nb_generated<-cpt;
		}
		write "If the schedules facet is not given, it will have its default value which is using the list of the species.\n"
			+ "As the list of the species is created the same way the agents are created, it should be ordered. So the console \n"
			+ "will be filled with messages with the agent with the number used to index them that should be ordered in a\n"
			+ "forward sort with without agent writing first";
	}
}
species without_scheduler
{
	int nb_generated;
	reflex sayHello
	{
		write "hello, i'm without scheduler "+nb_generated;
	}
}

species scheduler_species schedules:scheduler_species
{
	int nb_generated;
	reflex sayHello
	{
		write "hello, i'm scheduled "+nb_generated;
	}
}
experiment example_default_scheduler type:gui
{
	
}


