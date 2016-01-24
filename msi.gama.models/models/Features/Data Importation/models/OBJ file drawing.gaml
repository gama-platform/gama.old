/**
* Name: OBJ File to Geometry
* Author:  Arnaud Grignard
* Description: Model which shows how to use a OBJ, SVG or 3DS File to draw a complex geometry. The geometry is simply used, in this case, to draw the agents.
* Tags :  Import Files, 3D Display
*/


model obj_drawing   

global {
	geometry shape <- square(10);

	init { 
		create object number: 20;
	}  
} 

species object{
	aspect obj {
		draw file("../includes/teapot.obj") color:#red size: 3 ;
	}
}	

experiment Display  type: gui {
	output {
		display ComplexObject type:opengl background:°orange{
			species object aspect:obj;				
		}
	}
}
