/**
* Name: ESRI ASCII to grid model
* Author:  Patrick Taillandier
* Description: Model which shows how to initialize a grid using a ESRI ASCII file
* Tags: grid, load_file, asc
*/

model ascimport

global {
	//definiton of the file to import
	file grid_data <- file('../includes/hab10.asc') ;
	
	//computation of the environment size from the geotiff file
	geometry shape <- envelope(grid_data);	
}



//definition of the grid from the asc file: the width and height of the grid are directly read from the asc file. The values of the asc file are stored in the grid_value attribute of the cells.
grid cell file: grid_data{
	init {
		color<- grid_value = 0.0 ? #black  : (grid_value = 1.0  ? #green :   #yellow);
	}
}

experiment gridloading type: gui {
	output {
		display "As DEM" type: 3d axes:false{
			grid cell border: #gray elevation: self.grid_value * 300 ;
		}
		
		display "As 2D grid"  type: 2d {
			grid cell border: #black;
		}
	} 
}


