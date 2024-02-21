/**
* Name: Save to Geotiff
* Author: Patrick Taillandier
* Description: This is a model that shows how to save a grid inside a Geotiff File to reuse it later or to keep it.
* Tags: save_file, tiff, grid
*/

model SavetoGeotiff

global {
	init {	 
		//save grid "grid_value" attribute into the geotiff file.
		save cell to:"../results/grid.tif" format:"geotiff";
	}
}

//Grid that will be saved in the Geotiff File
grid cell width: 50 height: 50 {
	float grid_value <- self distance_to world.location * 2;
	rgb color <- rgb(255 * (1 - grid_value / 50), 0,0);
}

experiment main type: gui {
	output {
		display map {
			grid cell border: #black;
		}
	}
}
