/**
* Name: Save to Georeferenced image
* Author: Patrick Taillandier
* Description: This is a model that shows how to save a grid inside a georeferenced png file to reuse it later or to keep it.
* Tags: save_file, image, grid
*/

model SavetoGoereferencedImage

global {
	init {	 
		//save grid "grid_value" attribute into the png file - a world file grid.pgw will be created as well.
		save cell to:"../results/grid.png" type:"image";
	}
}

//Grid that will be saved in the ASC File
grid cell width: 50 height: 50 {
	float grid_value <- self distance_to world.location;
	rgb color <- rgb(255 * (1 - grid_value / 50), 0,0);
}

experiment main type: gui {
	output {
		display map {
			grid cell lines: #black;
		}
	}
}
