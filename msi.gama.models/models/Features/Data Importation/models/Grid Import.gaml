/**
 *  gridLoading
 *  Author: patricktaillandier
 *  Description: 
 */

model gridloading

global {
	file shape_file <- file('../includes/test.shp') ;
	file grid_file <- file('../includes/hab10.asc') ;
	geometry shape <- envelope(grid_file);	
	
	init {
		create object from: shape_file;    
	} 
	
}


grid cell file: grid_file{
	init {
		color<- grid_value = 0.0 ? #black  : (grid_value = 1.0  ? #green :   #yellow);
	
	}
}
species object  {
	aspect base {
		draw geometry: shape color: #red ; 	
	}
} 


experiment gridloading type: gui {
	output {
		display test {
			grid cell lines: #black;
			species object aspect: base;
		}
	} 
}
