/**
* Name:  Clustering of agents by their distance 
* Author:  Patrick Taillandier et JD Zucker ;-) 
* Description: A model to show how to use clustering operators with simple_clustering_by_distance to take into account the distances 
*        between agents or between cells to create the clusters,  and showing the relations between the people from the same cluster. 
* Tags: clustering, statistic, grid
*/

model clustering

global {
	//define the maximal distance between people in the continuous environement (in meters): if the distance between 2 people is lower than this value, they will be in the same group
	float max_dist_people <- 20.0;
	
	//define the maximal distance between cells (in number of cells): if the distance between 2 cells is lower than this value, they will be in the same group
	int max_dist_cell <- 1;
	
	//probability for a cell to have vegetation
	float proba_vegetation <- 0.2;
	
	//create the people agents
	init {
		create people number:20; 
    }
    
    //reflex that builds the people clusters
    reflex people_clustering {
    	//clustering by using the simple clustering operator: two people agents are in the same groups if their distance is lower than max_dist_people (in meters)
    	//returns a list of lists (i.e. a list of groups, a group is a list of people agents)
    	list<list<people>> clusters <- list<list<people>>(simple_clustering_by_distance(people, max_dist_people));
        
        //We give a random color to each group (i.e. to each people agents of the group)
        loop cluster over: clusters {
        	rgb rnd_color <- rnd_color(255);
        	ask cluster {
        		color_cluster <- rnd_color;
        	}
        }
        
        //build the hierchical clustering (https://en.wikipedia.org/wiki/Hierarchical_clustering)
        list clustering_tree <- hierarchical_clustering (people, max_dist_people);
        
        //create groups from the results of the hierarchical clustering
        do create_groups(clustering_tree, nil);
    }
    
    //recursive action that create group_people agents from the list of group.
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
    //reflex that builds the cell clusters
    reflex forest_clustering {
    	list<list<vegetation_cell>> clusters <- list<list<vegetation_cell>>(simple_clustering_by_distance(vegetation_cell where (each.color = #green), max_dist_cell));
        loop cluster over: clusters {
        	create forest {
        		cells <- cluster;
        		shape <- union (cells);
        	}
        }
        list clustering_tree <- hierarchical_clustering (people, max_dist_people);
    }
    
}
grid vegetation_cell width: 25 height: 25 neighbors: 4{
	rgb color <- flip (proba_vegetation) ? #green : #white;
}

species forest {
	list<vegetation_cell> cells;
	aspect default {
		draw shape.contour + 0.5 color: #red;
	}
}

species people {
	rgb color_cluster <- #black;
	rgb color_tree <- #black;
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
		draw shape + 0.2 color: #red;
		if (parent != nil) {
			draw line ([location, parent.location]) end_arrow: 2 color: #red;
		}
	}
}

experiment clustering type: gui {
	parameter "Maximal distance for people clustering" var: max_dist_people min: 0.0 max: 100.0 category: "People";
	parameter "Maximal distance for vegetation cell clustering" var: max_dist_cell min: 0 max: 5 category: "Forest";
	parameter "Probability for vegetation cells" var: proba_vegetation min: 0.1 max: 1.0 category: "Forest";
	
	//permanent layout: horizontal([vertical([0::5000,1::5000])::5000,2::5000]) tabs:true;
	output {
		layout horizontal([vertical([0::5000,1::5000])::5000,2::5000]) tabs:true editors: false;
		display map_people_clusters {
			species people aspect: cluster;
		}
		display map_people_tree {
			species people aspect: tree;
			species group_people;
		}
		display map_forest_clusters type:2d antialias:false{
			grid vegetation_cell border: #black;
			species forest;
		}
	}
}
