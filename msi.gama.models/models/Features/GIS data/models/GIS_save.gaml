/**
 *  saveGIS
 *  Author: Patrick Taillandier
 *  Description: Shows how to save GIS data
 */

model saveGIS

global {
	file shape_file_buildings <- file("../includes/buildings_simple.shp");
	string new_shape_file_buildings <- "../results/buildings_final.shp";
	
	//definition of the geometry of the world agent (environment) as the envelope of the shapefile
	geometry shape <- envelope(shape_file_buildings);
	
	init {
		//creation of the building agents from the shapefile: the height and type attributes of the building agents are initialized according to the HEIGHT and NATURE attributes of the shapefile
		create building from: shape_file_buildings with:[height::float(get("HEIGHT")), type::string(get("NATURE"))];
	}
	
	//when cycle = 5, save of the building agents in a shapefile with the attributes height, type and price.
	reflex save_data when: cycle = 5 {
		save building to: new_shape_file_buildings type:"shp" with:[height::"HEIGHT", type::"NATURE", price::"PRICE"];
	}
}

species building {
	float height;
	string type;
	float price <- (shape.area * height) * (type = "Industrial" ? 0.5: 1) update: price * (90 +rnd(20)) / 100; 
	rgb color <- type = "Industrial" ? rgb("pink") : rgb("gray");
	
	aspect default {
		draw shape depth: height color: color;
	}
	
}

experiment GIS_agentification type: gui {
	output {
		display city_display type: opengl {
			species building;
		}
	}
}
