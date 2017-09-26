/**
* Name: Loading of GIS data (buildings and roads)
* Author:
* Description: first part of the tutorial: Road Traffic
* Tags: gis
*/

model tutorial_gis_city_traffic

global {
	file buildings_shapefile <- file("../includes/Nha_o_va_cong_trinh.shp");
	file roads_shapefile <- file("../includes/Duong_Giao_Thong.shp");
	file rivers_shapefile <- file("../includes/Song_Han.shp");
	geometry shape <- envelope(envelope(buildings_shapefile) + envelope(roads_shapefile));
	//geometry shape <- envelope(roads_shapefile);
	float step <- 10 #mn;
	init {
		create building from: buildings_shapefile ;
		create road from: roads_shapefile ;
		create river from: rivers_shapefile ;
	}
}

species building {
	string type; 
	rgb color <- #gray  ;
	
	aspect base {
		draw shape color: color ;
	}
}

species river {
	string type; 
	rgb color <- #blue  ;
	
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
			species river aspect: base ;
			
		}
	}
}