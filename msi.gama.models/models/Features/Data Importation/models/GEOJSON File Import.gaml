/**
* Name: GEOJSON File Loading
* Author:  Alexis Drogoul
* Description: Initialize a set of geometries from a GEOJSON FIle. 
* Tags:  load_file, grid, json, gis
*/

model geojson_loading   

global {
	file geo_file <- geojson_file("../includes/countries.geojson");
	geometry shape <- envelope(geo_file);
	init {
		create countries from: geo_file with: [name::read("name")];
	}
} 

species countries {
	rgb color <- rnd_color(255);
	rgb text_color <- (color.brighter);
	
	init {
		shape <- (simplification(shape,0.01));
	}
	aspect default {
		draw shape color: color depth: 10;
		draw name font: font("Helvetica", 6 + #zoom, #bold) color: text_color at: location + {0,0,12};
	}
}

experiment Display  type: gui {
	output {
		display Countries type: opengl{	
			species countries;			
		}
	}
}
