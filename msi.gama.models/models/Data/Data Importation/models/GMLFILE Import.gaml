/**
* Name: GML File to Agents
* Author:  Patrick Taillandier
* Description: Model which shows how to import a GML (Geography Markup Language) file in GAMA and use it to create Agents.
* Tags:  load_file, gml file, gis
*/
model simpleGMLfileLoading



global {
	file gml_file_roads <- gml_file("../includes/road.gml", "EPSG:2154" );
	
	//definition of the geometry of the world agent (environment) as the envelope of the gml file
	geometry shape <- envelope(gml_file_roads);
	
	init {
		//creation of the road agents from the gml file: the name and type attributes of the road agents are initialized according to the NOM and TYPE attributes of the gml file
		create road from: gml_file_roads with:[name::string(get("NOM")), type::string(get("TYPE"))] ;
	}
}

species road {
	string type;
	rgb color <- #black;
	
	aspect default {
		draw shape  color: color;
		draw type color: #black;
	}
	
}

experiment GIS_agentification type: gui {
	output {
		display city_display  {
			species road;
		}
	}
}

