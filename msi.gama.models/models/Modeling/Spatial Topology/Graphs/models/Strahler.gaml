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
	file river_shapefile <- file("../includes/rivers.shp");
	geometry shape <- envelope(river_shapefile);
	map<int,rgb> color_index <- [1::#lightblue, 2::#green,3::#orange, 4::#red];
	init {
		create river from:river_shapefile ;
		river_network <- directed(as_edge_graph(river));
		strahler_numbers <- strahler(river_network);
		ask river {
			index <- strahler_numbers[self] as int;
		}
	}
}

species river {
	int index <- 1;
	aspect default {
		draw shape + index/2.0 color: color_index[index] end_arrow: 5;
		draw ""+index color: #black font: font(30);
	}
}

experiment testStrahler type: gui {
	output {
		display map {
			species river;
		}
	}
}
