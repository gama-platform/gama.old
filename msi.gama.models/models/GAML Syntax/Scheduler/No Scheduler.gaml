/**
* Name: schedullingagents
* Author: damienphilippon
* Description: With an empty list given inside the schedules facet of the species, it will not be executed 
				so, nothing will be written inside the console
* Tags: Tag1, Tag2, TagN
*/

model schedullingagents

/* Insert your model definition here */
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
		write "With an empty list given inside the schedules facet of the species, it will not be executed 
				so, nothing will be written inside the console";
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

experiment example_no_scheduler type:gui
{
	
}


