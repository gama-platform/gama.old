/**
 *  model1
 *  This model illustrates how to create simple agent and make them move in their environment.
 */

model model1

global{
	geometry shape<-envelope(square(500));
	init{
		create people number:500;
	}
}

species people skills:[moving]{		
	int size <- 5;
	float speed <- 5.0 + rnd(5);
	reflex move{
		do wander;
	}
	aspect circle{
		draw circle(size) color:rgb("green");
	}
}

experiment main_experiment type:gui{
	output {
		display map  {
			species people aspect:circle;			
		}
	}
}