/**
 *  clustering
 *  Author: Patrick Taillandier
 *  Description: shows how to use clustering algorithms
 */

model clustering

global {
	init {
		create dummy number: 100;
		list<list> instances <- dummy collect ([each.location.x, each.location.y]);
		list<list<int>> clusters <- list<list<int>>(dbscan(instances, 10.0,3));
		
		loop cluster over: clusters {
			rgb col <- rnd_color(255);
			loop i over: cluster {
				ask dummy[i] {color <- col;}
			}
		}
	}
	
}

species dummy {
	rgb color <- #white;
	aspect default {
		draw circle(2) color: color;
	}
}

experiment clustering type: gui {
	output {
		display map{
			species dummy;
		}
	}
}
