/**
* Name: OBJ File Drawing and Moving
* Author:  Patrick Taillandier
* Description: Model which shows how to draw a moving objet as a OBJ File.
* Tags:  load_file, 3d, skill, obj, moving, goto
*/


model Moving3Dobject


global {
	
	init { 
		create boat number: 1;
	}  
} 

species boat skills: [moving]{
	point target <- any_location_in(world);
	reflex move {
		do goto target: target;
		if (target = location) {
			target <- any_location_in(world);
		}	
	}
	aspect obj {
		//we draw an obj file in the aspect, the second argument (90::{-1,0,0}) is used to apply an initial rotation (90° along the {-1,0,0} vector)) to give the boat the right orientation
		//the location of a obj file is centroid of the bounding box, so we add with the "at" facet a translated along the z axis to place the boat on the water and not inside
		//the size represents here the max size of the bounding box
		//at last, we dynamically apply a rotation to the boat to make it head in direction of the heading of the agents.  
		draw obj_file("../includes/boat/boat.obj", 90::{-1,0,0}) at: location + {0,0,6} size: 10 rotate: heading + 180;
	}
}	

experiment Display  type: gui {
	output {
		display ComplexObject type: opengl ambient_light: 100 background: #black synchronized: true{
			species boat aspect:obj;	
			graphics world transparency: 0.4{ 
				draw world depth: 5  texture:("../images/water2.gif") ;
			}			
		}
	}
}