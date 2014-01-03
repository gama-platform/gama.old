/**
 *  Complex Object Loading
 *  Author: Arnaud Grignard
 *  Description: Display complex object (svg,obj and 3ds)
 */

model complexobjectloading   

global {

	init { 
		create ComplexObject;
	}  
} 

species ComplexObject{
	aspect obj {
		draw geometry (file("./includes/teapot.obj"))  at:{0,0,0} color:rgb('blue') border:rgb('green') size:100;
	}
}	

experiment Display  type: gui {
	output {
		display ComplexObject type:opengl {
			species ComplexObject aspect:obj;				
		}
	}
}
