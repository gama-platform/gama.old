/**
* Name: Save to Ascii
* Author: Patrick Taillandier
* Description: This is a model that shows how to save a grid inside a ASCII File to reuse it later or to keep it.
* Tags: save_file, asc, grid
*/

model SavetoAsc

global {
	init {	
		//save grid "grid_value" attribute into the asc file.
		save cell to:"../results/grid.asc";
	}
}

//Grid that will be saved in the ASC File
grid cell width: 50 height: 50 {
	float grid_value <- self distance_to world.location;
	rgb color <- rgb(255 * (1 - grid_value / 50), 0,0);
}

experiment main type: gui {
	output {
		display map type:2d antialias:false{
			grid cell border: #black;
		}
	}
}
