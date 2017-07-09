/**
* Name: testStrahler
* Author: Patrick Taillandier
* Description: exemple of Strahler number computation
* Tags: graph, strahler
*/

model exempleStrahler

global {
	graph river_network;
	map strahler_numbers;
	file river_shapefile <- file("../includes/river.shp");
	geometry shape <- envelope(river_shapefile);
	init {
		create river from:river_shapefile ;
		river_network <- directed(main_connected_component(as_edge_graph(river)));
		strahler_numbers <- strahler(river_network);
	}
}

species river {
	aspect default {
		draw shape color: #blue end_arrow: 5;
		if (self in strahler_numbers.keys) {draw ""+strahler_numbers[self] color: #black font: font(20);}
	}
}

experiment testStrahler type: gui {
	output {
		display map {
			species river;
		}
	}
}
