/**
* Name: OBJ File Drawing and Moving
* Author:  Patrick Taillandier, Tri Nguyen-Huu, Arnaud Grignard 
* Description: Model which shows how to draw a moving objet as a OBJ File and how to apply a 3D rotation to the object
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
		do goto target: target speed:0.5;
		if (target = location) {
			target <- any_location_in(world);
		}	
	}
	aspect obj {
		//we draw an obj file in the aspect, the second argument (90::{-1,0,0}) is used to apply an initial rotation (90° along the {-1,0,0} vector)) to give the boat the right orientation
		//the location of a obj file is centroid of the bounding box, so we add with the "at" facet a translated along the z axis to place the boat on the water and not inside
		//the size represents here the max size of the bounding box
		//at last, we dynamically apply a rotation to the boat to make it head in direction of the heading of the agents.  
//	    draw obj_file("../includes/boat/fishing-boat.obj", 90+ 5 * cos(cycle*10) ::{-1,0,0}) at: location + {0,0,9} size: 5 rotate: heading + 90;
		
		
		int yaw<-10;
		int picth<-20;
		int raw<-30;
		
		
		pair<float,point> r1 <-  90+ 5 * cos(cycle*10) ::{-1,0,0};
		pair<float,point> r2 <- 20*sin(cycle*7)::{0,1,0};
		draw obj_file("../includes/boat/fishing-boat.obj", compose_rotation(r1,r2)) at: location + {0,0,9} size: 5 rotate: heading + 90;
		
		
		draw obj_file("../includes/boat/fishing-boat.obj", 90+ 5 * cos(cycle*10) ::{-1,0,0}) at: location + {0,0,9} size: 5 rotate: heading + 90;
		
	    //draw obj_file("../includes/boat/fishing-boat.obj", 90+ 5 * cos(cycle*10) ::{-1,0,0}) at: location + {0,0,9} size: 5 rotate: heading + 90;
		
	}
	
//	reflex test{
//		pair<float,point> r1 <- (90::{1,0,0});
//		pair<float,point> r2 <- (90::{0,1,0});
////		pair<float,point> q2 <- rotation_to_quaternion(r1);
//	//	write q1;
//	//	write r1;
//	//	write q2;
//	//	pair<float,point> r3 <- quaternion_to_rotation(compose(rotation_to_quaternion(r1),rotation_to_quaternion(r2)));
//	//	write r3;
//		
//	}
	
	
	pair<float,point> compose_rotation(pair<float,point> r1, pair<float,point> r2){
		return quaternion_to_rotation(quaternion_product(rotation_to_quaternion(r1),rotation_to_quaternion(r2)));
	}	
	
	pair<float,point> quaternion_product(pair<float,point> r1, pair<float,point> r2){
		float a1 <- r1.key;
		float b1 <- r1.value.x;
		float c1 <- r1.value.y;
		float d1 <- r1.value.z;
		float a2 <- r2.key;
		float b2 <- r2.value.x;
		float c2 <- r2.value.y;
		float d2 <- r2.value.z;		
		
		float a3 <- a1*a2 - b1*b2 -c1*c2 - d1*d2;
		float b3 <- a1*b2+b1*a2+c1*d2-d1*c2;
		float c3 <- a1*c2+c1*a2-b1*d2+d1*b2;
		float d3 <- d1*a2+a1*d2+b1*c2-c1*b2;
		 
		return a3::{b3,c3,d3};
	}
	
	pair<float,point> rotation_to_quaternion(pair<float,point> r){
		// transform a rotation in an equivalent unitary quaternion
		return cos(r.key/2)::(r.value / norm(r.value) *sin(r.key/2));
	}	
	
	pair<float,point> quaternion_to_rotation(pair<float,point> q){
		// transform a unitary quaternion in a rotation
		return 2 * acos(q.key)::q.value;
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

