/**
* Name: From GAMA CRS
* Author: Patrick Taillandier
* Description: A model which shows how to CRS_transform operator which allows to translate a geometry (or a point) in GAMA CRS to a given CRS
* Tags:  gis, shapefile, spatial_computation, spatial_transformation, projection
*/
model From_GAMA_CRS

global {
	
	file building_file <- shape_file("../gis/init.shp");
	geometry shape <- envelope(building_file); //set the GAMA coordinate reference system using the one of the building_file (Lambert zone II).
	
	init {
		create building from: building_file;
		
		point poi_location <- first(building).location; //location of the first building in the GAMA reference system
		
		create poi with: [location::poi_location];
		
		point poi_location_WGS84 <- CRS_transform(poi_location, "EPSG:4326").location; //project the point to WGS84 CRS
		
		point poi_location_UTM31N <- CRS_transform(poi_location, "EPSG:32631").location; //project the point to UMT 31N CRS
		
		write "POI location - GAMA coordinates: " + poi_location +"\nWGS84: "+ poi_location_WGS84 + "\nUTM 31N: " + poi_location_UTM31N; 
		
	
	}
}

species poi {
	aspect default {
		draw circle(5) color: #red border: #black;
	}
}
species building {
	aspect default {
		draw shape color: #gray border: #black;
	}
}

experiment ProjectionManagement type: gui {
	output {
		display map {
			species building;
			species poi;
		}
	}
}
