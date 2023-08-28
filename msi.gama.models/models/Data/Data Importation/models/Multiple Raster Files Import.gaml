/**
* Name: importationraster
* Author: Patrick Taillandier
* Description: Shows how to use several grid files to initialize a grid 
* Tags: load_file, gis, 3d, dem, tif, asc
*/

model importationraster

global {
	//the two grid files that we are going to use to initialize the grid
	file dem_file <- file("../includes/mnt.asc");
	file land_cover_file <- file("../includes/land-cover.tif");
	
	//we use the dem file to initialize the world environment
	geometry shape <- envelope(dem_file);
	
	//map of colors (key: land_use, value: color)  just uses to visualize the different land_use
	map<int,rgb> colors;
	
	init {
		//we set the value of the land_use variable by the second (index = 1) value stored in the bands attribute 
		ask cell {
			land_use <- int(bands[1]);
		}
		
		//we define a color per land_use and use it to define the color of the cell
		list<int> land_uses <- remove_duplicates(cell collect each.land_use);
		colors <- land_uses as_map (each::rnd_color(255));
		ask cell {
			color <- colors[land_use];
		}
	}
}

//we define the cell grid from the two grid files: the first file (dem_file) will be used as reference for the definition of the grid number of rows and columns and location
//the value of the files are stored in the bands built-in list attribute: each value of the list corresponds to the value in the file
//the value of the first file is also stored in thr grid_value built-in variable
grid cell files: [dem_file,land_cover_file] {
	int land_use;
}

experiment importationraster type: gui {
	output {
		display map type: 3d axes:false antialias:false{
			grid cell elevation: true  triangulation: true refresh: false;	
		}
	}
}
