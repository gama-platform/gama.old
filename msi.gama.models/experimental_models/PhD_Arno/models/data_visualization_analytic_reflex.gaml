/**
 *  datavisualizationfeature
 *  Author: Arnaud Grignard
 *  Description: Low-level user analytic activities while interacting with an instance of data visualization are presented in the following model
 */

model datavisualizationfeature

global {
	map code_couleur<- map(["background"::rgb(10,40,55), "model":: rgb(255,249,192), "view"::°black,"environment"::°black, "cells"::°white]);
	int nb_cells <- 10;
	int envSize <-100;
	int cellSize<-10;
	geometry shape <- square(envSize);
	

	string analyticReflex <-"retrieveValue" among:["retrieveValue","filter","computeDerivedValue","sort","Cluster"];
	string description;
	string pseudocode;

	init {
		
		/*geometry the_shape <- circle(50);
		loop geom over: to_rectangles(the_shape, {2,2}, false) { //to_rectangles(geometry, dimension, true = overlaps, false = contains inside
                        create cells with: [location::geom.location]{
                        size <-cellSize * rnd(100)/100.0;
                        size<-1;
			            color <- code_couleur["cells"];	
                        }
                        
                }*/
		
		int curcell <- 0;
		create cells number: nb_cells{
			location <-{rnd(envSize),rnd(envSize)};
			size <-cellSize * rnd(100)/100.0;
			color <- code_couleur["cells"];
		}
		
	}

	
	reflex retrieveValue when: analyticReflex = "retrieveValue" {
		description <- "Given a set of specific cases, find attributes of those cases.";
		pseudocode <-"What are the values of attributes {X, Y, Z, ...} in the data cases {A, B, C, ...}?";
		ask cells where (each.size < 5){
		  do die;
		}
	}
	
	reflex filter when: analyticReflex = "filter"{
		description <- "Find data satisfying conditions on attribute values.";
		pseudocode <-"Which data cases satisfy conditions {A, B, C...}?";
		trace "za";
		ask cells {
			if(size > 5){
				do die;
			}
		}
	}
	
	reflex computeDeriveValue when: analyticReflex = "computeDerivedValue"{
		description <- "Given a set of data cases, compute an aggregate numeric representation of those data cases.";
		pseudocode <-"What is the value of aggregation function F over a given set S of data cases?";
		int nbcells <- length(cells);
		ask cells{
			do die;
		}
		create cells with: [location::{world.shape.width/2,world.shape.height/2},size::nbcells*cellSize/2];
	}
	
	reflex sort when: analyticReflex = "sort"{
		description <- "Given a set of data cases, rank them according to some ordinal metric.";
		pseudocode <-"What is the sorted order of a set S of data cases according to their value of attribute A??";
		ask cells{
          location <- {size*10,envSize/2};
		}
	}
	
	reflex Cluster when: analyticReflex = "Cluster"{
		description <- "Given a set of data cases, find clusters of similar attribute values.";
		pseudocode <-"Which data cases in a set S of data cases are similar in value for attributes {X, Y, Z, ...}?";
		int cluster1 <-0;
		int cluster2 <-0;
		ask cells{
          if(size>5){
          	cluster1 <-cluster1+1;
          }
          else{
          	cluster2 <- cluster2+1;
          }
          do die;   
		}
		create cells with: [size::cluster1*cellSize/2];
        create cells with: [size::cluster2*cellSize/2]; 
	}
}

species cells {
	rgb color;
	string type;
	float size;
	aspect default {
	  draw circle(size) color: °orange  border:°orange at: location;
	  draw circle(size*0.8) color: color  border:°orange at: location;	
	}
}

experiment Display type: gui {
	parameter "Analytic reflex" var:analyticReflex category:"Analysis";
	output {
		display View1  diffuse_light:10  type:opengl background:code_couleur["background"]{ 
			overlay "Cycle: " + (cycle) center: "Duration: " + total_duration + "ms" right: "Model time: " + as_date(time,"") color: [°yellow, °orange, °yellow];
			//overlay "Cycle: " + (cycle) center: "Duration: " + total_duration + "ms" right: "Model time: " + as_date(time,"") color: [°yellow, °orange, °yellow];
			graphics "world"{
				//draw "analytic reflex: " + analyticReflex size:5 color:°white at:{-world.shape.width,world.shape.height/2};
				draw "analytic reflex: " + analyticReflex size:5 color:°white at:{-world.shape.width*1.15,world.shape.height/2};
				draw description size:3 color:°white at:{-world.shape.width*1.15,world.shape.height*0.6};
				draw pseudocode size:3 color:°white at:{-world.shape.width*1.15,world.shape.height*0.7};
				//draw "gaml:????" size:3 color:°white at:{-world.shape.width*1.15,world.shape.height*0.8};
				
			}
			graphics 'model'{
			  draw shape color:code_couleur["model"];
			  draw "model" size:5 color:°white at:{world.shape.width/2,-world.shape.height*0.05};
			}
			graphics 'view'{
			  draw shape color:code_couleur["view"] at:{world.shape.width*1.5,world.shape.height/2};
			  draw "view" size:5 color:°white at:{world.shape.width*1.5,-world.shape.height*0.05};
			}
			species cells refresh:false;
			species cells position:{1.0,0};
		}
	}

}

