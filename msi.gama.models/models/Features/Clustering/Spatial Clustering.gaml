/**
 *  clustering
 *  Author: Patrick Taillandier
 *  Description: example of use of the spatial clustering operators
 */

model clustering

global {
	float max_dist_people <- 20.0;
	int max_dist_cell <- 1;
	float proba_vegetation <- 0.2;
	init {
		create people number:20; 
    }
    reflex people_clustering {
    	list<list<people>> clusters <- list<list<people>>(simple_clustering_by_distance(people, max_dist_people));
        loop cluster over: clusters {
        	rgb rnd_color <- rnd_color(255);
        	ask cluster {
        		color_cluster <- rnd_color;
        	}
        }
        
        list clustering_tree <- hierarchical_clustering (people, max_dist_people);
        do create_groups(clustering_tree, nil);
    }
    
    action create_groups (list group, group_people parent_gp) {
    	bool compute_shape <- false;
    	loop el over: group {
    		if (el is people) {
    			parent_gp.shape <- people(el).shape;
    		}
    		else {
    			create group_people returns: created_g{
    				if (parent_gp != nil) {
    					add self to: parent_gp.sub_groups;
    				}
    				parent <- parent_gp;
    			}
    			do create_groups(el, first(created_g));
    			compute_shape <- true;
    		}
    	}
    	if (compute_shape and parent_gp != nil) {
    		ask parent_gp {
    			shape <- polyline (sub_groups collect each.location);
    		}
    		
    	}
    }
    reflex forest_clustering {
    	list<list<vegetation_cell>> clusters <- list<list<vegetation_cell>>(simple_clustering_by_distance(vegetation_cell where (each.color = °green), max_dist_cell));
        loop cluster over: clusters {
        	create forest {
        		cells <- cluster;
        		shape <- union (cells);
        	}
        }
        list clustering_tree <- hierarchical_clustering (people, max_dist_people);
    }
    
}
grid vegetation_cell width: 25 height: 25 neighbours: 4{
	rgb color <- flip (proba_vegetation) ? °green : °white;
}

species forest {
	list<vegetation_cell> cells;
	aspect default {
		draw shape.contour + 0.5 color: °red;
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

species group_people {
	list<group_people> sub_groups;
	group_people parent;
	aspect default {
		draw shape + 0.2 color: °red;
		if (parent != nil) {
			draw line ([location, parent.location]) end_arrow: 2 color: °red;
		}
	}
}

experiment clustering type: gui {
	parameter "Maximal distance for people clustering" var: max_dist_people min: 0.0 max: 100.0 category: "People";
	parameter "Maximal distance for vegetation cell clustering" var: max_dist_cell min: 0 max: 5 category: "Forest";
	parameter "Probability for vegetation cells" var: proba_vegetation min: 0.1 max: 1.0 category: "Forest";
	output {
		display map_people_clusters {
			species people aspect: cluster;
		}
		display map_people_tree {
			species people aspect: tree;
			species group_people;
		}
		display map_forest_clusters {
			grid vegetation_cell lines: °black;
			species forest;
		}
	}
}
