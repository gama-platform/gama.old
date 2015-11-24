model tutorial_gis_city_traffic

global {
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	file shape_file_bounds <- file('../includes/bounds.shp'); 
	geometry shape <- envelope(shape_file_bounds);
	int nb_people <- 100;
	int day_time update: time mod 144 ;
	int min_work_start <- 36;
	int max_work_start <- 60;
	int min_work_end <- 84; 
	int max_work_end <- 132; 
	float min_speed <- 50.0;
	float max_speed <- 100.0; 
	float destroy <- 0.02;
	graph the_graph;
	int repair_time <- 6 ;
	
	list<building> residential_buildings;
	list<building>  industrial_buildings;
	
    agent_group_follower peoplefollower;
	list<list> testlist<-[[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0]];
	
	init {
		create building from: shape_file_buildings with: [type::string(read ('NATURE'))] {       
			if type='Industrial' {
				color <- rgb('blue') ;
			}  
	  	}
		create road from: shape_file_roads ;
		map<road,float> weights_map <- road as_map (each:: (each.destruction_coeff * each.shape.perimeter));
		the_graph <- as_edge_graph(road) with_weights weights_map;
		
		residential_buildings <- building where (each.type='Residential');
		industrial_buildings <- building where (each.type='Industrial') ;
		create people number: nb_people {
			 speed <- min_speed + rnd (max_speed - min_speed) ;
			 start_work <- min_work_start + rnd (max_work_start - min_work_start) ;
			 end_work <- min_work_end + rnd (max_work_end - min_work_end) ;
			 living_place <- one_of(residential_buildings) ;
			 working_place <- one_of(industrial_buildings) ;
			 location <- any_location_in (living_place);  
		}  
		create agentfollower 
		{
					  do analyse_cluster species_to_analyse:"people";
					  peoplefollower<-self;
		}
	}
	
	reflex update_graph{
	  create people number: 1
				{
			 speed <- min_speed + rnd (max_speed - min_speed) ;
			 start_work <- min_work_start + rnd (max_work_start - min_work_start) ;
			 end_work <- min_work_end + rnd (max_work_end - min_work_end) ;
			 living_place <- one_of(residential_buildings) ;
			 working_place <- one_of(industrial_buildings) ;
			 location <- any_location_in (living_place);  					
				}
		map<road,float> weights_map <- road as_map (each:: (each.destruction_coeff * each.shape.perimeter));
		the_graph <- the_graph with_weights weights_map;
		if (cycle>0)
		{
			testlist<-peoplefollower at_cycle ("multi_distribhistory","speed");
		
		}
		if (cycle=0) 
		{
			testlist<-[0,0,0];
			
		}
		write ""+((peoplefollower.distribhistory at {(reverse(peoplefollower.numvarmap)["cestmoi"]) as int,cycle-1}) );   
	}

	reflex repair_road when: (time mod repair_time) = 0 {
		let the_road_to_repair type: road value: (road as list) with_max_of (each.destruction_coeff) ;
		ask the_road_to_repair {
			set destruction_coeff value: 1 ;
		}
	}
}
entities {
	species agentfollower parent:agent_group_follower
	{
		
	}
	species building {
		string type; 
		rgb color <- rgb('gray')  ; 
		aspect base {
			draw shape color: color ;
		}
	}
	species road  {
		float destruction_coeff <- 1.0 max:2.0;
		int colorValue <- int(255*(destruction_coeff - 1)) update: int(255*(destruction_coeff - 1));
		rgb color <- rgb(min([255, colorValue]),max ([0, 255 - colorValue]),0)  update: rgb(min([255, colorValue]),max ([0, 255 - colorValue]),0) ;
		aspect base {
			draw  shape color: color ;
		}
	}
	species people skills: [moving]{
		rgb color <- rgb('yellow') ;
		building living_place <- nil ;
		building working_place <- nil ; 
		int start_work ;
		int end_work  ;
		string objective ; 
		point the_target <- nil ;
		
		reflex time_to_work when: day_time = start_work {
			 objective <- 'working' ;
			 the_target <- any_location_in (working_place);
		}
		reflex time_to_go_home when: day_time = end_work {
			 objective <- 'go home' ;
			 the_target <- any_location_in (living_place); 
		}  
		reflex move when: the_target != nil {
			path path_followed <- self goto [target::the_target, on::the_graph, return_path:: true];
			list<geometry> segments <- path_followed.segments;
			loop line over: segments {
				float dist <- line.perimeter;
				road ag <- road(path_followed agent_from_geometry line); 
				ask ag {
					destruction_coeff <- destruction_coeff + (destroy * dist / shape.perimeter);
				}
			}
			switch the_target { 
				match location { the_target <- nil ;}
			}
		}
		aspect base {
			draw circle(10) color: color;
		}
	} 
}

experiment road_traffic type: gui {
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS' ;
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	parameter 'Number of people agents' var: nb_people category: 'People' ;
	parameter 'Earliest hour to start work' var: min_work_start category: 'People' ;
	parameter 'Latest hour to start work' var: max_work_start category: 'People' ;
	parameter 'Earliest hour to end work' var: min_work_end category: 'People' ;
	parameter 'Latest hour to end work' var: max_work_end category: 'People' ;
	parameter 'minimal speed' var: min_speed category: 'People' ;
	parameter 'maximal speed' var: max_speed category: 'People' ;
	parameter 'Value of destruction when a people agent takes a road' var: destroy category: 'Road' ;
	parameter 'Number of steps between two road repairs' var: repair_time category: 'Road' ;
	
	output {
		display city_display refresh_every: 1 {
			species building aspect: base ;
			species road aspect: base ;
			species people aspect: base ;
		}

		display chart_displayHisto refresh_every: 1 { 
			chart name: 'Average Speed' type: histogram style: stack{
				datalist value: (testlist) legend: ["exp1", "exp2", "exp3"]style:stack;
			}
		}
		
	}
}