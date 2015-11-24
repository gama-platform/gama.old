/**
 *  MOSAIIC Model - 9 decembre 2014
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficComplex
 
global {   
	file shape_file_roads  <- file("../includes/data_validation/routes_simplifiees.shp") ;
	file shape_file_nodes  <- file("../includes/data_validation/noeud_simplifies.shp");
	
	//file shape_areas  <-  file("../includes/data_validation/bati_carreaux_fusion.shp");
	file shape_areas  <-  file("../includes/data_validation/ZF_rezone_L93.shp");
	file shape_file_boucle <- file("../includes/data_validation/boucles_periode_L93_v6_avec_supposition_tunnel.shp");
	file init_time_amorcage <- csv_file("../includes/P4_06H_07H.csv");
	file init_time <- csv_file("../includes/P1_7H_9H.csv");
	
	matrix OD_echange_init <- matrix(csv_file("../includes/MAT_ECHANGE_VL_P4.csv",";"));
	matrix OD_interne_init <- matrix(csv_file("../includes/MAT_INTERNE_VL_P4.csv",";"));
	matrix OD_transit_init <- matrix(csv_file("../includes/MAT_TRANSIT_VL_P4.csv",";"));
	matrix OD_echange <- matrix(csv_file("../includes/MAT_ECHANGE_VL_P1.csv",";"));
	matrix OD_interne <- matrix(csv_file("../includes/MAT_INTERNE_VL_P1.csv",";"));
	matrix OD_transit <- matrix(csv_file("../includes/MAT_TRANSIT_VL_P1.csv",";"));
	
	file file_ssp_speed_lane;
	file file_ssp_speed;
	file file_ssp_distance;
	file file_ssp_traffic_light ;
	bool save_results <- false;
	float save_result_frequency <- 30#mn;
	
	map<string, OD_area> ODs;
	
	int nb_recomputes <- 0;
	geometry shape <- envelope(shape_file_roads);
	graph road_network_speed_lane;  
	graph road_network_speed;  
	graph road_network_distance; 
	graph road_network_traffic_light;  
	graph road_network_custom;  
	int nb_people;
	float nb_people_factor <- 1.0;
	
	
	map general_speed_map_speed_lane;
	map general_speed_map_speed;
	map general_speed_map_distance;
	map general_speed_map_traffic_light;
	
	float proportion_speed_lane <-1.0;
	float proportion_speed <- 0.25;
	float proportion_distance <-0.25;
	
	float min_embouteillage_people_creation <- 30.0;
	float min_embouteillage_people_destruction <- 30.0;
	float speed_coeff_traffic_jam <- 5.0;
	float time_to_consider_traffic_jam <- 3#mn;
	float distance_see_traffic_jams <- 500.0;
	
	float time_end_init <- 1#h;
	float time_end <- time_end_init + 2#h;
	float time_end_final <- 3 #h;
	float nb_hours;
	
	int start_hour <- 6;
	int start_minute <- 0;
	
	list<node_> traffic_signals;
	list<node_> connected_nodes;
	list<OD_area> ca;
	list<road> real_roads;
	
	float min_length <- 0.0;
	
	
	int nb_people_supp <- 0;
	/*int nb_people_computed;
	int nb_people_calcul_ppc;
	int nb_deplace;
	int nb_people_parti;*/
	
	list<people> people_moving <- people where not(each.is_arrived) ;
	
	float current_factor_start;
	
	list<float> proportion;
	
	list<OD_area> ars;
	
	list<road> traffic_signal_road;
	
	list<list> components <-[[],[]];
	list<list> components2 <-[[],[]];
    node_ ref;
	list<node_> vertices;
	
	int nb_people_arrived;
	
	int nb_possible_recomputes;
	
	float proba_avoid_traffic_jam_global <- 1.0;
	float proba_know_map <- 0.5;
	int nb_avoid_max <- 10;
	
	string id_sim <- "(avoid_TJ_" +proba_avoid_traffic_jam_global+ "-know_map_" +proba_know_map + "-prop_speed_lane_" + proportion_speed_lane + ")";
	string shape_results  <-  "results_" + id_sim + "/roads_results";
	
	bool batch_mode <- false;
	float global_error;
	float global_error_sum;
	float global_error_tot;
	bool compute_global_error <- false;
	list<geometry> pbs;
		
	init {  
		if (not file_exists("results_" + id_sim) ) {
			file result_folder <- new_folder("results_" + id_sim);
			
			
		}
		create node_ from: shape_file_nodes with:[is_traffic_signal::bool(read("signal")), is_crossing :: bool(read("crossing"))];
		
		create road from: shape_file_roads with:[name::string(read("name")),highway::string(get("highway")),junction::string(read("junction")),lanes::int(read("lanes")), maxspeed::float(read("maxspeed")) #km/#h, oneway::string(read("oneway")), lanes_forward ::int (get( "lanesforwa")), lanes_backward :: int (get("lanesbackw"))] {
			capacite_max <- 1+ int(lanes * shape.perimeter/(4.0));
			min_traffic_jam_people_destroy <- int(min([capacite_max, min_embouteillage_people_destruction]));
			min_traffic_jam_people_creation <- int( min([capacite_max, min_embouteillage_people_creation]));
			geom_display <- (shape + (2.5 * lanes));
			max_embouteillage_vitesse <- maxspeed / speed_coeff_traffic_jam;
		}	
		ask road {neighbours <- node_ at_distance distance_see_traffic_jams;}
		real_roads <- road where (each.shape.perimeter > min_length);
		write "build road";
		general_speed_map_speed_lane <- road as_map (each::(each.shape.perimeter / each.maxspeed/ each.lanes)); 
		general_speed_map_speed <- road as_map (each::(each.shape.perimeter / each.maxspeed)); 
		general_speed_map_distance <- road as_map (each::each.shape.perimeter); 
		
		road_network_speed_lane <-  (as_driving_graph(road, node_))  with_weights general_speed_map_speed_lane;
		vertices <- list<node_>(road_network_speed_lane.vertices);
		loop i from: 0 to: length(vertices) - 1 {
			vertices[i].id <- i; 
		}
		
			
		traffic_signal_road <- (road where (node_(each.target_node).is_traffic_signal));
		general_speed_map_traffic_light <- road as_map (each::(each.shape.perimeter / each.maxspeed* ((each in traffic_signal_road) ? 1000 : 1)));
		
		road_network_speed <-  (as_driving_graph(road, node_))  with_weights general_speed_map_speed;
		road_network_distance <-  (as_driving_graph(road, node_))  with_weights general_speed_map_distance;
		road_network_traffic_light <-  (as_driving_graph(road, node_))  with_weights general_speed_map_traffic_light;
		road_network_custom <- (as_driving_graph(road, node_)) use_cache false;
		
		do fill_matrix;
		
	
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
		write "build graphs";
		connected_nodes <- node_ where (not empty(each.roads_in) and not empty(each.roads_out));
		do init_traffic_signal;
		write "init traffic signal";
		do init_area;
		write "init areas";
		do init_od od_internal: OD_interne_init od_transit: OD_transit_init od_echange:OD_echange_init;
		write "init OD";
		do init_boucle;
		//create boucle from: shape_file_boucle with: [name::string(get("nom_boucle")), comptage::string(get("comptage"))];
		write "init boucle";
		do init_proportion (matrix(init_time_amorcage));
		current_factor_start <- first(proportion);
		remove current_factor_start from: proportion;
		
		//save boucle type:"shp" to: "results_" + id_sim+ "/boucles.shp"  with:[name::"nom_boucle", comptage::"comptage"];
		//save OD_area type:"shp" to: "results_" + id_sim+ "/area.shp"  with: [name::"ID"];
		
		//do test_ssp;
		
	}
	
	action test_ssp {
		float t1 <- 0.0;
		float t2 <- 0.0;
		string strategy <- "speed lane" ;
			map<road,float> map_weights <- map<road, float>(strategy="speed lane" ? general_speed_map_speed_lane : (strategy="speed" ? general_speed_map_speed : (strategy="distance" ? general_speed_map_distance : general_speed_map_traffic_light)));
			road_network_custom <- road_network_custom with_weights map_weights;
	
		loop times: 5000 {
			node_ ns <- one_of(node_);
			node_ nt <- one_of(node_);
			road_network_custom <- (road_network_custom with_optimizer_type "Dijkstra") use_cache false;
			float t <- machine_time;
			path current_path <- road_network_custom path_between (ns,nt);
			t1 <- t1 + machine_time - t;
			road_network_custom <- (road_network_custom with_optimizer_type "AStar") use_cache false;
			t <- machine_time;
			current_path <- road_network_custom path_between (ns,nt);
			t2 <- t2 + machine_time - t;
		}
		write "temps Dijkstra : " + t1 + " temps Astar : " + t2;
	}
	
	action fill_matrix {
		int nb <- length(vertices);
		if (proportion_speed_lane) > 0 {
			file_ssp_speed_lane<- csv_file("../includes/data_validation/shortest_paths_speed_lanes_2.csv",";");
			 write "file_ssp_speed_lane: " + length(file_ssp_speed_lane);
		}
		if (proportion_speed_lane < 1.0 and proportion_speed > 0.0) {
			file_ssp_speed <- csv_file("../includes/data_validation/shortest_paths_speed_2.csv",";");
			write "file_ssp_speed: " + length(file_ssp_speed);
		}
		if ((proportion_speed_lane + proportion_speed) < 1.0 and  proportion_distance> 0.0) {
			file_ssp_distance<- csv_file("../includes/data_validation/shortest_paths_distance_2.csv",";");
			write "file_ssp_distance: " + length(file_ssp_distance);
		}
		if ((proportion_speed_lane + proportion_speed + proportion_distance) < 1.0)  {
	 		file_ssp_traffic_light <- csv_file("../includes/data_validation/shortest_paths_traffic_light_2.csv",";");
			write "file_ssp_traffic_light: " + length(file_ssp_traffic_light); 
		}
	}
	
	action init_proportion (matrix time_prop) {
		proportion <- []; 
		loop i from: 1 to: time_prop.rows - 1 {
			proportion << float(time_prop[2,i]);
		}
	}
	
	action init_area {
		create OD_area from: shape_areas with:[name::string(read("ID_NOEUD"))];
		ODs <- OD_area as_map (each.name :: each);
		ars <- list(OD_area);
		
		ask OD_area {
			nodes <- node_ overlapping self;
			if (empty(nodes)) {
				road cr <- (road at_distance 10) with_min_of (each distance_to self);
				if (cr = nil) {
					 cr <- (road closest_to self);
				}
				
				nodes <-list<node_>([cr.target_node]);
			} 
		}
	}
	
	action fill_OD(matrix mat) {
		loop i from: 1 to: mat.rows - 1 {
			string ids <- string(int(mat[0,i]));
			string idt <- string(int(mat[1,i]));
			OD_area aas <- ODs[ids];
			OD_area aat <- ODs[idt];
			if (aas != nil and aat != nil) {
				float val <- float(mat[2,i]);
				aas.val_source <- aas.val_source  + val;
				aas.vals_targets[aat] <- aas.vals_targets[aat] + val;
			} 
		} 
	}
	
	action init_od(matrix od_internal, matrix od_transit, matrix od_echange) {
		ask  ars {
			val_source <- 0.0;
			vals_targets <- [];
		}
		do fill_OD(od_internal);
		do fill_OD(od_transit);
		do fill_OD(od_echange);
		write "matrix loaded";
		float source_sum <- sum (ars collect each.val_source);
		float cs <- 0.0;
		ask ars{
			val_s <- val_source / source_sum;
			cs <- cs + val_s;
			val_source <- cs;
			float sum_vts <- sum(vals_targets.values);
			if (sum_vts > 0) {
				float cts <- 0.0;
				loop aa over: vals_targets.keys {
					float valt <-vals_targets[aa] / sum_vts;
					cts <- cts + valt;
					vals_targets[aa] <- cts;
					aa.val_t <- aa.val_t + valt;
				}
			} 
		}
		nb_hours <- time = 0 ? time_end_init / 1#h : (time_end - time_end_init) / 1 #h;
		nb_people <- int(nb_hours * int(source_sum / nb_people_factor));
	 	write "Nb total of people for the " + nb_hours +" hours : " + int(nb_people);
		
	}
	 
	action create_people {
		create people  { 
		//	nb_people_computed <- nb_people_computed + 1;
			speed <- 130 #km /#h ;
			real_speed <- 130 #km /#h ;
			vehicle_length <- 3.0 #m;
			right_side_driving <- true;
			proba_lane_change_up <-1.0;// 0.5 + (rnd(500) / 500);
			proba_lane_change_down <- 1.0;//0.7+ (rnd(300) / 500);
			bool ok <- false;
			loop while: not ok {
				float rand_nb <- rnd(100000) / 100000;
				loop aas over: ars {
					if (rand_nb < aas.val_source) {
						source_area <- aas;
						break;
					}
				}
				if (source_area != nil) {
					rand_nb <- rnd(100000) / 100000;
					loop aat over: source_area.vals_targets.keys {
						if (rand_nb < source_area.vals_targets[aat]) {
							target_area <- aat;
							break;
						}
					}
					
					if (target_area != nil) {
						ok <- true;
					}
					
				}
				
			}
			
			bool one <- length(OD_area(source_area).nodes) = 1;
			current_node <-  one_of(OD_area(source_area).nodes);
				
			
			
			location <- current_node.location;
			
			security_distance_coeff <- 2 * (1.5 - rnd(1000) / 1000);  
			proba_respect_priorities <- 1.0;
			proba_respect_stops <- [1.0];
			proba_block_node <- 0.0;
			proba_use_linked_road <- 0.0;
			max_acceleration <- (12 + rnd(500) / 100) #km/#h;
			speed_coeff <- 1.2 - (rnd(200) / 1000);
			proba_avoid_traffic_jam <- proba_avoid_traffic_jam_global;
			float alea <- rnd(1000) / 1000;
			if (alea < proportion_speed_lane) {strategy<-"speed lane";} 
			else if (alea < (proportion_speed_lane + proportion_speed)) {strategy<-"speed";} 
			else if (alea < (proportion_speed_lane + proportion_speed + proportion_distance)) {strategy<-"distance";} 
			else {strategy<-"traffic light";}
		
			
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
				ask (node_(first(gp))) {
					if (green) {do to_green;} 
					else {do to_red;}
					do compute_crossing;
				}	
			} else {
				point centroide <- mean (gp collect (node_(each)).location);
				int angle_ref <- centroide direction_to node_(first(gp)).location;
				bool first <- true;
				float ref_angle <- 0.0;
				loop si over: gp {
					node_ ns <- node_(si);
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
	
	
	action init_boucle {
		create boucle from: shape_file_boucle with: [name::string(get("nom_boucle")), comptage::string(get("comptage"))];
		ask boucle {
			//if  (each.name = "" or (empty(OD_area overlapping self))) {do die;} 
			if  (each.name = "" or (not (world overlaps self))) {do die;} 
			else {
				list<road> roads_close <- road at_distance 100;
				if (empty(roads_close)) {do die;}
				else {
					the_road <- roads_close with_min_of (each distance_to self);
					file data_file_2012 <- csv_file("../includes/_boucles_test_11_octobre_2012/" + name+ ".csv",";");
					file data_file_2013 <- csv_file("../includes/_boucles_test_10_octobre_2013/" + name+ ".csv",";");
					data_2012 <- data_file_2012.exists;
					data_2013 <- data_file_2013.exists;
					if (data_2012 or data_2013) {
						if (data_2012) {
							matrix data_boucle_2012 <- matrix(data_file_2012);
							int nb_rows <- data_boucle_2012.rows;
							bool save_data <- false;
							loop i from: 1 to: nb_rows - 1 {
								if (not save_data) {
									string date <- string(data_boucle_2012[0,i]);
									if date = nil or date ="" {break;}
									
									list<string> dateL <- date split_with ":";
									int h_t <- int(dateL[0]);
									int m_t <- int(dateL[1]);
									save_data <- h_t >= start_hour and m_t >= start_minute;	
									
								} 
								if (save_data) {
									observed_data << float(data_boucle_2012[1,i]) / nb_people_factor;
								}
							}
						}
						if (data_2013) {
							matrix data_boucle_2013 <- matrix(data_file_2013);
							int nb_rows <- data_boucle_2013.rows;
							bool save_data <- false;
							loop i from: 1 to: nb_rows - 1 {
								if (not save_data) {
									string date <- string(data_boucle_2013[0,i]);
									if date = nil or date ="" {break;}
									list<string> dateL <- date split_with ":";
									int h_t <- int(dateL[0]);
									int m_t <- int(dateL[1]);
									save_data <- h_t > start_hour and m_t > start_minute;	
								} 
								if (save_data) {
									observed_data << float(data_boucle_2013[1,i]) / nb_people_factor;
								}
							}
						}
						
					} else {
						do die;
					}
				}
			}
		}
	}
	
	reflex change_start_time when: every(30 #mn) {
		current_factor_start <- first(proportion);
		remove current_factor_start from: proportion;
	}
	
	float reste;
	reflex people_creation when: time <= time_end {
		//if (every(10 #mn)){write string(cycle) + " -> " + current_factor_start;}
		//nb_people_computed <- nb_people_computed - nb_people_supp;
		float val <- reste + (current_factor_start * nb_people / (30 * 100 * 60)) + nb_people_supp;
		int nb <-int(val);
		//nb_people_parti <- nb_people_parti + nb;
		reste <- val - nb;
		int cd <- 0;
		loop times:nb{ 
			do create_people;
		}
		nb_people_supp <- 0;
	} 
	
	
	
	reflex general_dynamic {
		float t <- machine_time;
		ask traffic_signals {
			do dynamic_node;
		}
		
		t_1 <- t_1 + machine_time - t;
		t <- machine_time;
		if (every(5)) {
			ask real_roads {
				do dynamic_road;
			}	
		}
		
		t_2 <- t_2 + machine_time - t;
		t <- machine_time;
		
		ask people where (each.target_node = nil ){
			do choose_target_node;
		}
		t_3 <- t_3 + machine_time - t;
		t <- machine_time;
	
		ask people where ((each.current_path = nil or each.recompute_path or each.final_target = nil)and each.target_node != nil) {
			 do choose_a_path; 
		}
		t_4 <- t_4 + machine_time - t;
		t <- machine_time;
	
		people_moving <- (people where (each.current_path != nil and each.final_target != nil ));
		
		t5_4 <- t5_4 + machine_time - t;
		float t2 <- machine_time;
		ask people_moving {
			val_order <- - 1000000 * segment_index_on_road + distance_to_goal;
		}
		people_moving <- people_moving sort_by each.val_order;
		t5_3 <- t5_3 + machine_time - t2;
		t2 <- machine_time;
		ask people_moving{
			 do driving;
			 time_to_arrive <- time_to_arrive + step;
			 mean_real_speed <- mean_real_speed + real_speed;
		}
		t5_6 <- t5_6 + machine_time - t2;
		t_5 <- t_5 + machine_time - t;
		t <- machine_time;
		ask people where (each.target_node != nil and (each.location distance_to each.target_node.location < 5)){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			is_arrived <- true;
			if (target_area != nil) {
				target_area.nb_out <- target_area.nb_out  + 1;
			}
			nb_people_arrived <- nb_people_arrived + 1;
			//write name + " is arrived";
			do die;
		}
		t_6 <- t_6 + machine_time - t;
		t <- machine_time;
	
		if (time <=time_end ) {
			ask boucle {
				do store_data;
			}
		}
		
		t_7 <- t_7 + machine_time - t;
		t <- machine_time;
		
		if (compute_global_error) {
			ask boucle {
				global_error_sum <- global_error_sum + error;
				global_error_tot <- global_error_tot + observed_value;
			}
			global_error <- global_error_sum / global_error_tot * 100;
		}
		if (time > time_end_init) {
			ask road {do compute_nb_people;}
		}
	
		
		if (save_results and ((int(time) mod int(save_result_frequency)) = 0)) {
			save road type:"shp" to:(shape_results + "_" + time + ".shp") with: [nb_people::"nb_people", nb_people_tot::"nb_people_tot", oneway::"oneway", lanes::"lanes", is_blocked::"is_blocked", traffic_jam::"traffic_jam"];
		}
		if (every (6 #mn) and time <= time_end ) {
		/* 	write (string(cycle) + " : nb_people_computed -> " + nb_people_computed);
			write (string(cycle) + " : nb_people_calcul_ppc -> " + nb_people_calcul_ppc);
			write (string(cycle) + " :  nb_deplace -> " + nb_deplace);
			write (string(cycle) + " : nb_people -> " + nb_people);
			write (string(cycle) + " : nb_people_arrived -> " + nb_people_arrived);
			write (string(cycle) + " : nb_people_parti -> " + nb_people_parti);*/
			
			save road  type:"shp" to: "results_" + id_sim+ "/roads_" + cycle + ".shp" with: [nb_people::"NB"];
			if (not empty(embouteillage) ) {
				save embouteillage type:"shp" to: "results_" + id_sim+ "/embouteillages_" + cycle + ".shp" ;
			}
			save string(cycle) + "," + string(nb_people) + "," + nb_people_arrived + "," + global_error to:  "results_" + id_sim+"/nb_people.csv" ;
			
		}
		
	}
	
	float t_1;
	float t_2;
	float t_3;
	float t_4;
	float t_4_1;
	float t_4_2;
	float t_4_3;
	float t_4_4;
	float t_5;
	float t5_1;
	float t5_2;
	float t5_3;
	float t5_4;
	float t5_6;
	
	float t_6;
	float t_7;
	reflex write_time_info when: every(30 #mn){
		write "******* " + cycle + " ********";
		write "time for traffic_signals dynamic: " + t_1;
		write "time for road dynamic: "  + t_2;
		write "time for choose_target_node dynamic: " + t_3;
		write "time for choose_a_path dynamic: "  + t_4;
		write "time for computing a shortest path: "  + t_4_1;
		write "time for loading a shortest path: "  + t_4_2;
		write "time for testing path: "  + t_4_3;
		write "time for testing path 2 + computing: "  + t_4_4;
		write "time for drive dynamic: "  + t_5;
		write "time for external factor 1: "  + t5_1;
		write "time for driving: "  + t5_2;
		write "sort people moving: "  + t5_3;
		write "filter people moving: "  + t5_4;
		write "driving action: "  + t5_6;
	
		write "time for unregister dynamic: " + t_6;
		write "time for boucle dynamic: "  + t_7;
		
		ask road {
			nb_cars <- length(all_agents);
		}
		save road type: "shp" to: "roads_" + cycle + ".shp" with:[nb_cars::"NB_CARS"];
	}
	
	reflex end_init when: time = time_end_init {
		do init_od od_internal: OD_interne od_transit: OD_transit od_echange:OD_echange;
		
		do init_proportion (matrix(init_time));
		compute_global_error <- true;
		current_factor_start <- first(proportion);
	}
	
	

	reflex end when: (time = max([time_end_final,time_end])) and not batch_mode{
		save road type:"shp" to: "results_" + id_sim+ "/passages.shp"  with: [nb_vehicles::"NB",name::"name",highway::"highway",junction::"junction",lanes::"lanes", maxspeed::"maxspeed", oneway::"oneway", lanes_forward ::"lanesforwa", lanes_backward ::"lanesbackw"];
		do pause;
	}
} 

species boucle {
	string comptage;
	list<float> observed_data;
	bool data;
	list<float> simulated_data_S1;
	list<float> simulated_data_S2;
	int nb_vehicles_S1;
	int nb_vehicles_S2;
	int simulated_value_S1;
	int simulated_value_S2;
	float error;
	float observed_value;
	float time_step <- 6#mn;
	road the_road;
	list people_on_road_S1;
	list people_on_road_S2;
	int current_hour <- start_hour;
	int current_min <- start_minute;
	bool data_2012;
	bool data_2013;
	
	aspect base {
		draw circle(20) color: rgb("magenta");
		
	}
	
	action store_data {
		if (comptage = "FD") {
			list<agent> ags <- (list<agent>(the_road.agents_on[0] accumulate each));
			nb_vehicles_S1 <- nb_vehicles_S1+ length(ags - people_on_road_S1);
			people_on_road_S1 <-copy(the_road.all_agents); 
		} else {
			nb_vehicles_S1 <- nb_vehicles_S1+ length(the_road.all_agents - people_on_road_S1);
			people_on_road_S1 <-copy(the_road.all_agents); 
		}
		if (the_road.linked_road != nil) {
			if (comptage = "FD") {
				list<agent> ags <- (list<agent>(road(the_road.linked_road).agents_on[0] accumulate each));
				nb_vehicles_S2 <- nb_vehicles_S2+ length( ags- people_on_road_S2);
				people_on_road_S2 <- copy(the_road.all_agents); 
			} else {
				nb_vehicles_S2 <- nb_vehicles_S2+ length(road(the_road.linked_road).all_agents - people_on_road_S2);
				people_on_road_S2 <-copy(road(the_road.linked_road).all_agents); 	
			}
		}
		if (every(time_step)) {
			current_min <- int(current_min + time_step/#mn);
			if (current_min >= 60) {
				current_min <- current_min - 60;
				current_hour <- current_hour + 1;
			}
			simulated_data_S1 << nb_vehicles_S1;
			simulated_data_S2 << nb_vehicles_S2;
			simulated_value_S1 <- nb_vehicles_S1;
			simulated_value_S2 <- nb_vehicles_S2;
			nb_vehicles_S1 <-0;
			nb_vehicles_S2 <-0;
			observed_value <- first(observed_data);
			switch comptage {
				match "S1" {
					error <- abs(simulated_value_S1 -observed_value);
				} match "S2" {
					error <- abs(simulated_value_S2 -observed_value);
				} match "FD" {
					error <- abs(simulated_value_S1 -observed_value);
				} match "CU" {
					error <- abs(simulated_value_S1 + simulated_value_S2 -observed_value);
				} match "" {
					error <- abs(simulated_value_S1 -observed_value);
				}
			}
			remove observed_value from: observed_data;	
			
			save [current_hour,current_min,observed_value,simulated_value_S1,simulated_value_S2, error] type: "csv" to: ("results_" + id_sim + "/"+(name + "_result.csv"));
		}
	}
}



species OD_area  {
	list<node_> nodes;
	rgb color <- rnd_color(255);
	float val_s;
	float val_t;
	float val_source <- 0.0;
	map<OD_area,float> vals_targets;
	int nb_in;
	int nb_out;
	aspect base {
		draw (shape.contour + 2.0) empty: true color: color;
	}
	
	aspect base_in {
		draw circle(nb_in) color: color;
	}
	
	aspect base_out {
		draw circle(nb_out) color: color;
	}
}
	
species node_ skills: [skill_road_node] frequency: 0{
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
	action dynamic_node  {
		counter <- counter + 1;
		
		if (counter >= time_to_change) { 
			counter <- 0;
			if is_green {do to_red;}
			else {do to_green;}
		} 
	}
	aspect centrality {
		draw circle(5) color: color_centr;
	}
	
	aspect geom3D {
		if (is_traffic_signal) {	
			draw box(1,1,10) color:rgb("black");
			draw sphere(5) at: {location.x,location.y,12} color: color_fire;
		} else if (is_blocked) {
			draw sphere(5) color: rgb("magenta");
		}
	}
	
	aspect base {    
		
		if (is_traffic_signal) {	
			draw circle(5) color: color_fire;
		} else {
			draw square(4) color: rgb("magenta");
		}
		draw string(id) color: #black size: 20;
	} 
	aspect pb {    
		
		if (pb_start > 0) {	
			draw circle(4 + pb_start) color: #red;
		} 
		if (pb_end > 0) {	
			draw circle(4 + pb_end) color: #blue;
		}
	} 
}

species road skills: [skill_road] frequency: 0 { 
	string oneway;
	geometry geom_display;
	bool is_blocked <- false;
	embouteillage embout_route <- nil;
	int nb_people <- 0;
	int nb_people_tot <- 0;
	int nb_cars;
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
		draw shape end_arrow: 5 color: rgb("gray") ;
	} 
	
	action dynamic_road {
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
	
	int nb_vehicles;
	list<agent> people_on_road_S1;
	action compute_nb_people {
			nb_vehicles <- nb_vehicles+ length(all_agents - people_on_road_S1);
			people_on_road_S1 <-copy(all_agents); 
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
	
	aspect base { 
		if (real) {
			draw shape color: rgb("red") border:rgb("red") ;	
		}
	}
	aspect base_width { 
		draw shape + 5.0 color: rgb("red");
	}
	
}

	
species people skills: [advanced_driving] frequency: 0{ 
	rgb color <-rnd_color(255) ;
	rgb color_behavior <-#yellow;
	bool in_traffic_jam <- false;
	node_ target_node;
	OD_area source_area;
	OD_area target_area;
	list<road> roads_traffic_jam;
	
	bool is_stopped <- false;
	bool recompute_path <- false;
	bool to_delete <- false;
	float proba_avoid_traffic_jam;
	string strategy;
	bool is_arrived <- false;
	float time_to_arrive <- 0.0;
	float mean_real_speed <- 0.0;
	node_ current_node;
	//int index_deplacement;
	bool mode_avoid <- false;
	int cpt_avoid <- 0;
	float val_order;
	
	
	action choose_target_node  {
		if (target_area != nil) {
			target_node <- one_of(target_area.nodes);
		//	target_area.nb_in <- target_area.nb_in  + 1;
			
		}  else {
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			nb_people_supp <- nb_people_supp + 1;
			//write name + " choose_target_node null ->target_area" + target_area + " ,target_sub_area: " + target_sub_area;
			do die; 
		}
		
		if(location distance_to target_node.location < 5){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			is_arrived <- true;
			if (target_area != nil) {
				target_area.nb_out <- target_area.nb_out  + 1;
			}
			nb_people_arrived <- nb_people_arrived + 1;
			//write name + " is arrived";
			do die;
		}
		
	}
	
	action remove_nodes {
		target_node.pb_end <- target_node.pb_end + 1;
		
		current_node.pb_start <- current_node.pb_start + 1;
		pbs << line([current_node.location, target_node.location]);
		
		if (OD_area(target_area) != nil and length(OD_area(target_area).nodes) > 1 ) {
			remove target_node from: OD_area(target_area).nodes;	
		} 
		if (OD_area(source_area) != nil and length(OD_area(source_area).nodes) > 1 ) {
			remove current_node from: OD_area(source_area).nodes;
			
		} 
		
	}
	
	//bool virgin <- true;
	action driving {
		if (distance_to_goal = 0 and real_speed = 0) {
			proba_respect_priorities <- proba_respect_priorities - 0.1;
		} else {
			proba_respect_priorities <- 1.0;
		}
		//float t <- machine_time;
		do drive;
		//t5_2 <- t5_2 + machine_time - t;
		//t <- machine_time;
		if ((location distance_to target_node.location) < 5.0){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			is_arrived <- true;
			if (target_area != nil) {
				target_area.nb_out <- target_area.nb_out  + 1;
			}
			nb_people_arrived <- nb_people_arrived + 1;
			//write name + " is arrived 2";
			do die;
		} 
		//t5_1 <- t5_1 + machine_time - t;
	} 
	
	
	
	list<node_> nodes_for_path (node_ source, node_ target, file ssp){
		list<node_> nodes <- [];
		int id <- source.id;
		int target_id <- target.id;
		int cpt <- 0;
		loop while: id != target_id {
			nodes << node_(vertices[id]);
			id <- int(ssp[target_id, id]);
			cpt <- cpt +1;
			if (id = -1 or cpt > 50000) {
				return list<node_>([]);
			}
		}
		nodes<<target;
		return nodes;
	}
	
	action choose_a_path { 
		graph road_network <- strategy="speed lane" ? road_network_speed_lane : (strategy="speed" ? road_network_speed : (strategy="distance" ? road_network_distance : road_network_traffic_light));
		map<road,float> map_weights <- map<road, float>(strategy="speed lane" ? general_speed_map_speed_lane : (strategy="speed" ? general_speed_map_speed : (strategy="distance" ? general_speed_map_distance : general_speed_map_traffic_light)));
		if (recompute_path) {
			do recomputing_path(map_weights);
			recompute_path <- false;
		} else {
			//float tt <- machine_time;
		
		 	file ssp_file <- strategy="speed lane" ? file_ssp_speed_lane : (strategy="speed" ? file_ssp_speed : (strategy="distance" ? file_ssp_distance : file_ssp_traffic_light));
			list<node_> nodes <- nodes_for_path(current_node,target_node,ssp_file);
			if (length(nodes) > 1) {current_path <- path_from_nodes(graph: road_network, nodes: nodes);}
			//t_4_2 <- t_4_2 + machine_time - tt;	
			//current_path <- compute_path(graph: road_network, target: target_node);
			if (current_path = nil) {
				road_network_custom <- road_network_custom  with_weights map_weights;
				current_path <- compute_path(graph: road_network_custom, source: current_node, target: target_node);
				
				if (current_path = nil) {
					if (current_road != nil) { 
						ask road(current_road) {
							do unregister(myself);
						}
					}
					do remove_nodes;
					//write name + " pas de plus ccc : current_node: " + current_node + " target_node: " + target_node ;
					nb_people_supp <- nb_people_supp + 1;
					do die;
				}
			} else {
			//	nb_people_calcul_ppc <- nb_people_calcul_ppc + 1;
				if flip(proba_avoid_traffic_jam) and not empty(roads_traffic_jam) and length(current_node.roads_out) > 1 and ((current_node.roads_out count ((road(each).embout_route = nil) or not road(each).embout_route.real) ) > 0){
					//tt <- machine_time;
					bool tj <- false;
					//write string(cycle) + " : " + name + " roads_traffic_jam: " + roads_traffic_jam + " current_path.edges: " + current_path.edges;
					loop rd over: current_path.edges {
						if (road(rd).embout_route != nil and road(rd).embout_route.real and rd in roads_traffic_jam) {
							tj <- true;
							break;	
						}
					}
					//t_4_3 <- t_4_3 + machine_time - tt;	
					//tt <- machine_time;
					if (tj) {
						do recomputing_path(map_weights);
					}
					//t_4_4 <- t_4_4 + machine_time - tt;	
				}
			}
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
		//float t <- machine_time;
		current_path <- compute_path(graph: road_network_custom, target: target_node, source: current_node);
		//t_4_1 <- t_4_1 + machine_time - t;
		nb_recomputes <- nb_recomputes + 1;
		loop rd over: roads_traffic_jam{
			map_weights[rd] <- rds[rd];
		}		
	}
	
	action recomputing_path (map<road,float> map_weights) {
		if (mode_avoid) {
			cpt_avoid <- cpt_avoid + 1;
			if (cpt_avoid > nb_avoid_max) {
				mode_avoid <- false;
			}
		}
		if (not mode_avoid and flip(proba_know_map)) {
			do compute_shortest_path(map_weights);
		}
		else {
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
	}
	aspect car3D {
		if (not is_arrived and current_road != nil) {
			point loc <- calcul_loc();
			draw box(vehicle_length, 1,1) at: loc rotate:  heading color: color;
			draw triangle(0.5) depth: 1.5 at: loc rotate:  heading + 90 color: color;	
		}
	} 
	
	aspect base { 
		if (not is_arrived) {	
			draw triangle(8) color: color_behavior rotate:heading + 90;	
		}
		
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
	//	float t <- machine_time;
		if (proba_avoid_traffic_jam > 0 and flip(proba_avoid_traffic_jam)) {
			current_node <- node_(new_road.source_node);
			roads_traffic_jam <- remove_duplicates(roads_traffic_jam + (current_node.neighbours_tj));// where (each.embout_route != nil and each.embout_route.real));// and not (each in roads_traffic_jam)));
			if (not empty(roads_traffic_jam) and  length(current_node.roads_out) > 1 and ((current_node.roads_out count ((road(each).embout_route = nil) or not road(each).embout_route.real) ) > 0)) {
				remaining_time <- test_traffic_jam(new_road, remaining_time);
				nb_possible_recomputes <- nb_possible_recomputes + 1;
				
			
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
		
		//t5_1 <- t5_1 + machine_time - t;
		
		
	//	
		return remaining_time;
	}
	
	point calcul_loc {
		float val <- (road(current_road).lanes - current_lane) + 0.5;
		val <- on_linked_road ? val * - 1 : val;
		if (val = 0) {
			return location; 
		} else {
			return (location + {cos(heading + 90) * val, sin(heading + 90) * val});
		} 
	}
	
	
} 

experiment traffic_simulation type: gui {
	output {
		monitor "nb people" value: length(people);
		monitor "nb traffic jams" value: embouteillage count each.real;
		monitor "nb_possible_recomputes" value: nb_possible_recomputes;
		monitor "nb_recomputes" value: nb_recomputes;
		monitor "nb_people_supp" value: nb_people_supp;
		display carte_embouteillage type: opengl {
			species road aspect: base_gray refresh: false;
			species embouteillage aspect: base ;
		}
		
		/*display carte_pb type: opengl {
			species OD_area aspect: base  refresh: false;
			species road aspect: base_gray refresh: false;
			graphics "lines" {
				loop pb over: pbs {
					draw pb end_arrow: 5 color: #magenta;
				}	
			}
			species node_ aspect: base ;
			
		}*/
		
		display chart_nbpeople refresh: every(1#mn){
			chart "nb people evolution" type: series {
				data "nb people not arrived" value: length(people) color: rgb("blue") marker: false;
				data "nb people arrived" value: nb_people_arrived color: rgb("green") marker: false;
				data "nb_people_supp" value: nb_people_supp color: rgb("gray") marker: false;
			}
		}
		display city_display2D type: opengl{
		//	species OD_area aspect: base  refresh: false;
			species road aspect: carto refresh: false;
			species node_ aspect: base;
			species boucle aspect: base refresh: false;
			species people aspect: base; 
		}
		
		/*display chart_people_arrived refresh: every(1#mn){
			chart "mean time to arrive at destination" type: series size:{1.0,0.5} position: {0.0,0.0}{
				data "travel time" value: (empty(people where each.is_arrived) ? 0.0 : mean((people where each.is_arrived) collect each.time_to_arrive)) color: rgb("blue") marker: false;
			}
			chart "mean speed during the travel" type: series size:{1.0,0.5} position: {0.0,0.5}{
				data "travel speed" value: (empty(people where each.is_arrived) ? 0.0 : (3.6 * mean((people where each.is_arrived) collect (each.mean_real_speed / each.time_to_arrive * step)))) color: rgb("red") marker: false;
			}
		}*/
		
		/*display error_display refresh:every(6#mn) {
			chart "error comptage" type: series {
				data "global_error" value: global_error  color: #red marker: false;
				data "observed value" value: mean(boucle collect each.observed_value)  color: #blue marker: false;
				data "simulated value S1" value: mean(boucle collect each.simulated_value_S1)  color: #green marker: false;
				data "simulated value S2" value: mean(boucle collect each.simulated_value_S2)  color: #magenta marker: false;
				data "simulated somme S1 et S2" value: (mean(boucle collect each.simulated_value_S1)  + mean(boucle collect each.simulated_value_S2)  ) color: #yellow marker: false;
			}
		}
		
		display nb_in_area refresh: every(1#mn) {
			species OD_area aspect: base_in;
		}
		display nb_out_area refresh: every(1#mn) {
			species OD_area aspect: base_out;
		}*/
		
	}
	
}

// This experiment explores two parameters with an exhaustive strategy,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment 'Exhaustive exploration' type: batch repeat: 1 keep_seed: true until: (time = max([time_end_final,time_end])){
	parameter 'proba know map' var: proba_know_map among: [  0.0, 0.25, 0.5, 1.0  ];
	parameter 'proportion_speed_lane:' var: proportion_speed_lane among: [ 1.0 ];
	parameter "proba_avoid_traffic_jam_global:" var: proba_avoid_traffic_jam_global among: [0,0.25,0.5,1.0];
	parameter "batch_mode" var: batch_mode among: [true];
	method exhaustive ;
}