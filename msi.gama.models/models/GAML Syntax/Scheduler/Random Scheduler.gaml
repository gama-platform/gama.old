/**
* Name: randomscheduler
* Author: damienphilippon
* Description: To randomize the way agents of a same species are executed, it is possible to use the operator shuffle in the schedules facet. In this case, agents
			will write their number, but they are executed in a random way
* Tags: scheduling, execution, random
*/

model randomscheduler

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
		write "To randomize the way agents of a same species are executed,\n" 
			+ "it is possible to use the operator shuffle in the schedules facet. \n"
			+ "In this case, agents will write their number,\n"
			+ "but they are executed in a random way";
	}
	
	reflex write_new_step
	{
		write "NEW STEP";
	}
}
species no_scheduler schedules:shuffle(no_scheduler)
{
	int nb_generated;
	reflex sayHello
	{
		write "hello, i'm "+nb_generated;
	}
}

experiment "Schedule" type:gui
{
	
}
