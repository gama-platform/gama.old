/**
* Name: managerscheduler
* Author: damienphilippon
* Description: This model shows how a species can manage other species execution process. In the facet schedules of the 
			manager species, the list of the agents of the species B and A has been given. B agents will be executed first and then A agents.
			An important point is that the manager is not executed, since it is not scheduled anywhere, but it is still scheduling other species agents.
			This is shown with the reflex write_new_step belonging to the manager species that is not executed, not writing its text to the console.
* Tags: scheduling, execution
*/

model managerscheduler

/* Insert your model definition here */

global
{
	init
	{
		create manager;
		int cpt <- 0;
		create A number:10
		{
			cpt <- cpt+1;
			nb_generated<-cpt;
		}
		cpt<-0;
		create B number:10
		{
			cpt <- cpt+1;
			nb_generated<-cpt;
		}
		
		write 	"This model shows how a species can manage other species execution process. In the facet schedules of the \n"
			+	"manager species, the list of the agents of the species B and A has been given. B agents will be executed first and then A agents.\n"
			+	"An important point is that the manager is not executed, since it is not scheduled anywhere, but it is still scheduling other species agents.\n"
			+	"This is shown with the reflex write_new_step belonging to the manager species that is not executed, not writing its text to the console.";
	}
}

species manager schedules:(B+A)
{
	reflex write_new_step
	{
		write "-----NEW STEP BY MANAGER-----";
	}
}

species A schedules:[]
{
	int nb_generated;
	reflex present_itself
	{
		write "I'm A "+nb_generated;
	}
}
species B schedules:[]
{
	int nb_generated;
	reflex present_itself
	{
		write "I'm B "+nb_generated;
	}
	
}
experiment "Schedule" type:gui
{
	
}