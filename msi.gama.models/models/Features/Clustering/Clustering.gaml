/**
 *  clustering
 *  Author: Patrick Taillandier
 *  Description: shows how to use clustering algorithms
 */

model clustering

global {
	int k <- 4;
	float eps <- 10.0; 
	int minPoints <- 3;
	init {
		create dummy number: 100;
	}
	
	reflex cluster_building {
		list<list> instances <- dummy collect ([each.location.x, each.location.y]);
		list<list<int>> clusters_dbscan <- list<list<int>>(dbscan(instances, eps,minPoints));
		
		loop cluster over: clusters_dbscan {
			rgb col <- rnd_color(255);
			loop i over: cluster {
				ask dummy[i] {color_dbscan <- col;}
			}
		}
		
		list<list<int>> clusters_kmeans <- list<list<int>>(kmeans(instances, k));
		loop cluster over: clusters_kmeans {
			rgb col <- rnd_color(255);
			loop i over: cluster {
				ask dummy[i] {color_kmeans <- col;}
			}
		}
	}
	
}

species dummy {
	rgb color_dbscan <- #white;
	rgb color_kmeans <- #white;
	aspect dbscan_aspect {
		draw circle(2) color: color_dbscan;
	}
	aspect kmeans_aspect {
		draw circle(2) color: color_kmeans;
	}
}

experiment clustering type: gui {
	parameter "Number of clusters to split the data into" var: k category: "KMEANS";
	parameter "Maximum radius of the neighborhood to be considered" var: eps category: "DBSCAN";
	parameter "Minimum number of points needed for a cluster " var: minPoints category: "DBSCAN";
	output {
		display map_dbscan{
			species dummy aspect: dbscan_aspect;
		}
		display map_kmeans{
			species dummy aspect: kmeans_aspect;
		}
	}
}
