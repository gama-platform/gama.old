/**
 *  Complex Object Loading
 *  Author: Arnaud Grignard
 *  Description: Display complex object (obj)
 */

model complexobjectloading   

global {
	geometry geom_to_display;
	geometry shape <- square(5);
	init { 
		geom_to_display <- geometry (file("./includes/teapot.obj"));
	}  
} 

experiment Display  type: gui {
	output {
		display ComplexObject type:opengl {
			graphics "geometry" refresh: false{
				draw geom_to_display color: °green at:world.location ;
			}	
		}
	}
}
