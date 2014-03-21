/**
 *  RoadTrafficSimple
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficSimple 
  
global {  
	file shape_file_roads  <- file("../includes/RoadCircleLanes.shp") ;
	file shape_file_bounds <- file("../includes/BoundsLaneRoad.shp") ;
	geometry shape <- envelope(shape_file_bounds);
	
	graph the_graph;  
	list roadsList of: road ; 
		
	
	init {  
		create road from: shape_file_roads with: [nbLanes::int(read("lanes"))] {
			geom_visu <- shape + nbLanes;
		}
		the_graph <- as_edge_graph(road);
	}   
	
	reflex createPeople when: cycle mod 20 = 0 and cycle < 400{
		roadsList <- (road as list);  
		create people number: 1 { 
			speed <-  (2.0 + 2 * length(people as list)) ;
			currentRoad <- first (roadsList);
			source <- first((currentRoad.shape).points);
			location <- source; 
			target <- last((currentRoad.shape).points);
			living_space <- 10.0;
			tolerance <- 0.1;
			lanes_attribute <- "nbLanes";
			obstacle_species <- [species(self)]; 
		}  
	}   
} 
	
species road  { 
	int nbLanes; 
	geometry geom_visu;
	aspect base {    
		draw geom_visu color: rgb("black") ;
	} 
}

species people skills: [driving]{ 
	float speed; 
	rgb color <- rgb(rnd(255),rnd(255),rnd(255)) ; 
	point target <- nil ; 
	point source <- nil;
	road currentRoad <- nil;
	reflex move when: target != nil {
		do goto_driving target: target on: the_graph speed: speed ; 
		switch target { 
			match location {
				currentRoad <- (roadsList select (each != currentRoad)) with_min_of (each distance_to self);
				source <- location;
				list<point> rls <- (currentRoad.shape).points;
				target <- first (rls) = source ? last(rls):first(rls);
			}
		}
	}
		
	aspect base {
		draw circle(10) color: color;
	}
}

experiment Simple type: gui {
	parameter "Shapefile for the roads:" var: shape_file_roads category: "GIS" ;
	parameter "Shapefile for the bounds:" var: shape_file_bounds category: "GIS" ;
	
	output {
		display city_display refresh_every: 1 {
			species road aspect: base ;
			species people aspect: base;
		}
	}
}




