/**
 *  Complex Object Loading
 *  Author: Arnaud Grignard
 *  Description: Display complex object (svg,obj and 3ds)
 */

model complexobjectloading   

global {
	
	geometry shape <- square(100);
	geometry svgfile <- geometry(svg_file("./includes/teapot.svg", {50,50}));
	geometry objfile <- geometry (file("./includes/teapot.obj"));
	geometry threedsfile <- geometry (file("./includes/teapot.3ds"));
		
	init { 
		create ComplexObject number:1;
	}  
} 

species ComplexObject{

	aspect svg {
		draw svgfile at: location;
	}
	
	aspect obj {
		draw objfile at: location ;
	}
	
	aspect threeds {
		draw threedsfile at: location ;
	}
}	

experiment Display  type: gui {
	output {
		display Poincare refresh_every: 1  type:opengl{
			species ComplexObject aspect:svg;
			species ComplexObject aspect:obj;	
			species ComplexObject aspect:threeds;								
		}
	}
}
