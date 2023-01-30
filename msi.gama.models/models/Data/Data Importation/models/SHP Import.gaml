/**
* Name: Shapefile to Agents
* Author:  Patrick Taillandier
* Description: Model which shows how to import a Shapefile in GAMA and use it to create Agents.
* Tags:  load_file, shapefile, gis
*/
model simpleShapefileLoading



global {
	file shape_file_buildings <- shape_file("../includes/buildings_simple.shp");
	
	//definition of the geometry of the world agent (environment) as the envelope of the shapefile
	geometry shape <- envelope(shape_file_buildings);
	
	init {
		//creation of the building agents from the shapefile: the height and type attributes of the building agents are initialized according to the HEIGHT and NATURE attributes of the shapefile
		create building from: shape_file_buildings with:[height::float(get("HEIGHT")), type::string(get("NATURE"))];
	}
}

species building {
	float height;
	string type;
	rgb color <- type = "Industrial" ? #pink : #gray;
	
	aspect default {
		draw shape depth: height color: color;
	}
	
}

experiment GIS_agentification type: gui {
	output {
		display city_display type: 3d axes:false{
			species building;
		}
	}
}

