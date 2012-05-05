/**
 * 
 *  testSIG
 *  Author: arnaudgrignard
 *  Description: 
 */

model testSIG
global {
	file shape_file_in <- file('../includes/city/building.shp') ;
	graph the_graph;
	
	init {    
		create species: object from: shape_file_in ;	
		let the_object_geom type: geometry <- (first (object as list)).shape;
		loop geom over: (the_object_geom.geometries) {
			create species: object_simple {
				set shape value: geom;
			}  
		}
	}
}  
environment bounds: shape_file_in ;   
entities {
	species object  {
		aspect default {
			draw shape: geometry color: 'red' ;
		}
		reflex when:flip(0.1) {
			do die;
		}
	}
	species object_simple  {
		rgb color <- rgb([rnd(255),rnd(255),rnd(255)]);
		aspect default {
			draw shape: geometry color: color ;
		}
	}
}
output {
	display objects_display type: opengl{
		species object aspect: default ;
		species object_simple aspect: default ;
	}

}
