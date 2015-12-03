/**
 *  ascimport
 *  Author: Patrick Taillandier
 *  Description: Show how to import an asc file (ESRI ASCII file) to initialize a grid
 */

model ascimport

global {
	//definiton of the file to import
	file grid_file <- file('../includes/hab10.asc') ;
	
	//computation of the environment size from the geotiff file
	geometry shape <- envelope(grid_file);	
}



//definition of the grid from the geotiff file
grid cell file: grid_file{
	init {
		color<- grid_value = 0.0 ? #black  : (grid_value = 1.0  ? #green :   #yellow);
	}
}

experiment gridloading type: gui {
	output {
		display test {
			grid cell lines: #black;
		}
	} 
}


