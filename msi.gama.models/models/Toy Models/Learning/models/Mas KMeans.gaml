/**
* Name: Agent Based Clustering
* Author: Jean-Danie Zucker with Patrick Taillandier's and Arnaud Grignard's Help
* Description: This model displays the step to stpe algorithm of k-means
* See for  https://en.wikipedia.org/wiki/K-means_clustering ...
* Clustering
* The k-medoid could be added
* To be added stop the simulation when convergence is reached
* To be added an overlay
* To be added position the points at the begining usug user interaction model...
*/
model MASKMEANS


global
{
// the number of classes to create (kmeans)
// It corresponds to the centroids
	int k ;
	// the number of points
	int N ;
	//number of dimensions
	int dimensions <- 2;
	float globalIntraDistance <- 0.0;
	bool converged <- false;
	font regular <- font("Helvetica", 14, # bold);
	init
	{
		//create datapoints agents
		create datapoints number: N
		{
			if (dimensions = 3)
			{
				location <- { rnd(100), rnd(100), rnd(100) };
			}

			if (dimensions = 2)
			{
				location <- { rnd(100), rnd(100) };
			}

		}
		//create centroid agents
		create centroids number: k
		{
			if (dimensions = 3)
			{
				location <- { rnd(100), rnd(100), rnd(100) };
			}

			if (dimensions = 2)
			{
				location <- { rnd(100), rnd(100) };
			}

		}
		int K <- length(centroids);
		if (K > 0) {loop i from:0 to: K-1 { ask centroids[i] { color_kmeans  <- hsb(i/K,1,1); }}}
					
					
			//give a random color to each centroid (i.e. to each datapoints agents of the group)
			//		loop c over: centroids { rgb col <- rnd_color(255); ask c { color_kmeans <- col;}}
		
	
	}
	
	reflex pauseAtConvergence when: converged { do pause;
		
	}
	reflex assign_points_to_centroid when: even(cycle)
	{
	    // The "assignment" step is also referred to as expectation step,
		ask centroids
		{
			mypoints <- list<datapoints> ([]);
		}

		loop pt over: datapoints
		{
			ask pt
			{
				if not empty(centroids) {
					mycenter <- centroids closest_to self;
					color_kmeans <- mycenter.color_kmeans;
					add self to: mycenter.mypoints;
				}
			}

		}

	}

	reflex update_centroids when: not even(cycle)
	{
	// the "update step" as maximization step,
	// making this algorithm a variant of the generalized expectation-maximization algorithm.

	//We give a random color to each group (i.e. to each datapoints agents of the group)
		ask centroids where (not empty(each.mypoints))
		{
			location <- mean(mypoints collect each.location);
			float oldist <- myIntraDistance;
			myIntraDistance <- mypoints sum_of (each distance_to self);
			converged <- (oldist-myIntraDistance) with_precision(2) = 0;
		}
		
		globalIntraDistance <- centroids sum_of (each.myIntraDistance);
	}

}

species datapoints
{
	rgb color_kmeans <- rgb(225,225,225) 	;
	centroids mycenter;
	aspect kmeans_aspect2D
	{
		draw circle(2) color: color_kmeans border:color_kmeans-25;
	}

	aspect kmeans_aspect3D
	{
		draw sphere(2) color: color_kmeans ;
	}

}

species centroids
{
	rgb color_kmeans <-  rgb(225,225,225);
	list<datapoints> mypoints;
	float myIntraDistance <- 0.0;
	aspect kmeans_aspect2D
	{
		// explicitly loops over a copy of the points to avoid concurrency issues with the simulation
		loop pt over: copy(mypoints)
		{
			draw line([location, pt]) + 0.1 color: color_kmeans;
		}
		draw cross(3, 0.5) color: color_kmeans border:color_kmeans-25;
	}

	aspect kmeans_aspect3D
	{
		loop pt over: mypoints
		{
			draw line([location, pt], 0.2) color: color_kmeans;
		}
		draw cube(5) color: color_kmeans border: # black;
		
	}

}

experiment clustering2D type: gui
{
	parameter "Number of clusters to split the data into" var: k init:4 category: "KMEANS";
	parameter "Number of points to be clustered" var: N init: 500;
	
		
	point target <- { 20, 95 };
	output
	{
		
		display map_kmeans 
		{

			graphics "Full target"
			{
				draw rectangle(120, 4) color: # yellow  at: { 50, 2 };
				draw rectangle(120, 4) color: # yellow at: target + { 30, 2 };
				if (not even(cycle))
				{
				// the "update step" as maximization step, (a mean is done to recenter)
					if ! (globalIntraDistance = 0) {
						draw "Current step was an estimation Step (each point is assigned the color of his nearest centroid" at:{ 12, 2 } font: regular color: # green;
						draw "Current sum of cluster intra-distance " + globalIntraDistance with_precision(1)  at:{ 12, 4 } font: regular color: # black;
						}
					if converged {draw "Algorithm has converged !" + " cycle "+ cycle at:{ 60, 4 } font: regular color: # red;}
					draw "Next step is a maximisation step the centroid will move to the center of its  associated points" at: target + { 0, 3 } font: regular color: # red;
				} else
				{
					if ! (globalIntraDistance = 0) {
						draw "Current step was a maximisation step the centroid moved to the center of its associated points" at: { 12, 2 } font: regular color: # red;
						draw "Current sum of cluster intra-distance " + globalIntraDistance with_precision(1)  at:{ 12, 4 } font: regular color: # black;
						}
					if converged {draw "Algorithm has converged !"  at:{ 60, 4 } font: regular color: # red;}
					draw "Next step is an estimation Step (each point is assigned the color of his nearest centroid" at: target + { 0, 3 } font: regular color: # green;
				}

			}
			species datapoints aspect: kmeans_aspect2D transparency:0.4;
			species centroids aspect: kmeans_aspect2D;

		}

	}
}

experiment clustering3D type: gui 
{
	parameter "Number of clusters to split the data into" var: k init:4 min: 0 max: 10 category: "KMEANS";
	parameter "Number of points to be clustered" var: N init:1000 ;
	parameter "Number of dimensions (2D or 3D)" var: dimensions init: 3 min: 2 max: 3;
	font regular <- font("Helvetica", 14, # bold);
	point target <- { 20, 95 };
	
	// The display is explicitly synchronized to avoid concurrency issues (if the points are changed in the simulation while being displayed)
	output synchronized: true
	{
		display map_kmeans type: 3d
		{
			species datapoints aspect: kmeans_aspect3D transparency:0.4;
			species centroids aspect: kmeans_aspect3D;
		}

	}

}

