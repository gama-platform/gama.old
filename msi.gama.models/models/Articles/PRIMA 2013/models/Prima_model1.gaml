/**
 *  model1
 *  This model illustrates how to create simple agent and make them move in their environment.
 */

model SI_city

global{
	geometry shape<-envelope(square(500));
	init{
		create people number:1000;
	}
}

species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	reflex move{
		do wander;
	}
	reflex infect when: is_infected{
		ask people at_distance 10 {
			if flip(0.01) {
				is_infected <- true;
			}
		}
	}
	aspect circle{
		draw circle(5) color:is_infected ? rgb("red") : rgb("green");
	}
}

experiment main_experiment type:gui{
	output {
		display map  {
			species people aspect:circle;			
		}
	}
}