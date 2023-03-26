/**
* Name: Camera Position
* Author: Arnaud Grignard & Alexis Drogoul
* Description: A model presenting how to manipulate cameras in a 3D display
* Tags: 3d
*/
model camera_locationition

global {

	init {
		create object;
	}

}

species object skills: [moving] {

	reflex move {
		do wander amplitude: 20.0 speed: 1.0;
	}

	aspect default {
		draw sphere(5) at: location color: #white border: #gray;
	}

}

experiment Display type: gui autorun: true {
	float w -> simulation.shape.width; 
	float h -> simulation.shape.height;
	point p -> first(object).location;
	float factor <- 1.0;
	parameter "Shared zoom" var: factor min: 0.01 max: 10.0;
	float minimum_cycle_duration <- 0.01;
	output {
		layout #split;
		display shared type: 3d virtual: true {
			image "../includes/wood.jpg";
			species object;
		}
		display "Changing every 500" parent: shared camera: [#from_up_front, #from_up_left, #from_up_right, #from_above, #from_front, #from_left, #from_right] at ((cycle / 500) mod 7) {
		}
		display "Fixed location" parent: shared camera: "fixed" {
			camera "fixed" locked: true location: {w / 2, h * 2, w / factor} target: {w / 2, h / 2, 0} dynamic: true;
		}
		display "Dynamic location" parent: shared camera: #default {
			camera #default location: {w * cos(cycle), w * sin(cycle), w / factor} target: {w / 2, h / 2, 0} dynamic: true;
		}
		display "Follow object" parent: shared {
			camera #default target: p distance: 150 / factor location: #from_above dynamic: true;
		}
		display "First person" type: opengl{
			image "../includes/wood.jpg";
			camera #default dynamic: true location: {int(first(object).location.x), int(first(object).location.y), 5/factor} target:
			{cos(first(object).heading) * first(object).speed + int(first(object).location.x), sin(first(object).heading) * first(object).speed + int(first(object).location.y), 5/factor};
		}
		display "Camera & rotation" parent: shared {
			rotation angle: 1.0 axis: {0,1,0} dynamic: true;
			camera #default location: #from_right distance: 40 / factor target: {w, p.y, 0} dynamic: true;
		}
		display "Isometric" parent: shared {
			camera #default location: #isometric target: {p.x, p.y, 0} dynamic: true;
		}


	}

}