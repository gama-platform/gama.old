/**
 *  simpleOSMLoading
 *  Author: patricktaillandier
 *  Description: load data and format them in order to be used easily by the advance driving skill
 */

model simpleOSMLoading 
 
global{
	
	file osmfile <-  file("../includes/rouen.gz")  ;
	geometry shape <- envelope(osmfile);
	graph the_graph; 
	

	init {
		//possibility to load all of the attibutes of the OSM data: for an exhaustive list, see: http://wiki.openstreetmap.org/wiki/Map_Features
		create osm_agent from:osmfile with: [highway_str::string(read("highway")), building_str::string(read("building")),lanes::string(read("lanes")),oneway::string(read("oneway")),maxspeed::string(read("maxspeed"))];
		
		//from the created generic agents, creation of the selected agents
		ask osm_agent {
			if (length(shape.points) = 1 and highway_str != nil ) {
				create node with: [shape ::shape, type:: highway_str];
			} else {
				if (highway_str != nil and highway_str in ["primary", "secondary", "tertiary", "motorway", "living_street","residential", "unclassified"]) {
					create road with: [shape ::shape, type:: highway_str, oneway::oneway, maxspeed::float(maxspeed), lanes::int(lanes)];
				} 
			}
			do die;
		}
		ask road {
			if lanes < 1 {lanes <- 1;} //default value for the lanes attribute
			if maxspeed = 0 {maxspeed <- 50.0;} //default value for the maxspeed attribute
			
			point ptF <- first(shape.points);
			if ((node first_with (each.location = ptF)) = nil) {
				create node with:[location::ptF];
			}
			point ptL <- last(shape.points);
			if ((node first_with (each.location = ptL)) = nil) {
				create node with:[location::ptL];
			}
		}
		ask node {
			if (empty (road overlapping (shape + 0.5))) {
				do die;
			}
		}
		ask road {
			point ptF <- first(shape.points);
			if ((node first_with (each.location = ptF)) = nil) {
				do die;
			}
			else {
				point ptL <- last(shape.points);
				if ((node first_with (each.location = ptL)) = nil) {
					do die;
				}
			}
		}
		save road type:"shp" to:"roads.shp" with:[lanes::"lanes",maxspeed::"maxspeed", oneway::"oneway"] ;
		save node type:"shp" to:"nodes.shp" with:[type::"type"] ;
	}
	
	
		
}

species osm_agent frequency: 0{
	string highway_str;
	string building_str;
	string oneway;
	string maxspeed;
	string lanes;
	
} 
	
	

species road frequency: 0{
	rgb color <- rgb(rnd(255),rnd(255),rnd(255));
	string type;
	string oneway;
	float maxspeed;
	int lanes;
	aspect base_ligne {
		draw shape color: color; 
	}
	
} 
	
species node {
	string type;
	aspect base { 
		draw square(3) color: rgb("red") ;
	}
} 
	

experiment experiment_light type: gui {
	output {
		display carte_principale type: opengl ambient_light: 100{
			species road aspect: base_ligne  refresh: false  ;
			species node aspect: base   refresh: false ;
		}
	}
}
