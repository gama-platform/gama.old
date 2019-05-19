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
			sphere_mat <- material(10, 1); // create a steel-like material
			color <- #grey;
			size <- 10.0;
		}

		create cube_species {
			location <- {70, 20, 5};
			cube_mat <- material(10, 1); // create a steel-like material : maximal reflectivity
			color <- #orange;
			size <- 14.0;
		}

		create sphere_species {
			location <- {30, 55, 13};
			sphere_mat <- material(0, 0); // create a gum-like material : minimal reflectivity
			color <- #red;
			size <- 8.0;
		}

		create cylinder_species {
			location <- {80, 65, 4};
			cylinder_mat <- material(10, 1); // create a steel-like material : maximal reflectivity
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
	material sphere_mat;
	rgb color;
	float size;

	aspect base {
		draw sphere(size) material: sphere_mat color: color;
	}

}

species cube_species {
	material cube_mat;
	rgb color;
	float size;

	aspect base {
		draw cube(size) material: cube_mat color: color;
	}

}

species cylinder_species {
	material cylinder_mat;
	rgb color;
	float size;

	aspect base {
		draw cone3D(size, size * 2) rotated_by (90, {1, 0, 0}) material: cylinder_mat color: color;
	}

}

species board {
	material mat;

	init {
		mat <- material(10, 0.2); // create a varnish-like wood : moderated reflectivity
	}

	aspect base {
		draw rectangle(100, 100) texture: "../includes/wood.jpg";
		draw rectangle(100, 10) at: {50, 5} depth: 10 material: mat texture: "../includes/wood.jpg";
		draw rectangle(100, 10) at: {50, 95} depth: 10 material: mat texture: "../includes/wood.jpg";
		draw rectangle(10, 100) at: {5, 50} depth: 10 material: mat texture: "../includes/wood.jpg";
		draw rectangle(10, 100) at: {95, 50} depth: 10 material: mat texture: "../includes/wood.jpg";
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

		display "OpenGL" type: opengl background:#black ambient_light: 20 {
			light 1 active:button type: point position: {7, 7, 48} color: #white draw_light: true;
			species sphere_species aspect: base;
			species cube_species aspect: base;
			species cylinder_species aspect: base;
			species board aspect: base;
			species lamp aspect: base;
		}

	}

}