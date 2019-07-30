/***
* Name: MASKmeansInteractive
* Author: Jean-Daniel Zucker
* Description: Model which shows how to use the event layer to place points and initial centroids
* to see the impact of choosing. 
* CHOOSE the experiment SelectPoints2Cluster
* Click on the dot on the right palette to then click on the left display
* so as to position points. Then click on the cross on the right palette (to position centroids). Then
* drop a few centroids. Their number is the number of clusters searched for.
* Then start to iterate on the simulation one at a time ak-means and see the kmeans algorithm.
***/

model MASKmeansInteractive
import "Mas KMeans.gaml"

global {

	//current action type
	int action_type <- -1;	
	
	//images used for the buttons
	list<file> images <- [
		file("../images/dot.jpg"),
		file("../images/cross.png"),
		file("../images/eraser.jpg"),
		file("../images/empty.png")
	]; 
	
	reflex pauseAtConvergence when: converged { }
	
	action activate_act {
		button selected_but <- first(button overlapping (circle(1) at_location #user_location));
		if(selected_but != nil) {
			ask selected_but {
				ask button {bord_col<-#black;}
				if (action_type != id) {
					action_type<-id;
					bord_col<-#red;
				} else {
					action_type<- -1;
				}
				
			}
		}
	}
	
	action cell_management {
		write agents;
		switch action_type {
			match 0 {
				create datapoints with:(location : #user_location);
			}
			
			match 1 {
			
				create centroids with:(location : #user_location);
				int K <- length(centroids);
		  		loop i from:0 to: K-1 { ask centroids[i] { color_kmeans  <- hsb(i/K,1,1); }}
		   }
		
				
			match 2 {
				list<agent> close_ag <- (datapoints+centroids) overlapping (circle(5) at_location #user_location);
				if not empty(close_ag) {
					ask close_ag closest_to #user_location {
						if (self is datapoints ) {
							centroids c <- datapoints(self).mycenter;
							if (c != nil) {
								c.mypoints >> self;
							}
						} else {
							ask centroids(self).mypoints {
								mycenter <- nil;
								color_kmeans <- rgb(225,225,225) ;
							}
						}
						do die;
					}
				}
				
			}
				
			
		}
	}

}


grid button width:2 height:2 
{
	int id <- int(self);
	rgb bord_col<-#black;
	aspect normal {
		draw rectangle(shape.width * 0.8,shape.height * 0.8).contour + (shape.height * 0.01) color: bord_col;
		draw image_file(images[id]) size:{shape.width * 0.5,shape.height * 0.5} ;
	}
}

// To avoid displaying experiments coming from the inherited model MAS_KMEANS
experiment clustering2D type: gui virtual: true;
experiment clustering3D type: gui virtual: true;

experiment SelectPoints2Cluster2D type: gui {
	output {
		layout horizontal([0.0::8000,1::2000]) tabs:true;
		
		display map  {
			
			
			event mouse_down action:cell_management;
			species datapoints aspect: kmeans_aspect2D transparency:0.5;
			species centroids aspect: kmeans_aspect2D;
			graphics "Full target"
			{
			if ! (globalIntraDistance = 0) {
						draw "Current sum of cluster intra-distance " + globalIntraDistance with_precision(1)  at:{ 12, 4 } font: regular color: # black;
						}
			
			if converged {draw "Algorithm has converged !" + " at cycle "+ cycle at: { 60, 4 } font: regular color: # red; }
			}
		}
		//display the action buttons
		display action_buton background:#white name:"Tools panel"  	{
			species button aspect:normal ;
			event mouse_down action:activate_act;    
		}

	}
}
