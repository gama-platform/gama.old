/**
 *  RoadTrafficSimple
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficSimple 
  
global {  
	file shape_file_roads  <- file('../includes/RoadCircleLanes.shp') ;
	file shape_file_bounds <- file('../includes/BoundsLaneRoad.shp') ;
	
	graph the_graph;  
	list roadsList of: road ; 
		
	
	init {  
		create road from: shape_file_roads with: [nbLanes::read('LANE_NB')] ;
		loop rd over: road as list {
			create road_display {
				set shape <- rd.shape buffer rd.nbLanes;
			}	
		}
		set the_graph <- as_edge_graph(list(road));
	}   
	
	reflex createPeople when: time mod 20 = 0 and time < 400{
		set roadsList <- (road as list);  
		create people number: 1 { 
			set speed <-  (2 + 2 * length(people as list)) ;
			set currentRoad <- first (roadsList);
			set source <- first((currentRoad.shape).points);
			set location <- source; 
			set target <- last((currentRoad.shape).points);
			set living_space <- 10.0;
			set tolerance <- 0.1;
			set lanes_attribute <- "nbLanes";
			set obstacle_species <- [species(self)]; 
		}  
		/*create people number: 1 { 
			set speed <-  (2 + 2 * length(people as list)) ;   
			set location <- last(((last (roadsList)).shape).points); 
			set target <- first(((first (roadsList)).shape).points); 
			set living_space <- 10.0;   
			set tolerance <- 0.1; 
			set lanes_attribute <- "nbLanes";   
		}*/ 
	}   
} 
entities {
	species road  { 
		int nbLanes; 
	}
	species road_display  {
		aspect base {    
			draw shape: geometry color: rgb('black') ;
		} 
	}
	species people skills: [driving]{ 
		float speed; 
		rgb color <- rgb([rnd(255),rnd(255),rnd(255)]) ; 
		point target <- nil ; 
		point source <- nil;
		road currentRoad <- nil;
		reflex move when: target != nil {
			do gotoTraffic target: target on: the_graph speed: speed ; 
			switch target { 
				match location {
					set currentRoad value: (roadsList select (each != currentRoad)) with_min_of (each distance_to self);
					set source <- location;
					let rls type: list of: point <- (currentRoad.shape).points;
					set target <- first (rls) = source ? last(rls):first(rls);
				}
			}
		}
		aspect base {
			draw shape: circle color: color size: 10 ;
		}
	}
}

environment bounds: shape_file_bounds ;

experiment Simple type: gui {
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	
	output {
		display city_display refresh_every: 1 {
			species road_display aspect: base ;
			species people aspect: base;
		}
	}
}




