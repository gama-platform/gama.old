/**
 *  RoadTrafficComplex
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficComplex
 
global {   
	file shape_file_roads  <- file("../includes/ManhattanRoads.shp") ;
	file shape_file_bounds <- file("../includes/ManhattanBounds.shp") ;
	geometry shape <- envelope(shape_file_bounds);
	int nbGoalsAchived <- 0;
	graph the_graph;  
	 
	init {  
		create road from: shape_file_roads with:[nbLanes::int(read("lanes"))] {
			geom_visu <- shape + (2 * nbLanes);
		}	
		the_graph <-  (as_edge_graph(road)) ;
		create people number: 300 { 
			speed <- 15.0 ;
			target <- any_location_in (one_of(road));
			location <- any_location_in (one_of(road));
			living_space <- 10.0;
			tolerance <- 0.1;
			lanes_attribute <- "nbLanes";
			obstacle_species <- [species(self)]; 
		}   
	}
	
} 

species road  { 		
	int nbLanes;
	int indexDirection; 
	geometry geom_visu;
	aspect base {    
		draw geom_visu color: rgb("black") ;
	} 
}
	
species people skills: [driving] { 
	float speed; 
	rgb color <- rgb(rnd(255),rnd(255),rnd(255)) ;
	point target <- nil ; 
	point targetBis <- nil ; 
	point previousLoc <- nil;
	bool normalMove <- true;
	float evadeDist <- 300.0;
		
	reflex move when: normalMove{
		previousLoc <- copy(location);
		do goto_driving target: target on: the_graph speed: speed ; 
		switch location { 
			match target {
				target <- any_location_in (one_of(road));
				nbGoalsAchived <- nbGoalsAchived +1;
			}
			match previousLoc {
				targetBis <- last((one_of(road where (each distance_to self < evadeDist)).shape).points);
				normalMove <- false;
			}
		}
	}
		
	reflex EvadeMove when: !(normalMove){
		previousLoc <- copy(location);
		do goto_driving target: targetBis on: the_graph speed: speed; 
		switch location { 
			match targetBis {
				normalMove <- true;
			}
			match previousLoc {
				targetBis <- last((one_of(road where (each distance_to self < evadeDist)).shape).points);
			}
		}
	}
		
	aspect base {
		draw circle(20) color: color;
	}
} 

experiment Complex type: gui {
	parameter "Shapefile for the roads:" var: shape_file_roads category: "GIS" ;
	parameter "Shapefile for the bounds:" var: shape_file_bounds category: "GIS" ;
	
	output {
		display city_display refresh_every: 1 {
			species road aspect: base ;
			species people aspect: base;
		}
		monitor "Number of goals achieved" value: nbGoalsAchived refresh_every: 1 ;
	}
}

