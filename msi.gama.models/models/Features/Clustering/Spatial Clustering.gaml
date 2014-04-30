/**
 *  clustering
 *  Author: Patrick Taillandier
 *  Description: example of use of the clustering operators
 */

model clustering

global {
	float max_dist <- 20.0;
	map<list, rgb> map_color ;
	init {
		create people number:20; 
    }
    reflex people_clustering {
    	list<list<people>> clusters <- list<list<people>>(simple_clustering_by_distance(people, max_dist));
        loop cluster over: clusters {
        	rgb rnd_color <- rnd_color(255);
        	ask cluster {
        		color_cluster <- rnd_color;
        	}
        }
        
        list clustering_tree <- hierarchical_clustering (people, max_dist);
        
        do init_color_agents(clustering_tree);
    }
    
    action init_color_agents (list group) {
    	bool is_leaf <- true;
    	loop el over: group {
    		if not (el is people) {
    			is_leaf <- false;
    			do init_color_agents (el);
    		}
    	}
    	if (is_leaf) {
    		map_color[group] <- rnd_color(255);
    	}
    }
}

species people {
	rgb color_cluster <- °black;
	rgb color_tree <- °black;
	aspect cluster {
		draw circle(2) color: color_cluster;
	}
	aspect tree {
		draw circle(2) color: color_tree;
	}
}

experiment clustering type: gui {
	parameter "Maximal distance for clustering" var: max_dist min: 0.0 max: 100.0;
	output {
		display map_clusters {
			species people aspect: cluster;
		}
		display map_tree {
			species people aspect: tree;
		}
	}
}
