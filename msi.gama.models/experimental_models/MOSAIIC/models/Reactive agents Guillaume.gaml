/**
 *  MOSAIIC Model - 9 decembre 2014
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficComplex
 
global schedules: [world] + traffic_signals + real_roads + (people sort_by (- 1000000 * each.segment_index_on_road + each.distance_to_goal)){   
	bool small <- true parameter: true;
	//file shape_file_roads  <- small ? file("../includes/roads_finalL93_small.shp") : file("../includes/roads_finalL93_delete_mathilde_2.shp") ;
	file shape_file_roads  <- small ? file("../includes/roads_7200_with_pm_small_small.shp") : file("../includes/roads_finalL93_delete_mathilde_2.shp") ;
	//file shape_file_nodes  <-  small ?file("../includes/node_finalL93_small.shp") : file("../includes/node_finalL93_delete_mathilde_2.shp");
	file shape_file_nodes  <-  small ?file("../includes/nodes_7200_with_pm_small_small.shp") : file("../includes/node_finalL93_delete_mathilde_2.shp");
	file shape_urgence  <-  small ?file("../includes/Evacuation_L93_small.shp") : file ("../includes/Evacuation_L93_big.shp") ;
	file shape_fuite <- file("../includes/Fuite_L93.shp");
	
	geometry shape <- envelope(shape_file_roads);
	graph road_network_speed;  
	graph road_network_custom;  
	int nb_people <- small ? 500: 2000;
	
	map<road,float> general_speed_map_speed;
	
	float proportion_speed_lane <- 1.0;
	float proportion_speed <- 0.25;
	float proportion_distance <- 0.25;
	
	float min_embouteillage_people_creation <- 30.0;
	float min_embouteillage_people_destruction <- 20.0;
	float speed_coeff_traffic_jam <- 3.0;
	float time_to_consider_traffic_jam <- 2#mn;
	float distance_see_traffic_jams <- 500.0; // changé, initialement 500m. 
	string sc_normal <- "sc_normal";
	string sc_evacuation <- "sc_evacuation";
	string sc_fuite <- "sc_fuite";
	
	int time_accident <- 4000;
	float prop_agent_evacuation <- 1.0;
	string scenario <- sc_evacuation;
	
	int nb_agents_in_traffic_jam update:people count (each.in_traffic_jam);
	int nb_driving_agent update:people count (each.real_speed > 1#km /#h);
	int nb_agent_speed_30 update:people count (each.real_speed < 30#km /#h); 
	int nb_agent_speed_zero update:people count (each.real_speed < 1#km /#h);
	int nb_traffic_signals_green update:node_ count (each.is_green);
	int nb_agent_update <- nb_people update:length(people);
	//int nb_agent_speed_zero update:liste_individus count (each.bloques); 
	//bloques
	//float prop_agent_zero = (nb_agent_speed_zero / nb_driving_agent) ; 
	
	float traffic_jam_length <- 0.0 update: sum(embouteillage collect (each.shape.perimeter));
	
	int traffic_jam_nb_roads <- 0 update: length(embouteillage);
	// float km_traffic_jam <- 0.0 ;
	
	float mean_real_speed <- 0.0 update: mean((people) collect (each.real_speed));
	//	float mean_real_speed <- 50°km/°h update: mean((people where not each.is_arrived) collect (each.real_speed));
	
	float time_end_final <- 3 #h;
	
	list<node_> traffic_signals;
	list<node_> connected_nodes;
	list<road> real_roads;
	
	float min_length <- 0.0;
	
 	list<node_> vertices;
	
	float proba_avoid_traffic_jam_global <- 0.4;
	float proba_know_map <- 0.5;
	int nb_avoid_max <- 10;
		
	init {  
		if scenario = sc_evacuation { 
			create evacuation_urgence from: shape_urgence;
		}
		if scenario = sc_fuite { 
			create point_fuite from: shape_fuite;
		}
		create node_ from: shape_file_nodes with:[is_traffic_signal::(string(read("type")) = "traffic_signals"), is_crossing :: (string(read("crossing")) = "traffic_signals")];
		ask evacuation_urgence {
			noeud_evacuation <- node_ closest_to self;
		}

		
	  /* 
	//********************************************* CREATION D UN RESEAU MANHATTAN
	
	list<geometry> geoms <- [] ;km/°h 
	int nb_vertical_roads <- 17 ;
	int nb_horizontal_roads <- 17 ;
	float y_max <- 3250.0 ;
	float x_max <- 3250.0 ;
	
	float dist_x_step <- x_max / (nb_vertical_roads + 1) ;
	float dist_x <- dist_x_step ;
	
	loop times: nb_vertical_roads {
		geometry ligne <- line([{0,dist_x}, {y_max,dist_x}]) ;
		geoms << ligne ;
		dist_x <- dist_x + dist_x_step ;
	}
	
	float dist_y_step <- y_max / (nb_horizontal_roads + 1) ;
	float dist_y <- dist_y_step ;
	
	loop times: nb_horizontal_roads {
		geometry ligne <- line([{dist_y,0}, {dist_y,x_max}]) ;
		geoms << ligne ;
		dist_y <- dist_y + dist_y_step ;
	}
	
	list<geometry> roads_geom <- split_lines(geoms) ;
	loop road_geom over:geoms{
		create road{
			shape <- road_geom ;
		}
	}
	write "shape créé" ;
	
	//********************************************* fin CREATION D UN RESEAU MANHATTAN 
*/
		

		create road from: shape_file_roads with:[nb_agents::int(read("NB_CARS")), name::string(read("name")),highway::string(get("highway")),junction::string(read("junction")),lanes::int(read("lanes")), maxspeed::float(read("maxspeed")), oneway::string(read("oneway")), lanes_forward ::int (get( "lanesforwa")), lanes_backward :: int (get("lanesbackw"))] {
			if maxspeed <= 0 {maxspeed <- 50 #km/#h;}
			if lanes <= 0 {lanes <- 1;}
			capacite_max <- 1+ int(lanes * shape.perimeter/(4.0));
			min_traffic_jam_people_destroy <- int(min([capacite_max, min_embouteillage_people_destruction]));
			min_traffic_jam_people_creation <- int( min([capacite_max, min_embouteillage_people_creation]));
			
			geom_display <- (shape + (2.5 * lanes));
			max_embouteillage_vitesse <- maxspeed / speed_coeff_traffic_jam;
			//do manage_oneway_attribute;
			
			
		}	
		
		real_roads <- road where (each.shape.perimeter > min_length);
		general_speed_map_speed <- road as_map (each::(each.shape.perimeter / each.maxspeed)); 
		
		
		road_network_speed <-  (as_driving_graph(road, node_))  with_weights general_speed_map_speed;
		road_network_custom <- (as_driving_graph(road, node_)) use_cache false;
		ask road {
			neighbours <- node_ at_distance distance_see_traffic_jams;
			loop times: nb_agents / 3 {
				ask world{do create_people_road(myself);}
			} 
		}
	
		list<string> roads_importance <- ["service","residential", "unclassified",  "tertiary_link", "tertiary",  "secondary_link", "secondary", "primary_link", "primary", "trunk_link", "trunk", "motorway_link", "motorway"];
		ask node_ {
			roads_in <- remove_duplicates(roads_in);
			roads_out <- remove_duplicates(roads_out);
			if (length(roads_in) > 1) {
				priority_roads <- roads_in where (road(each).junction = "roundabout");
				if (empty(priority_roads)) {
					int max_importance <- roads_in max_of (roads_importance index_of road(each).highway);
					priority_roads <- roads_in where ((roads_importance index_of road(each).highway) = max_importance);
				}
			}
			
		}
		connected_nodes <- node_ where (not empty(each.roads_in) and not empty(each.roads_out));
		do init_traffic_signal;
	//	loop times: nb_people{do create_people;} creation de base des automobilistes
	    
	}
	
	reflex scnerio_evac when: cycle = time_accident and scenario = sc_evacuation {
		ask (prop_agent_evacuation * length(people)) among people {
			target_node <- nil;
			color_behavior <- #red;
			targets <- [];
			current_path <- nil;
			size <- 20;
		}
	}
	
	reflex scnerio_fuite when: cycle = time_accident and scenario = sc_fuite {
		ask (prop_agent_evacuation * length(people)) among people {
			target_node <- nil;
			mode_fuite <- true ;
			recompute_path <- true ;
			current_path <- nil;
			color_behavior <- #blue;
			size <- 20;
		}
	}
	
	action create_people_road(road a_road) {
		create people  { 
			speed <- 50 #km /#h ;
			real_speed <- 50 #km /#h ;
			vehicle_length <- 3.0 #m;
			right_side_driving <- true;
			proba_lane_change_up <-1.0;// 0.5 + (rnd(500) / 500);
			proba_lane_change_down <- 1.0;//0.7+ (rnd(300) / 500);
			location <- any_location_in(a_road);
			//ask a_road { do register agent:myself lane: 0;}
			init_rd <- a_road;
			write "init_rd: " + init_rd;
			security_distance_coeff <- 2 * (1.5 - rnd(1000) / 1000);  
			proba_respect_priorities <- 1.0;
			proba_respect_stops <- [1.0];
			proba_block_node <- 0.0;
			proba_use_linked_road <- 0.0;
			max_speed <- 150 #km/#h;
			max_acceleration <- 1000.0;//(12 + rnd(500) / 100) #km/#h;
			speed_coeff <- 1.2 - (rnd(200) / 1000);
			proba_avoid_traffic_jam <- proba_avoid_traffic_jam_global;
		}
	}
	
	action create_people {
		create people  { 
			speed <- 50 #km /#h ;
			real_speed <- 50 #km /#h ;
			vehicle_length <- 3.0 #m;
			right_side_driving <- true;
			proba_lane_change_up <-1.0;// 0.5 + (rnd(500) / 500);
			proba_lane_change_down <- 1.0;//0.7+ (rnd(300) / 500);
			current_node <- one_of(connected_nodes);
			location <- current_node.location;
			security_distance_coeff <- 2 * (1.5 - rnd(1000) / 1000);  
			proba_respect_priorities <- 1.0;
			proba_respect_stops <- [1.0];
			proba_block_node <- 0.0;
			proba_use_linked_road <- 0.0;
			max_acceleration <- (12 + rnd(500) / 100) #km/#h;
			speed_coeff <- 1.2 - (rnd(200) / 1000);
			proba_avoid_traffic_jam <- proba_avoid_traffic_jam_global;
		}
	}
	action init_traffic_signal { 
		traffic_signals <- node_ where each.is_traffic_signal ;
		ask traffic_signals {
			stop << [];
		}
		
		list<list<node_>> groupes <- list<list<node_>>(traffic_signals simple_clustering_by_distance 50.0); 
		loop gp over: groupes {
			int cpt_init <- rnd(100);
			bool green <- flip(0.5);
			
			if (length(gp) = 1) {
				ask (first(gp)) {
					if (green) {do to_green;} 
					else {do to_red;}
					do compute_crossing;
				}	
			} else {
				point centroide <- mean (gp collect each.location);
				int angle_ref <- centroide direction_to first(gp).location;
				bool first <- true;
				float ref_angle <- 0.0;
				loop ns over: gp {
					bool green_si <- green;
					int ang <- abs((centroide direction_to ns.location) - angle_ref);
					if (ang > 45 and ang < 135) or  (ang > 225 and ang < 315) {
						green_si <- not(green_si);
					}
					ask ns {
						counter <- cpt_init;
						if (green_si) {do to_green;} 
							else {do to_red;}
						if (not empty(roads_in)) {
							if (is_crossing or length(roads_in) >= 2) {
								if (first) {
									list<point> pts <- road(roads_in[0]).shape.points;
									float angle_dest <- float( last(pts) direction_to road(roads_in[0]).location);
									ref_angle <-  angle_dest;
									first <- false;
								}
								loop rd over: roads_in {
									list<point> pts <- road(rd).shape.points;
									float angle_dest <- float(last(pts) direction_to rd.location);
									
									float ang <- abs(angle_dest - ref_angle);
									if (ang > 45 and ang < 135) or  (ang > 225 and ang < 315) {
										add road(rd) to: ways2;
									}
								}
							} else {do compute_crossing;}
						}
					}	
				}
			}
		}
		ask traffic_signals {
			loop rd over: roads_in {
				if not(rd in ways2) {
					ways1 << road(rd);
				}
			}
		} 
	}
	
	reflex end when: length(people) = 0  { // time = time_end_final{
		do pause;
	}
} 


	
species node_ skills: [skill_road_node] {
	bool is_traffic_signal;
	int counter ;
	rgb color_centr; 
	rgb color_fire;
	float centrality;	
	bool is_blocked <- false;
	bool is_crossing;
	list<road> ways1;
	list<road> ways2;
	bool is_green;
	int time_to_change <- 60;
	list<road> neighbours_tj;
	int pb_start <- 0;
	int pb_end <- 0;
	int id;
	
	action compute_crossing{
		if (is_crossing and not(empty(roads_in))) or (length(roads_in) >= 2) {
			road rd0 <- road(roads_in[0]);
			list<point> pts <- rd0.shape.points;						
			float ref_angle <-  float( last(pts) direction_to rd0.location);
			loop rd over: roads_in {
				list<point> pts2 <- road(rd).shape.points;						
				float angle_dest <-  float( last(pts2) direction_to rd.location);
				float ang <- abs(angle_dest - ref_angle);
				if (ang > 45 and ang < 135) or  (ang > 225 and ang < 315) {
					add road(rd) to: ways2;
				}
			}
		}
	}
	
	action to_green {
		stop[0] <-  ways2 ;
		color_fire <- rgb("green");
		is_green <- true;
	}
	
	action to_red {
		stop[0] <- ways1;
		color_fire <- rgb("red");
		is_green <- false;
	}
	reflex dynamic_node when: is_traffic_signal {
		counter <- counter + 1;
		
		if (counter >= time_to_change) { 
			counter <- 0;
			if is_green {do to_red;}
			else {do to_green;}
		} 
	}
	
	aspect base {    
		if (is_traffic_signal) {	
			draw circle(5) color: color_fire;
		} else {
			draw square(4) color: rgb("magenta");
		}
	} 
	
}

