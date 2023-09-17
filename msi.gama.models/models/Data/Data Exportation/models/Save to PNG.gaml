/**
* Name: Save to Geotiff
* Author: Patrick Taillandier
* Description: This is a model that shows how to save a grid inside a georefrenced png file to reuse it later or to keep it.
* Tags: save_file, png, grid
*/

model SavetoGeotiff

global {
	shape_file buildings <- shape_file("../includes/building.shp");
	geometry shape <- envelope(buildings);
	init {	 
		ask cell {
			if not empty(buildings overlapping self) {
				color <- #blue;
			}
		}
		//save grid "grid_value" attribute into the georefrenced png file.
		save cell to:"../results/grid.png";
	}
}

grid cell width: 50 height: 50 ;

experiment main type: gui {
	output {
		display map type:2d antialias:false {
			grid cell border: #black;
		}
	}
}
