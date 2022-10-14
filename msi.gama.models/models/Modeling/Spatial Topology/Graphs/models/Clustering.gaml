/**
* Name: Clustering
* Author: Patrick Taillandier
* Description: This model shows how to use clustering algorithms to create communities in a graph. 3 algorithms are avalaible: girvan_newman_clustering, k_spanning_tree_clustering and label_propagation_clustering 
* Tags: Graph, Clustering
*/

model Clustering

global {
	int k <- 4;
	int max_iteration <- 100;
	list<list<node_agent>> clusters;
	init {
		graph the_graph <- generate_watts_strogatz(20, 0.01, 4, true,node_agent, edge_agent);	
		the_graph <- layout_force(the_graph, world.shape,0.5,0.5,100);
		clusters <- girvan_newman_clustering(the_graph, k);
			
		loop c over: clusters {
			rgb col <- rnd_color(255);
			ask list<node_agent>(c) {
				col_cluster_gn <- col;
			}
		}
		
		clusters <- k_spanning_tree_clustering(the_graph, k);
			
		loop c over: clusters {
			rgb col <- rnd_color(255);
			ask list<node_agent>(c) {
				col_cluster_stc <- col;
			}
		}
		
		clusters <- label_propagation_clustering(the_graph, max_iteration);
			
		loop c over: clusters {
			rgb col <- rnd_color(255);
			ask list<node_agent>(c) {
				col_cluster_lpc <- col;
			}
		}
		
		
	}
}


species edge_agent {
	aspect default {	
		draw shape color: #black;
	}
}

species node_agent {
	rgb col_cluster_gn;
	rgb col_cluster_stc;
	rgb col_cluster_lpc;
	aspect default {	
		draw circle(1) color: #red;
	}
	aspect color_cluster_gn {	
		draw circle(1) color: col_cluster_gn ;
	}
	aspect color_cluster_stc {	
		draw circle(1) color: col_cluster_stc ;
	}
	aspect color_cluster_lpc {	
		draw circle(1) color: col_cluster_lpc ;
	}
}

experiment clustering type: gui {
	
	output {
		layout #split;
		display general_graph type: 3d axes: false{
			species edge_agent ;
			species node_agent ;
		}
		display cluster_girvan_newman type: 3d axes: false{
			species edge_agent ;
			species node_agent aspect: color_cluster_gn ;
		}
		display cluster_k_spanning_tree type: 3d axes: false{
			species edge_agent ;
			species node_agent aspect: color_cluster_stc ;
		}
		display cluster_label_propagation type: 3d axes: false{
			species edge_agent ;
			species node_agent aspect: color_cluster_lpc ;
		}
	}
}