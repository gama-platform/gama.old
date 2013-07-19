/**
 *  model1
 *  This model illustrates how to create simple agent and make them move in their environment.
 */

model model1

global{
	geometry shape<-envelope(square(100));
	init{
		create simple_agent number:100;
	}
}

species simple_agent{		
	int size <- rnd(5);
	reflex move{
		location<- any_point_in(world.shape);
	}
	aspect circle{
		draw circle(size) color:rgb("blue");
	}
}

experiment exp1 type:gui{
	output {
		display default_display  {
			species simple_agent aspect:circle;			
		}
	}
}