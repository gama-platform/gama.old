/**
 *  WAGAMA1
 *  Author: Patrick Taillandier
 *  Description: model structure definition; species definition (node agents); display definition; parameter definition; agent creation from GIS data
 */

model WAGAMA1
 
global {
	
	file nodes_file <- file('../includes/nodes_simple.shp'); 
	file env_file <- file('../includes/environment.shp');
	init {
		create node from: nodes_file;
	}
}

environment bounds: env_file;

entities {
	species node {
		const radius type: float <- 2.0;
		rgb color <- rgb('white');
		
		aspect circle {
			draw circle(radius) color: color;
		}
	}
}

experiment with_interface type: gui {
	parameter 'GIS file of the nodes' var: nodes_file category: 'GIS';
	parameter 'GIS file of the environment' var: env_file category: 'GIS';
	output {
		display dynamic {
			species node aspect: circle;
		}
	}
}