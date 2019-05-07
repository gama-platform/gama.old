/**
* Name:  Clustering of agents by K Means and DBScan
* Author:  Patrick Taillandier et J.-D. Zucker
* Description: A model to show how to use clustering operators and two methods of clustering (K Means and DBScan) 
*              with the goal of grouping agents into clusters
* Tags: clustering, statistic
*/

model clustering

global {
	//the number of groups to create (kmeans)
	int k <- 4;
	
	//the maximum radius of the neighborhood (DBscan)
	float eps <- 20.0; 
	
	//the minimum number of elements needed for a cluster (DBscan)
	int minPoints <- 10;
	
	init {
		//create dummy agents
			create dummy number: 20;
	     }	
	     
	reflex cluster_building {
		//create a list of list containing for each dummy agent a list composed of its x and y values
		list<list> instances <- dummy collect ([each.location.x, each.location.y]);
		
		//from the previous list, create groups with the eps and minPoints parameters and the DBSCAN algorithm (https://en.wikipedia.org/wiki/DBSCAN)
		list<list<int>> clusters_dbscan <- list<list<int>>(dbscan(instances, eps, minPoints));
		
		// We give a random color to each group (i.e. to each dummy agents of the group)
       loop cluster over: clusters_dbscan {
			rgb col <- rnd_color(255);
			loop i over: cluster {
				ask dummy[i] {color_dbscan <- col;}
			}
	   }
	  //  		write 'Cluster =' + string(clusters_dbscan);
//		int K <- length(clusters_dbscan);
//		write 'length=' + string(K-1);
//		write 'instances ' + string(instances);
//		loop i from:0 to: K-1 {
//			rgb col <- rnd_color(255);
//			loop j over: clusters_dbscan[i] {
//				write 'i=' +  string(i) + 'j=' + string(j) + 'K=' + string(K);
//				ask dummy[j] {color_dbscan <- hsb(i/max(1,K),1,1);}
//			}
		
		//from the previous list, create k groups  with the Kmeans algorithm (https://en.wikipedia.org/wiki/K-means_clustering)
		list<list<int>> clusters_kmeans <- list<list<int>>(kmeans(instances, k));
		
		//We give a fixed color to each group in function of the cluwter's number (i.e. to each dummy agents of the group)
		int K <- length(clusters_kmeans);
		loop i from:0 to: K-1 {
			// rgb col <- rnd_color(255);
			loop j over: clusters_kmeans[i] {
				ask dummy[j] {color_kmeans <- hsb(i/K,1,1);}
			}
		}
   }
 }  
   
species dummy skills:[moving] {
	rgb color_dbscan <- #grey;
	rgb color_kmeans <- #grey;
	aspect dbscan_aspect {
		draw square(2) color: color_dbscan;
	}
	aspect kmeans_aspect {
		draw circle(2) color: color_kmeans;
	}
	reflex move
	{
		do wander;		
	}
}

experiment clustering type: gui {
	parameter "Number of clusters to split the data into" var: k category: "KMEANS";
	parameter "Maximum radius of the neighborhood to be considered" var: eps category: "DBSCAN";
	parameter "Minimum number of points needed for a cluster " var: minPoints category: "DBSCAN";
	output {
		layout horizontal([1::5000,0::5000]) tabs:true editors: false;
		display map_dbscan{
            //define a new overlay layer positioned at the coordinate half-space/0
            overlay position: { world.shape.width/2, 0 } size: { 180 #px, 50 #px } background: #black transparency: 0.2 border: #black rounded: true
            {
					//draw square(5#px) at: { 0, 0 } color: #red border: #white;
                    draw string("DbScan") at: { world.shape.width/2 - 40, 20 } color: #blue font: font("SansSerif", 36, #bold); 
                }		

			species dummy aspect: dbscan_aspect;
		}
		display map_kmeans{
			overlay position: { world.shape.width/2, 0 } size: { 180 #px, 50 #px } background: #black transparency: 0.2 border: #black rounded: true
            {
					//draw square(5#px) at: { 0, 0 } color: #red border: #white;
                    draw string("K-means") at: { world.shape.width/2-45, 20 } color: #orange font: font("SansSerif", 36, #bold); 
                }	
			species dummy aspect: kmeans_aspect;
		}
	}
}