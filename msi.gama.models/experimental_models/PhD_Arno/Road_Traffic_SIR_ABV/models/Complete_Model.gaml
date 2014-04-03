model tutorial_gis_city_traffic

global {
	
	geometry focusOnShape <- rectangle (world.shape.width*1.5,world.shape.height) at_location {world.shape.width*1.5, world.shape.height/2};
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
	
	map<string,list<abstractPeopleAge>> pyramidAge;
	
	float delta <- 0.01 parameter: "Delta (I->R)";
	
	float natality <-0.0;
	
	
	//Layout Parameter
	
	//Building
	float abstractBuildingWorkPos <-world.shape.width*0.1;
	float abstractBuildingHomePos <-world.shape.width*0.2;
	float buildingWorkInterval <-100.0;
	float buildingHomeInterval <-5.0;
	
	//Road
	float abstractRoadPos <-world.shape.width*0.3;
	
	//People
	float abstractSPos <-world.shape.width*0.8;
	float abstractIPos <-world.shape.width*0.9;
	float abstractRPos <-world.shape.width*1.0;
	
	float peopleInterval <-5.0;
	
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
	
	reflex cleanMirrorsWithDeadTarget{
		ask abstractPeople{
			if(dead(target)){
				do die;
			}
		} 
		
		ask abstractPeopleAge{
			if(dead(target)){
				do die;
			}
		}
		
		ask abstractPeopleInfected{
			if(dead(target)){
				do die;
			}
		}
	}
	//////////////////////////LAYOUT INIT//////////////////////////////////////////
	reflex InitAbstractBuildingLayout when: time = 1{
		int curIndus<-0;
		int curRes <-0;
		ask abstractBuilding{	
			if(building(target).type = "Industrial"){
				location <-{abstractBuildingWorkPos ,world.shape.height - curIndus*buildingWorkInterval};
				curIndus<-curIndus+1;
			}
			else{
				location <-{abstractBuildingHomePos ,world.shape.height - curRes*buildingHomeInterval};
				curRes<-curRes+1;
			}	
		}		
	}
	
	reflex InitAbstractRoadLayout when: time = 1{
		int curRoad<-0;
		ask abstractRoad{	
		  shape<-line([{abstractRoadPos+curRoad*3,world.shape.height},{abstractRoadPos+curRoad*3,(world.shape.height-road(target).shape.perimeter)}]);
		  curRoad<-curRoad+1;
		}		
	}
	
	reflex InitAbstractPeopleLayout{
		int curS<-0;
		int curI<-0;
		int curR<-0;
		
		ask abstractPeople{			
			if (people(target).is_susceptible = true){
				location <-{abstractSPos ,world.shape.height - curS*peopleInterval};
				curS<-curS+1;
			}	
				
			if(people(target).is_infected = true){
				location <-{abstractIPos ,world.shape.height - curI*peopleInterval};
				curI<-curI+1;
			}
			
			if(people(target).is_immune =true){
				location <-{abstractRPos ,world.shape.height - curR*peopleInterval};
				curR<-curR+1;
			}
			
		}		
	}
	
	reflex UpdateAbstractPeopleInfected when:cycle>1{
		
		list<abstractPeopleInfected> Ss <- abstractPeopleInfected where(people(each.target).is_infected=false and people(each.target).is_immune=false);
        list<abstractPeopleInfected> Is <- abstractPeopleInfected where(people(each.target).is_infected=true);
        list<abstractPeopleInfected> Rs <- abstractPeopleInfected where(people(each.target).is_immune=true);
		
        //S
		float radius <- float(length(Ss));
		geometry bounds_geom <- circle(length(Ss));				
		float size <- sqrt((radius^2 * 3.14159265359) /(length(Ss)));
        list<geometry> rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
       
        loop while: (length(rectangles) < length(Ss)) {
          size <- size * 0.99;
          rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
         }
           
		int i1 <- 0;
        ask Ss{
            location <- (rectangles[i1]).location;
            i1 <- i1+ 1;
        }
        
        
        //I
		if(length(Is) >0){
			radius <- float(length(Is));	
			bounds_geom <- circle(length(Is)) translated_by {length(Ss),0};				
			size <- sqrt((radius^2 * 3.14159265359) /(length(Is)));
	        rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
	       
	        loop while: (length(rectangles) < length(Is)) {
	          size <- size * 0.99;
	          rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
	         }
	           
			i1 <- 0;
	        ask Is{
	            location <- (rectangles[i1]).location;
	            i1 <- i1+ 1;
	        }    
        }
        
        
        
         //R
        if(length(Rs) >0){
			radius <- float(length(Rs));
			bounds_geom <- circle(length(Rs)) translated_by {2*length(Ss),0};				
			size <- sqrt((radius^2 * 3.14159265359) /(length(Rs)));
	        rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
	       
	        loop while: (length(rectangles) < length(Rs)) {
	          size <- size * 0.99;
	          rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
	         }
	           
			i1 <- 0;
	        ask Rs{
	            location <- (rectangles[i1]).location;
	            i1 <- i1+ 1;
	        }
        }
        
        
        
        
	}
	
	
	reflex UpdatepyramidAge{
		pyramidAge <-[];
		int step <- 100 /20;
		loop i from:0 to: 20 { 
			int val_min <- i * step;
			int val_max <- (i + 1) * step;
			pyramidAge["["+val_min + "," + val_max+ "["] <- abstractPeopleAge where (people(each.target).age >= val_min and people(each.target).age < val_max );
		}
				
		int i <- 0;
		loop cat over: pyramidAge.keys {
			list<abstractPeopleAge> here <- pyramidAge[cat];
			loop j from: 0 to: length(here) - 1 {
				here[j].color<-rgb(people(here[j].target).age,0,0);
				here[j].location <- {i*20,world.shape.height-j*10};
			}
			i <- i+1;
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
	
	species abstractBuilding mirrors: list(building) {
	list<abstractBuilding> neigbhours update: abstractBuilding where ((each.location distance_to location < 200) and (location.y > each.location.y) and (location.x = each.location.x));
	abstractBuilding upper_cell update: neigbhours with_min_of (location distance_to each.location);
	float my_area <- building(target).shape.area; 


 	bool is_satisfied <- true update: not ((upper_cell != nil) and ( my_area< upper_cell.my_area) and (time >1));

    reflex upperSwap when: !is_satisfied{
		point tmp1Loc <- location;
		location <- upper_cell.location;
		upper_cell.location <- tmp1Loc;
	}	
	
	  aspect abstract {
	  	draw building(target).shape  color: building(target).color border: °black  at: location;	
	  }
	  
	  aspect area {	
	  	draw circle(my_area / 100)  color: rgb(rnd(255),rnd(255),rnd(255))  at: location;	
	  }

   }
	
	species road {
		rgb color <- rgb('black') ;
		aspect base {
			draw shape color: color ;
		}
	}
	
	species abstractRoad mirrors: list(road) {	
	  point ref_point <- shape.points[0] update: shape.points[0];
	  list<abstractRoad> neigbhours update: abstractRoad where ((each.ref_point distance_to ref_point < 400) and (location.x > each.location.x));
	  abstractRoad side_cell update: neigbhours with_min_of (ref_point distance_to each.ref_point);
	  float my_length <- road(target).shape.perimeter; 


 	  bool is_satisfied <- true update: not ((side_cell != nil) and ( my_length< side_cell.my_length) and (time >1));

      reflex sideSwap when: !is_satisfied{
		float tmp1Locx <- location.x;
		location <- {side_cell.location.x,location.y};
		side_cell.location <- {tmp1Locx,side_cell.location.y};
	  }	
		
	  aspect abstract {
	    draw shape  color: road(target).color border: °black  at: location;	  	
	  }
	}
	
	species abstractPeople mirrors: list(people) {
	  aspect abstract {
	    draw people(target).shape  color: people(target).color border: °black  at: location;	
	  	
	  }
	}
	
	species abstractPeopleAge mirrors: list(people) {	
	   rgb color;	
	  aspect abstract {
	      //draw people(target).shape  color: rgb((people(target).age/100)*255,0,0) border: rgb((people(target).age/100)*255,0,0)  at: location;	  	
	     draw people(target).shape  color: people(target).color border: people(target).color  at: location;	  	
	  
	  }
	}
	
	species abstractPeopleInfected mirrors: list(people) {	
	   rgb color;	
	  aspect abstract {
	       draw people(target).shape  color: people(target).color border: people(target).color  at: location;	  	  	
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
			text "reference model" size:50;
			species building aspect: base ;
			species road aspect: base ;
			species people aspect: base ;	
			text "tidying up" size:50 position:{world.shape.width * 1.2, 0.0, 0 };
			species abstractBuilding aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };
			species abstractRoad aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };
		    species abstractPeople aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };
		    
		    text "age pyramid" size:50 position:{world.shape.width * 2 * 1.2, 0.0, 0 };
		    species abstractPeopleAge aspect: abstract position: { world.shape.width * 2 * 1.2, 0.0, 0 };
		    
		    text "macro SIR" size:50 position:{world.shape.width * 3 * 1.2, 0.0, 0 };
		    species abstractPeopleInfected aspect: abstract position: { world.shape.width * 3 * 1.2, 0.0, 0 };
		}	
	}
}

