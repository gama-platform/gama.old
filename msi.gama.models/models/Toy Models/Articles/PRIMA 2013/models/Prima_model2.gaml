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
		create buildings from: buildings_shapefile;
		create people number:1000 {
			location <- any_location_in(one_of(buildings));
			target <- any_location_in(one_of(buildings));
		}
	}
}

species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	point target;
	reflex move {
		do wander;
	}
	reflex infect when: is_infected{
		ask people at_distance 10 {
			if flip(0.01) {
				is_infected <- true;
			}
		}
	}
	aspect circle{
		draw circle(5) color:is_infected ? #red : #green;
	}
}

species roads {
	aspect geom {
		draw shape color: #black;
	}
}

species buildings {
	aspect geom {
		draw shape color: #gray;
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