/**
* Name: Advanced 3D properties : specular light
* Author: Julien Mazars
* Description: Model presenting how to add materials to the objects, tunning the two properties of the material :
* the damping factor (the larger this value is, the larger the reflection zone will be) and the reflectivity factor (between 
* 0 and 1, the ratio of light that is reflected). Notice that in order to run this model, you need to have a graphical card 
* and the facet "use_shader" activated in your display definition.
* Tags: 3d, light
*/
model specular_light

global {
	
	bool button;

	init {
		create sphere_species {
			location <- {50, 50, 15};
			color <- #grey;
			size <- 10.0;
		}

		create cube_species {
			location <- {70, 20, 5};
			color <- #orange;
			size <- 14.0;
		}

		create sphere_species {
			location <- {30, 55, 13};
			color <- #red;
			size <- 8.0;
		}

		create cylinder_species {
			location <- {80, 65, 4};
			color <- #darkolivegreen;
			size <- 8.0;
		}

		create board {
			location <- myself.location;
		}

		create lamp {
			location <- {0, 0, 0};
		}

	}

}

species sphere_species {
	rgb color;
	float size;

	aspect base {
		draw sphere(size) color: color;
	}

}

species cube_species {
	rgb color;
	float size;

	aspect base {
		draw cube(size) color: color;
	}

}

species cylinder_species {
	rgb color;
	float size;

	aspect base {
		draw cone3D(size, size * 2) rotated_by (90, {1, 0, 0}) color: color;
	}

}

species board {


	aspect base {
		draw rectangle(100, 100) texture: "../includes/wood.jpg";
		draw rectangle(100, 10) at: {50, 5} depth: 10 texture: "../includes/wood.jpg";
		draw rectangle(100, 10) at: {50, 95} depth: 10 texture: "../includes/wood.jpg";
		draw rectangle(10, 100) at: {5, 50} depth: 10 texture: "../includes/wood.jpg";
		draw rectangle(10, 100) at: {95, 50} depth: 10 texture: "../includes/wood.jpg";
	}

}

species lamp {

	aspect base {
		draw cylinder(2, 50) color: #darkgrey;
		draw cylinder(10, 10) rotate: (45::{1, 1, 1}) at: {5, 5, 50} color: #darkgrey;
	}

}

experiment specular_light type: gui {
	parameter "turn on/off the ligth" var:button init:true;
	output {
		layout #split;

		display "OpenGL" type: 3d background:#black  {
			camera 'default' location: {-50.692,109.4647,74.8304} target: {60.563,33.0012,0.0};
			light #ambient intensity: 20;
			light #default active:button type: #point location: {7, 7, 48} intensity: #white show: true;
			species sphere_species aspect: base;
			species cube_species aspect: base;
			species cylinder_species aspect: base;
			species board aspect: base;
			species lamp aspect: base;
		}

	}

}