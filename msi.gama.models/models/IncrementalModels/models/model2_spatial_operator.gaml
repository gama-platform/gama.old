/**
 *  model2
 *  This model illustrates how to load GIS data (shapefiles) and to read attributes from GIS data
 */ 
model model2 
 
global {
	file shapefile <- file('../includes/building.shp');
	geometry shape <- envelope(shapefile);
	init {
		create polygon from: shapefile with: [type:: string(read('NATURE'))] {
			mycolor <- type='Industrial' ? rgb('blue') : rgb('blue');
		}
	}
}

species polygon {
	string type;
	rgb mycolor;
	
	reflex scale{
		shape <-shape * 1.5;
	}
	reflex rotate{
		shape <- shape rotated_by 90;
	}
	reflex translate{
		location <- location + {10,10};
	}
	
	reflex union{
		list<polygon> overlappingPolygons <-  polygon overlapping self;
		if(not empty(overlappingPolygons)){
			shape <- shape + one_of (overlappingPolygons).shape;	
		}
	}
	aspect geometry {
		draw shape color: rgb('blue');
	}
}

experiment exp2 type: gui {
	output {
		display default_display  {
			species polygon aspect: geometry;
		}
	}
}