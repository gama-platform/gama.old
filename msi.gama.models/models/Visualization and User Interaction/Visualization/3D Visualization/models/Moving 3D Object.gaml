/**
* Name: OBJ File Drawing and Moving
* Author:  Patrick Taillandier, Tri Nguyen-Huu, Arnaud Grignard 
* Description: Model which shows how to draw a moving objet as a OBJ File and how to apply a 3D rotation to the object
* Tags:  load_file, 3d, skill, obj, moving, goto
*/

model Moving3Dobject

global {
	
	init { 
		create boat;
	}  
} 

species boat skills: [moving]{
	point target <- any_location_in(world);
	reflex move {
		do goto target: target speed:0.5;
		if (target = location) {
			target <- any_location_in(world);
		}	
	}
	aspect obj {
		//we draw an obj file in the aspect apply an initial rotation r0 to give the boat the right orientation and apply a composition of rotation (pitch,roll,yaw) and 
		//the location of a obj file is centroid of the bounding box, so we add with the "at" facet a translated along the z axis to place the boat on the water and not inside
		//the size represents here the max size of the bounding box
		//at last, we dynamically apply a rotation to the boat to make it head in direction of the heading of the agents. 
		pair<float,point> r0 <-  -90::{1,0,0};	
		pair<float,point> pitch <-  5 * cos(cycle*10) ::{1,0,0};
		pair<float,point> roll <- 20*sin(cycle*3)::{0,1,0};
		pair<float,point> yaw <- 1*sin(cycle*7)::{0,0,1};
		draw obj_file("../includes/boat/fishing-boat.obj", rotation_composition(r0,pitch,roll,yaw)) at: location + {0,0,9} size: 5 rotate: heading + 90;
	}	
}	


experiment Display  type: gui {
	output synchronized: true {
		display ComplexObject type: 3d background: #black axes:false{
			camera 'default' location: {-34.826,115.0892,54.4789} target: {50.0,50.0,0.0};
			light #ambient intensity: 100;
			species boat aspect:obj;	
			graphics world transparency: 0.4 { 
				draw world depth: 5 texture:("../images/water.gif") ;
			}
		}
	}
}

