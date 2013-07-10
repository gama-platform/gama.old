/**
 *  model1
 *  Author: hqnghi
 *  Description: 
 */

model model1

/* Insert your model definition here */

global{
	geometry shape<-envelope(square(100));
	init{
		create A number:1000;
	}
}

entities{
	species A{		
		rgb my_color<-rgb(rnd(255),rnd(255),rnd(255));
		reflex moving_around{
			location<- any_point_in(world.shape);
		}
		aspect my_aspect{
			draw circle(1) color:my_color;
		}
	}
}

experiment exp1 type:gui{
	output {
		display default_display refresh_every:1{
			species A aspect:my_aspect;			
		}
	}
}