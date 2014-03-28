/**
 *  Agentification of GIS data
 *  Author: Patrick Taillandier
 *  Description: this model shows how to agentify GIS data  
 */

model GIS_agentification

global {
	file shape_file_buildings <- file("../includes/buildings_simple.shp");
	
	//definition of the geometry of the world agent (environment) as the envelope of the shapefile
	geometry shape <- envelope(shape_file_buildings);
	
	init {
		//creation of the building agents from the shapefile: the height and type attributes of the building agents are initialized according to the HEIGHT and NATURE attributes of the shapefile
		create building from: shape_file_buildings with:[height::float(get("HEIGHT")), type::string(get("NATURE"))];
	}
}

species building {
	float height min: 0.0 max: 100.0 update: height + 5 - rnd(10);
	string type;
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
