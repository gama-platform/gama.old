/**
 *  Complex Object Loading
 *  Author: Arnaud Grignard
 *  Description: Display complex object (svg,obj and 3ds)
 */

model complexobjectloading   

global {
	geometry shape <- square(10);

	init { 
		create ComplexObject;
	}  
} 

species ComplexObject{
	aspect obj {
		draw geometry (file("./includes/teapot.obj"))  at:{5.0,5.0,0} color:rgb('blue');
	}
}	

experiment Display  type: gui {
	output {
		display ComplexObject type:opengl background:°orange{
			species ComplexObject aspect:obj;				
		}
	}
}
