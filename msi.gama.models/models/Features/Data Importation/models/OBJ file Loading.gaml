/**
* Name: OBJ File to Geometry
* Author:  Arnaud Grignard
* Description: Model which shows how to use a OBJ, SVG or 3DS File to create a geometry
* Tags : :  Import Files, 3D Display
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
		//draw the geometry of the agent as the obj contained in the file
		draw geometry (file("../includes/teapot.obj"))  at:{5.0,5.0,0} color:#blue;
	}
}	

experiment Display  type: gui {
	output {
		display ComplexObject type:opengl background:°orange{
			species ComplexObject aspect:obj;				
		}
	}
}
