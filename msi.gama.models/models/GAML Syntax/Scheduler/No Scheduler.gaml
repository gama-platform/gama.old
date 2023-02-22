/**
* Name: schedullingagents
* Author: damienphilippon
* Description: With an empty list given inside the schedules facet of the species, it will not be executed 
				so, nothing will be written inside the console
* Tags: scheduling, execution 
*/

model schedullingagents

global
{
	init
	{
		int cpt <- 0;
		create no_scheduler number:10
		{
			cpt<-cpt+1;
			nb_generated<-cpt;
		}
		write "With an empty list given inside the schedules facet of the species, it will not be executed \n"
			+ "so, nothing will be written inside the console";
	}
}
species no_scheduler schedules:[]
{
	int nb_generated;
	reflex sayHello
	{
		write "hello, i'm "+nb_generated;
	}
}

experiment "No Scheduling" type:gui
{
	
}


