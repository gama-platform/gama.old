/**
 * 
 *  Author: Patrick Taillandier
 *  Description: Show how to import a geotiff file to initialize a grid
 */

model geotiffimport

global {
	//definiton of the file to import
	file grid_file <- file('../includes/bogota_grid.tif') ;
	
	//computation of the environment size from the geotiff file
	geometry shape <- envelope(grid_file);	
	
	float max_value;
	float min_value;
	init {
		max_value <- cell max_of (each.grid_value);
		min_value <- cell min_of (each.grid_value);
		ask cell {
			int val <- int(255 * ( 1  - (grid_value - min_value) /(max_value - min_value)));
			color <- rgb(val,val,val);
		}
	}
}

//definition of the grid from the geotiff file
grid cell file: grid_file;

experiment show_example type: gui {
	output {
		display test {
			grid cell lines: #black;
		}
	} 
}
