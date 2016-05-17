/**
* Name: Agent Based Clustering
* Author: Jean-Danie Zucker with Patrick's Help
* Description: This model displays the step to stpe algorithm of k-means
* See for  https://en.wikipedia.org/wiki/K-means_clustering ...
* Clustering
* The k-medoid could be added
*/


model MASKMEANS

global {
	// the number of classes to create (kmeans)
	// It corresponds to the centroids
	int k <- 4;
	// the number of points
	int N <- 100;
	
	init {
		//create datapoints agents
		create datapoints number: N;
		
		//create centroid agents
		create centroids number: k;
		
		//give a random color to each centroid (i.e. to each datapoints agents of the group)
		loop c over: centroids {
			rgb col <- rnd_color(255);
			ask c {color_kmeans <- col;
			}
		}
		
	}
	
	reflex assign_points_to_centroid when: even(cycle) {
		// The "assignment" step is also referred to as expectation step,
		ask centroids {
			mypoints <- list<datapoints>([]);
		}
		loop pt over: datapoints {
			ask pt {mycenter <- (centroids) closest_to self ;
			color_kmeans <- mycenter.color_kmeans ;	
			add self to: mycenter.mypoints;
			}
		}
	}
	
	reflex update_centroids when: not even(cycle) {
		// the "update step" as maximization step, 
		// making this algorithm a variant of the generalized expectation-maximization algorithm.
		
		//We give a random color to each group (i.e. to each datapoints agents of the group)
		loop center over: centroids {
			//old code... center.location <- geometry(center.mypoints).location;
			center.location <- mean(center.mypoints collect each.location);
		}
	}
	
}

species datapoints {
	rgb color_kmeans <- #grey;
	centroids mycenter;

	aspect kmeans_aspect {
		draw circle(2) color: color_kmeans;
	}
}

species centroids {
	rgb color_kmeans <- #grey;
	list<datapoints> mypoints ;
	aspect kmeans_aspect {
		draw cross(3,0.5) color: color_kmeans border: #black;
		loop pt over: mypoints {
			draw line([location, pt]) color: color_kmeans;
		}
	}
}

experiment clustering type: gui {
	parameter "Number of clusters to split the data into" var: k category: "KMEANS";

	output {

		display map_kmeans{
			species datapoints aspect: kmeans_aspect;
			species centroids aspect: kmeans_aspect;
		}
	}
}