/**
 *  model2
 *  This model illustrates how to load GIS data (shapefiles) and to read attributes from GIS data
 */ 
model model2 
 
global {
	file shapefile <- file('../includes/building.shp');
	geometry shape <- envelope(shapefile);
	init {
		create Buildings from: shapefile with: [type:: string(read('NATURE'))] {
			mycolor <- type='Industrial' ? rgb('blue') : rgb('yellow');
		}
	}
}

species Buildings {
	string type;
	rgb mycolor;
	aspect asp1 {
		draw shape color: mycolor;
	}
}

experiment exp2 type: gui {
	output {
		display default_display  {
			species Buildings aspect: asp1;
		}
	}
}