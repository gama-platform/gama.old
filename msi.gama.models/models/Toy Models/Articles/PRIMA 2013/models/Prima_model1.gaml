/**
* Name: Prima 1
* Author: 
* Description: This model shows how to create agent and make them move randomly in the world.
* 	Some agents are infected, and others can gain the infection if they are in a certain range.
* Tags: skill
*/

model SI_city

global{
	geometry shape<-envelope(square(500));
	
	//Creation of the people agents
	init{
		create people number:1000;
	}
}
//People species with agents moving and can be infected
species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	//Make the agent wander at each step with a certain speed.
	reflex move{
		do wander;
	}
	//Infect the agent if it is not already infected, and according to the infected people in a range
	reflex infect when: is_infected{
		ask people at_distance 10 {
			if flip(0.01) {
				is_infected <- true;
			}
		}
	}
	aspect circle{
		draw circle(5) color:is_infected ? #red : #green;
	}
}

experiment main_experiment type:gui{
	output {
		display map  {
			species people aspect:circle;			
		}
	}
}