/**
* Name: Save to Geotiff
* Author: Patrick Taillandier
* Description: This is a model that shows how to save a grid inside a georefrenced png file to reuse it later or to keep it.
* Tags: save_file, png, grid
*/

model SavetoGeotiff

global {
	shape_file parcels <- shape_file("../includes/parcels.shp");
	geometry shape <- envelope(parcels);
	init {	 
		ask cell {
			if not empty(parcels overlapping self) {
				color <- #blue;
			}
		}
		//save grid "grid_value" attribute into the georefrenced png file.
		save cell to:"../results/grid.png" type:image;
	}
}

grid cell width: 50 height: 50 ;

experiment main type: gui {
	output {
		display map {
			grid cell border: #black;
		}
	}
}
