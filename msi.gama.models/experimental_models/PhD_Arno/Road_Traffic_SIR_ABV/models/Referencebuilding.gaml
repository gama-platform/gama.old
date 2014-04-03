model tutorial_gis_city_traffic

global {
	
	geometry focusOnShape <- rectangle (world.shape.width,world.shape.height) at_location {world.shape.width, world.shape.height/2};
	file shape_file_buildings <- shape_file('../includes/building.shp');
	file shape_file_roads <- shape_file('../includes/road.shp');
	file shape_file_bounds <- shape_file('../includes/bounds.shp');
	geometry shape <- envelope(shape_file_bounds);
	int nb_people <- 200;
	int day_time update: cycle mod 144 ;
	int min_work_start <- 36;
	int max_work_start <- 60;
	int min_work_end <- 84; 
	int max_work_end <- 132; 
	float min_speed <- 50.0;
	float max_speed <- 100.0; 
	graph the_graph;
	
	list<building> residential_buildings;
	list<building>  industrial_buildings;
	

	
	float delta <- 0.01 parameter: "Delta (I->R)";
	
	float natality <-0.0;
	

	
	init {
		
		
		create building from: shape_file_buildings with: [type::string(read ('NATURE'))] {
			if type='Industrial' {
				color <- rgb('blue') ;
			} 
		}
		create road from: shape_file_roads ;
		the_graph <- as_edge_graph(list(road));
		
		residential_buildings <- building where (each.type='Residential');
	    industrial_buildings <- building  where (each.type='Industrial') ;
	    
		create people number: nb_people {
			 speed <- min_speed + rnd (max_speed - min_speed) ;
			 start_work <- min_work_start + rnd (max_work_start - min_work_start) ;
			 end_work <- min_work_end + rnd (max_work_end - min_work_end) ;
			 living_place <- one_of(residential_buildings) ;
			 working_place <- one_of(industrial_buildings) ;
			 location <- any_location_in (living_place); 
			 distance_work_to_living_place <- working_place.location distance_to living_place.location;
			 shape<-circle(10);
			 age <-(rnd(100)/100*rnd(100)/100*rnd(100)/100)*100;
		} 
		ask 20 among people {
			is_infected<-true;
			color <- °red;
		}
	}
	
	
	
	reflex demoGraphy when: cycle > 10 {
		ask (nb_people*natality) among people{
			do die;
		}
		create people number: nb_people*natality {
			 speed <- min_speed + rnd (max_speed - min_speed) ;
			 start_work <- min_work_start + rnd (max_work_start - min_work_start) ;
			 end_work <- min_work_end + rnd (max_work_end - min_work_end) ;
			 living_place <- one_of(residential_buildings) ;
			 working_place <- one_of(industrial_buildings) ;
			 location <- any_location_in (living_place); 
			 distance_work_to_living_place <- working_place.location distance_to living_place.location;
			 shape<-circle(10);
			 age <-1;
			 is_susceptible <-true;
			 is_infected <-false;
			 is_immune <-false;
		}  
	}
	

}
entities {

	species building{
		string type; 
		rgb color <- rgb('gray')  ;
		aspect base {
			draw shape color: color ;
		}
	}
	

	
	species road {
		rgb color <- rgb('black') ;
		aspect base {
			draw shape color: color ;
		}
	}

	
	species people skills: [moving]{
		rgb color <- rgb('green') ;
		building living_place <- nil ;
		building working_place <- nil ;
		int start_work ;
		int end_work  ;
		string objectif ; 
		point the_target <- nil ;
		int age <-0;
		
		//Added to abstract data
		bool is_susceptible <-true;
		bool is_infected <- false;
		bool is_immune<-false;
		float distance_work_to_living_place;
		int has_been_affected_at <-0;
		
		reflex time_to_work when: day_time = start_work {
			 objectif <- 'working' ;
			 the_target <- any_location_in (working_place);
		}
		reflex time_to_go_home when: day_time = end_work {
			 objectif <- 'go home' ;
			 the_target <- any_location_in (living_place); 
		}  
		reflex move when: the_target != nil {
			do goto target: the_target on: the_graph ; 
			switch the_target { 
				match location {the_target <- nil ;}
			}
		}
		
		reflex infect when: is_infected{
		  ask people at_distance 10 {
			if flip(0.01) {
				if(is_infected = false){
					has_been_affected_at <-cycle;
				}
				is_susceptible <-false;
				is_infected <- true;
				color<-°red;			
			}
		  }
	    }
	    
	    reflex become_immune when: (is_infected and flip(delta)) {
        	is_infected <- false;
            is_immune <- true;
            color <- rgb("blue");
        }
	    	    
	    reflex age{
	    	age<-age+1;
	    } 
		
		aspect base {
			draw shape color:color;
		}
	}
}

experiment road_traffic type: gui {	
	output {
		display city_display type:opengl focus:focusOnShape{
			species building aspect: base ;
			species road aspect: base ;
			species people aspect: base ;

		    
		    
		
		}	
	}
}

