/**
* Name: Loading of GIS data (buildings and roads)
* Author:
* Description: first part of the tutorial: Road Traffic
* Tags: gis
*/

model tutorial_gis_city_traffic

global {
	file buildings_shapefile <- file("../includes/building.shp");
	file roads_shapefile <- file("../includes/road.shp");
	geometry shape <- envelope(envelope(buildings_shapefile) + envelope(roads_shapefile));
	float step <- 10 #mn;
	
	init {
		create building from: buildings_shapefile ;
		create road from: roads_shapefile ;
	}
}

species building {
	string type; 
	rgb color <- #gray  ;
	
	aspect base {
		draw shape color: color ;
	}
}

species road  {
	rgb color <- #red ;
	aspect base {
		draw shape color: color ;
	}
}

experiment road_traffic type: gui {
		
	output {
		display city_display type:opengl {
			species building aspect: base ;
			species road aspect: base ;
		}
	}
}