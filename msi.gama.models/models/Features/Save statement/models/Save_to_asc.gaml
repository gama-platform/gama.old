/**
 *  SavetoAsc
 *  Author: Patrick Taillandier
 *  Description: Show how to save a grid into an asc file
 */

model SavetoAsc

global {
	init {	
		//save grid "grid_value" attribute into the asc file.
		save cell to:"../results/grid.asc" type:"asc";
	}
}

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
