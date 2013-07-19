/**
 *  model2
 *  This model illustrates how to load GIS data (shapefiles) and to read attributes from GIS data
 */ 
model model2 
 
global {
	file roads_shapefile <- file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	init {
		create roads from: roads_shapefile;
		create buildings from: buildings_shapefile with: [type:: string(read("NATURE"))] {
			color <- type="Industrial" ? rgb("blue") : rgb("gray");
		}
		create people number:500;
	}
}

species people skills:[moving]{		
	int size <- 5;
	float speed <- 5.0 + rnd(5);
	reflex move{
		do wander;
	}
	aspect circle{
		draw circle(size) color:rgb("green");
	}
}

species roads {
	aspect geom {
		draw shape color: rgb("black");
	}
}

species buildings {
	string type;
	rgb color;
	aspect geom {
		draw shape color: color;
	}
}

experiment main_experiment type:gui{
	output {
		display map {
			species roads aspect:geom;
			species buildings aspect:geom;
			species people aspect:circle;			
		}
	}
}