/**
* Name: To GAMA CRS
* Author: Patrick Taillandier
* Description: A model which shows how to to_GAMA_CRS operator which allows to translate a geometry (or a point) to GAMA CRS
* Tags:  gis, shapefile, spatial_computation, spatial_transformation, projection
*/
model To_GAMA_CRS

global {
	
	file building_file <- shape_file("../gis/init.shp");
	file data_csv_file <- csv_file("../gis/data.csv", ",", float);
	
	geometry shape <- envelope(building_file); //set the GAMA coordinate reference system using the one of the building_file (Lambert zone II).
	
	init {
		create building from: building_file;
		matrix<float> data <- matrix<float>(data_csv_file);
		loop i from: 0 to: data.rows - 1 {
			point poi_location_WGS84 <- {data[0,i],data[1,i]};
			point poi_location_GAMA <- point(to_GAMA_CRS(poi_location_WGS84, "EPSG:4326"));
			write "\nPOI location - WGS84: " + poi_location_WGS84 +"\nGAMA CRS: "+ poi_location_GAMA; 
			create poi with: [location::poi_location_GAMA];
		}
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
