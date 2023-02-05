/**
* Name: clean_road_network
* Author: Patrick Taillandier
* Description: shows how GAMA can help to clean network data before using it to make agents move on it
* Tags: gis, shapefile, graph, clean
*/

model clean_road_network

global {
	//Shapefile of the roads
	file road_shapefile <- file("../includes/roads.shp");
	
	//Shape of the environment
	geometry shape <- envelope(road_shapefile);
	
	
	//clean or not the data
	bool clean_data <- true parameter: true;
	
	//tolerance for reconnecting nodes
	float tolerance <- 3.0 parameter: true;
	
	//if true, split the lines at their intersection
	bool split_lines <- true parameter: true;
	
	//if true, keep only the main connected components of the network
	bool reduce_to_main_connected_components <- true parameter: true;
	
	string legend <- not clean_data ? "Raw data" : ("Clean data : tolerance: " + tolerance + "; split_lines: " + split_lines + " ; reduce_to_main_connected_components:" + reduce_to_main_connected_components );
	
	list<list<point>> connected_components ;
	list<rgb> colors;
			
	init {
		
		//clean data, with the given options
		list<geometry> clean_lines <- clean_data ? clean_network(road_shapefile.contents,tolerance,split_lines,reduce_to_main_connected_components) : road_shapefile.contents;
		
		//create road from the clean lines
		create road from: clean_lines;
		
		//build a network from the road agents
		graph road_network_clean <- as_edge_graph(road);
		
		//computed the connected components of the graph (for visualization purpose)
		connected_components <- list<list<point>>(connected_components_of(road_network_clean));
		loop times: length(connected_components) {colors << rnd_color(255);}
    }
}

//Species to represent the roads
species road {
	aspect default {
		draw shape color: #black;
	}
}

experiment clean_network type: gui {
	init {
		create clean_road_network_model with:[clean_data::false]; 
		create clean_road_network_model with:[split_lines::false,reduce_to_main_connected_components::false]; 
		create clean_road_network_model with:[split_lines::true,reduce_to_main_connected_components::false]; 
	}
	output {
		layout #split;
		display network type: 2d {
	
			 overlay position: { 10#px, 10#px } size: { 800 #px, 60 #px } background: # black transparency: 0.5  rounded: true
            {
				draw legend color: #white font: font("SansSerif", 12, #bold) at: {40#px, 40#px, 1 };
			}
			species road ;
			graphics "connected components" {
				loop i from: 0 to: length(connected_components) - 1 {
					loop j from: 0 to: length(connected_components[i]) - 1 {
						draw circle(2) color: colors[i] at: connected_components[i][j];	
					}
				}
			}
		}
	}
}