species road skills: [skill_road] { 
	string oneway;
	geometry geom_display;
	bool is_blocked <- false;
	embouteillage embout_route <- nil;
	int nb_people <- 0;
	int nb_people_tot <- 0;
	int nb_agents ;
	
	int lanes_backward;
	int lanes_forward;
	
	string junction;
	
	float max_embouteillage_vitesse;
	bool traffic_jam <- false;
	list<people> bloques <- [];
	int capacite_max ;
	float nb_bloques <- 0.0;
	int min_traffic_jam_people_destroy;
	int min_traffic_jam_people_creation;
	
	road next_linked_road;
	string highway;
	list<node_> neighbours;
	
	aspect geom {    
		draw geom_display border:  rgb("gray")  color: rgb("gray") ;
	}  
	
	aspect base {    
		draw shape color: is_blocked ? rgb("red"): rgb("black") ;
	} 
	
	aspect carto {
		if highway = "trunk" or highway="trunk_link" or highway = "motorway" or highway = "motorway_link" {draw (shape + 3) color: #red;}
		else if highway = "primary" or highway="primary_link"{draw (shape + 2) color: #orange;}
		else if highway = "secondary" or highway="secondary_link" {draw (shape + 1) color: #yellow;}
		else {draw shape color: #black;}
	}
	
	aspect base_gray {    
		draw shape color: rgb("gray") ;
	} 
	action manage_oneway_attribute {
		if (lanes_forward > 0 and lanes_backward > 0) {
			create road {
				 capacite_max <- 1+ int(lanes * shape.perimeter/(4.0));
				min_traffic_jam_people_destroy <- int(min([capacite_max, min_embouteillage_people_destruction]));
				min_traffic_jam_people_creation <- int( min([capacite_max, min_embouteillage_people_creation]));
				lanes <-myself.lanes_backward;
				shape <- polyline(reverse(myself.shape.points));
				maxspeed <- myself.maxspeed;
				max_embouteillage_vitesse <- maxspeed / speed_coeff_traffic_jam;
				geom_display  <- myself.geom_display;
				linked_road <- myself;
				myself.linked_road <- self;
			}
			lanes <- lanes_forward;
		} else {
			switch oneway {
				match "yes" {
										
				}
				match "-1" {
					shape <- polyline(reverse(shape.points));
				} 
				default {
					if (junction != 'roundabout') {
						create road {
							 capacite_max <- 1+ int(lanes * shape.perimeter/(4.0));
							min_traffic_jam_people_destroy <- int(min([capacite_max, min_embouteillage_people_destruction]));
							min_traffic_jam_people_creation <- int( min([capacite_max, min_embouteillage_people_creation]));
							lanes <- max([1, int (myself.lanes / 2.0)]);
							shape <- polyline(reverse(myself.shape.points));
							maxspeed <- myself.maxspeed;
							max_embouteillage_vitesse <- maxspeed / speed_coeff_traffic_jam;
							geom_display  <- myself.geom_display;
							linked_road <- myself;
							myself.linked_road <- self;
						}
						lanes <- int(lanes /2.0 + 0.5);	
					}
				}
			}
		}
		
	}
	reflex dynamic_road {
		nb_people <- length(all_agents);
		if (embout_route != nil) {
			bloques <- list<people>(nb_people = 0 ? [] : all_agents where (people(each).real_speed < max_embouteillage_vitesse));
			nb_bloques <- length(bloques) + 0.0;// /capacite_max ;
			if(nb_bloques < min_traffic_jam_people_destroy) {
				do maj_emboutillage_destruction;	
			} else if every(3) {
				do maj_embouteillage;
			}
		} else {
			if (nb_people > min_traffic_jam_people_creation) {
				bloques <- list<people>(nb_people = 0 ? [] : all_agents where (people(each).real_speed < max_embouteillage_vitesse));
				nb_bloques <- length(bloques) + 0.0;// /capacite_max ;
			
				if (nb_bloques > min_traffic_jam_people_creation) {
					do maj_emboutillage_creation;
				}	
			}
		}
		
	}
	
	action maj_emboutillage_destruction {
		ask embout_route {
			loop pb over: personnes_bloquees {
				pb.in_traffic_jam <- false;
			}
			do die;
		}
		ask neighbours {
			neighbours_tj >> myself;
		}
		embout_route <- nil;
		traffic_jam <- false;
	}
	
	action maj_emboutillage_creation {
		create embouteillage returns: eb with: [personnes_bloquees::bloques,route_concernee::self, shape::shape+5.0];
		embout_route <- first(eb);
		traffic_jam <- true;
		loop pb over: bloques {
			pb.in_traffic_jam <- true;
		}
		ask embout_route{
			personnes_bloquees<-myself.bloques;
		}
		
	}
	action maj_embouteillage {
		ask embout_route{
			loop pb over: personnes_bloquees {
				pb.in_traffic_jam <- false;
			}
			personnes_bloquees<-myself.bloques;
			loop pb over: personnes_bloquees {
				pb.in_traffic_jam <- true;
			}
		}	
	}
	
	
}

species embouteillage {
	list<people> personnes_bloquees;
	road route_concernee;
	float counter <- 0.0 update: counter + step;
	bool real_before <- false;
	bool real <- false ; 
	
	reflex maj_neighbours{
		real_before <- real;
		real <- counter > time_to_consider_traffic_jam;
		if (real and not real_before) {
			ask route_concernee.neighbours {
				neighbours_tj << myself.route_concernee;
			}
		} 
	}
	action maj_forme{
		shape <- polyline(personnes_bloquees collect each.location);
	}
	
	
	aspect base_width { 
		draw shape + 5.0 color: rgb("red");
	}
	
}

species evacuation_urgence {
	node_ noeud_evacuation ;
	aspect default {
		draw circle (100) color: rgb("orange");
	}
}

species point_fuite {
	aspect default {
		draw circle (100) color: rgb("pink");
	}
}
	
species people skills: [advanced_driving] schedules: [] { 
	rgb color <-rnd_color(255) ;
	rgb color_behavior <-#yellow;
	int size <- 8;
	bool in_traffic_jam <- false;
	node_ target_node;
	list<road> roads_traffic_jam;
	
	bool is_stopped <- false;
	bool recompute_path <- false;
	bool to_delete <- false;
	float proba_avoid_traffic_jam;
	node_ current_node;
	bool mode_avoid <- false;
	bool mode_fuite <- false;
	int cpt_avoid <- 0;
	road init_rd;
	
	
	reflex choose_target_node when:target_node = nil {
		if cycle >= time_accident {
			if scenario = sc_evacuation {
				target_node <- (evacuation_urgence with_min_of (each distance_to self)).noeud_evacuation;
			}
			else {
				target_node <- one_of(connected_nodes);
			}
		}
		else {
			target_node <- one_of(connected_nodes);
		}
		
		
	
		if(location distance_to target_node.location < 5){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			//if cycle < (time_accident - 200) {ask world {do create_people;}}
			do die;
		}
		
	}
	
	reflex choose_a_path when: (current_path = nil or recompute_path or final_target = nil)and target_node != nil { 
		if (recompute_path) {
			do recomputing_path(general_speed_map_speed);
			recompute_path <- false;
		} else {
			if (init_rd != nil) {
				current_path <- compute_path(graph: road_network_speed, target: target_node, on_road: init_rd);
				init_rd <- nil;
			} else {
				current_path <- compute_path(graph: road_network_speed, target: target_node);
			
			}
			if (current_path = nil) {
				if (current_road != nil) { 
					ask road(current_road) {
						do unregister(myself);
					}
				}
				//if cycle < time_accident {ask world {do create_people;}}
				do die;
			} else {
				if flip(proba_avoid_traffic_jam) and not empty(roads_traffic_jam) and length(current_node.roads_out) > 1 and ((current_node.roads_out count ((road(each).embout_route = nil) or not road(each).embout_route.real) ) > 0){
					bool tj <- false;
					loop rd over: current_path.edges {
						if (road(rd).embout_route != nil and road(rd).embout_route.real and rd in roads_traffic_jam) {
							tj <- true;
							break;	
						}
					}
					if (tj) {
						do recomputing_path(general_speed_map_speed);
					}
				}
			}
		}
	}
	
	reflex driving when: current_path != nil and final_target != nil {
		if (distance_to_goal = 0 and real_speed = 0) {
			proba_respect_priorities <- proba_respect_priorities - 0.1;
		} else {
			proba_respect_priorities <- 1.0;
		}
		do drive;
		if ((location distance_to target_node.location) < 5.0){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			//if cycle < time_accident {ask world {do create_people;}}
			do die;
		} 
	} 
	
	
	action compute_shortest_path (map<road,float> map_weights){
		map<road,float> rds <- [];
		loop rd over: roads_traffic_jam{
			float val <- map_weights[rd];
			rds[rd] <- val ;
			map_weights[rd] <-val * 100;
		}
		road_network_custom <- road_network_custom  with_weights map_weights;
		current_path <- compute_path(graph: road_network_custom, target: target_node, source: current_node);
		loop rd over: roads_traffic_jam{
			map_weights[rd] <- rds[rd];
		}		
	}
	
	action recomputing_path (map<road,float> map_weights) {
		if (mode_avoid and not mode_fuite) {
			cpt_avoid <- cpt_avoid + 1;
			if (cpt_avoid > nb_avoid_max) {
				mode_avoid <- false;
			}
		}
		if (not mode_fuite and not mode_avoid and flip(proba_know_map)) {
			do compute_shortest_path(map_weights);
		}
		else if (not mode_fuite){
			if (not mode_avoid) {
				mode_avoid <- true;
				cpt_avoid <- 0;
			} 
			list<road> possible_edges <- list<road>(road_network_custom out_edges_of current_node);
			list<node_> possible_nodes <- [];
			loop rd over: possible_edges {
				if (rd.embout_route = nil or not rd.embout_route.real or (rd.target_node = target_node)  ) {
					possible_nodes << node_(rd.target_node);	
				}
			}
			if not empty(possible_nodes) {
				list<float> vals <- [];
				node_ the_temp_target <- nil;
				loop nd over:possible_nodes {
					float dist <- nd distance_to target_node;
					if dist = 0 {
						the_temp_target<- nd;
						break;
					}
					float angle <- float(abs(angle_between(nd.location, location, target_node.location)));
					if (angle > 180) {angle <- 360 - angle;}
					float val <- angle = 0 ? 1 : 1/angle;
						vals << val ;
				}
				if (the_temp_target = nil) {
					int index <- 0;
					if (sum(vals)> 0) {
						index <- rnd_choice(vals);
					}
					
					the_temp_target <- possible_nodes[index];
				}
				current_path <- path_from_nodes(graph: road_network_custom, nodes: [current_node, the_temp_target]);
			}
			
			
		}
		else if (mode_fuite) {
			list<road> possible_edges <- list<road>(road_network_custom out_edges_of current_node);
			list<node_> possible_nodes <- [];
			loop rd over: possible_edges {
				if (rd.target_node = current_node)   {
					possible_nodes << node_(rd.target_node);	
				}
			}
			if not empty(possible_nodes) {
				targets <- [];
				current_path <- nil;
			
				float angle_max <- 0.0;
				node_ the_temp_target <- nil;
				loop nd over:possible_nodes {
					float angle <- float(abs(angle_between(nd.location, location, first(point_fuite).location)));
					if (angle > 180) {angle <- 360 - angle;}
					if angle > angle_max {
						angle_max <- angle ;
						the_temp_target <- nd ;
					}
				}
				current_path <- path_from_nodes(graph: road_network_custom, nodes: [current_node, the_temp_target]);
			}
		}
	}
	
	aspect base { 
		draw triangle(size) color: color_behavior rotate:heading + 90;	
	} 
	
	float test_traffic_jam(road a_road,float remaining_time) {
		if a_road.embout_route != nil and a_road.embout_route.real and (a_road in roads_traffic_jam) {
			current_path <- nil;
			return 0.0;
		} else {
			return remaining_time;
		}
	}
	
	float external_factor_impact(road new_road,float remaining_time) {
		current_node <- node_(new_road.source_node);
		if (mode_fuite ) {
			if (flip(0.9)) {
				recompute_path <- true;
				current_path <- nil;
				return remaining_time;
			} else {
				size <- 8;
				color_behavior <- #yellow;
				mode_fuite <- false;
			}
			
		}
		if (proba_avoid_traffic_jam > 0 and flip(proba_avoid_traffic_jam)) {
			
			roads_traffic_jam <- remove_duplicates(roads_traffic_jam + (current_node.neighbours_tj));// where (each.embout_route != nil and each.embout_route.real));// and not (each in roads_traffic_jam)));
			if (not empty(roads_traffic_jam) and  length(current_node.roads_out) > 1 and ((current_node.roads_out count ((road(each).embout_route = nil) or not road(each).embout_route.real) ) > 0)) {
				remaining_time <- test_traffic_jam(new_road, remaining_time);
				if (remaining_time > 0 and current_path != nil) {
					bool next <- false;
					loop rd over: current_path.edges {
						if (not next and current_node = road(rd).source_node) {
							next <- true;
						}
						if (next) {
							remaining_time <- test_traffic_jam(rd, remaining_time);
							if (current_path = nil) {
								recompute_path <- true;
								break;
							}
						}
					}
				}
			}
		}
		if (current_path != nil) {
			new_road.nb_people_tot <- new_road.nb_people_tot + 1;
		}
		return remaining_time;
	}
	
} 

experiment traffic_simulation_sc_exceptionnel type: gui {
	output {
		monitor "moniteur" value: length(people);
		/*display carte_embouteillage {
			species road aspect: base_gray refresh: false;
			species embouteillage aspect: base_width ;
		}*/
		
		display city_display2D {
			species road aspect: carto refresh: false;
			species node_ aspect: base refresh: false;
			species people aspect: base; 
			species evacuation_urgence;
			species point_fuite;
		}
		
		display Graphiques {
			chart "mean speed of people" type: series size:{0.5,0.5} position:{0.0,0.5} {
				data "mean speed of people" value: mean_real_speed color: #blue ;
			}
			
			chart "rapports" type: series size:{0.5,0.5} position:{0.5,3.5}{
				data "nb agents en mvt // nb agents" value: nb_driving_agent * 100 / (nb_agent_update) style: line color: #gray ;
				data "feux verts" value: (nb_traffic_signals_green * 100 / length(traffic_signals)) style: line color: #green ;
				data "routes embouteillees" value: (traffic_jam_nb_roads * 100 / length(road)) style: line color: #red ;
			}
			//chart "blocked roads" type: series size:{0.5,0.5} position:{0.5,3.5}{
			//	data "number of blocked roads" value: traffic_jam_nb_roads color: #red ;	
			//}
			
			chart "infos agents" type: series size:{0.5,0.5} position:{0.0,3.5}{
				data "nb  agents" value: length(people) style: line color: #black ; //nb_agent_update
				//data "nb agents" value: nb_agent_update style: line color: #black ;
				data "nb driving agent" value: nb_driving_agent style: line color: #red ;
				data "nb agents in t-jam" value: nb_agents_in_traffic_jam style: line color: #orange ;
				data "nb agent speed < 30 km/h" value: nb_agent_speed_30 color: #gray;
				data "nb agent speed == 0 km/h" value: nb_agent_speed_zero color: #purple;
			}
			chart "traffic jam length" type: series size:{0.5,0.5} position:{0.5,0.5}{
				data "traffic jam meters (cummulative)" value: traffic_jam_length color: #black;
			}
			/*chart "infos agents" type: series size:{0.5,0.5} position:{0.5,0.5}{
				data "prop. agent stoppé" value: (nb_agent_speed_zero / nb_driving_agent) color: #black;
			}*/
		}
	}
	
}
