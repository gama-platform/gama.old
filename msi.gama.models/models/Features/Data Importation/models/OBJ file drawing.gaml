/**
* Name: OBJ File to Geometry
* Author:  Arnaud Grignard
* Description: Model which shows how to use a OBJ File to draw a complex geometry. The geometry is simply used, in this case, to draw the agents.
* Tags:  load_file, 3d, skill, obj
*/


model obj_drawing   

global {
	geometry shape <- square(40);

	init { 
		create object number: 30;
	}  
} 

species object skills: [moving]{
	rgb color <- rgb(rnd(255),rnd(255),rnd(255));
	int size <- rnd(10) + 1;
	int rot <- 1000 + rnd(1000);
	reflex m when: every(100#cycles) {
		do wander amplitude: 30.0 speed: 0.001;
	}
	aspect obj {
		draw obj_file("../includes/teapot.obj") color: color size: size rotate: cycle/rot::{0,1,0} ;
	}
}	

experiment Display  type: gui {
	output {
		display ComplexObject type: opengl background:°orange{
			species object aspect:obj;				
		}
	}
}
