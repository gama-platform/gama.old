/**
 *  DXFAgents
 *  Author: Patrick Taillandier
 *  Description: show how to load DXF file and to create agents from it
 */

model DXFAgents 

global {
	file house_file <- file("../includes/house.dxf");
	geometry shape <- envelope(house_file);
	
	init {
		create house_element from: house_file with: [layer::string(get("layer"))];
		map layers <- list(house_element) group_by each.layer;
		loop la over: layers.keys {
			rgb col <- rnd_color(255);
			ask layers[la] {color <- col;}
		}
	}
}

species house_element {
	string layer;
	rgb color;
	aspect default {
		draw shape color: color;
	}
}

experiment DXFAgents type: gui {
	output {
		display map type: opengl{
			species house_element;
		}
	}
}
