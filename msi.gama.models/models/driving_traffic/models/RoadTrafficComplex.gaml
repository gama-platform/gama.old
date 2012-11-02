/**
 *  RoadTrafficComplex
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficComplex
 
global {   
	file shape_file_roads  <- file('../includes/ManhattanRoads.shp') ;
	file shape_file_bounds <- file('../includes/ManhattanBounds.shp') ;
	
	int nbGoalsAchived <- 0;
	graph the_graph;  
	list roadsList of: road ; 
	 
	init {  
		create road from: shape_file_roads with:[nbLanes::read('LANE_NB')];
		set roadsList <- road as list;
		loop rd over: road as list {
			create road_display {
				set shape <- rd.shape buffer (2 * rd.nbLanes);
			}	
		}
		set the_graph <-  (as_edge_graph(list(road))) with_optimizer_type "Dijkstra";
		create people number: 300 { 
			set speed <- 15 ;
			set target <- any_location_in (one_of(roadsList));
			set location <- any_location_in (one_of(roadsList));
			set living_space <-10;
			set tolerance <- 0.1;
			set lanes_attribute <- "nbLanes";
			set obstacle_species <- [species(self)]; 
		}   
	}
	
} 
entities {
	species road  { 
		int nbLanes;
		int indexDirection; 
		aspect base { 
			draw shape: geometry color: rgb('black') ;
		} 
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
		point targetBis <- nil ; 
		point previousLoc <- nil;
		bool normalMove <- true;
		float evadeDist <- 300.0;
		reflex move when: normalMove{
			set previousLoc <- copy(location);
			do goto_driving target: target on: the_graph speed: speed ; 
			switch location { 
				match target {
					set target <- any_location_in (one_of(roadsList));
					set nbGoalsAchived <- nbGoalsAchived +1;
				}
				match previousLoc {
					set targetBis <- last((one_of(roadsList where (each distance_to self < evadeDist)).shape).points);
					set normalMove <- false;
				}
			}
		}
		reflex EvadeMove when: !(normalMove){
			set previousLoc <- copy(location);
			do goto_driving target: targetBis on: the_graph speed: speed ; 
			switch location { 
				match targetBis {
					set normalMove <- true;
				}
				match previousLoc {
					set targetBis <- last((one_of(roadsList where (each distance_to self < evadeDist)).shape).points);
				}
			}
		}
		aspect base {
			draw shape: circle color: color size: 20 ;
		}
	}
} 
environment bounds: shape_file_bounds ;

experiment Complex type: gui {
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	
	output {
		display city_display refresh_every: 1 {
			species road_display aspect: base ;
			//species road aspect: base ;
			species people aspect: base;
		}
		monitor nbGoalsAchived value: nbGoalsAchived refresh_every: 1 ;
	}
}

