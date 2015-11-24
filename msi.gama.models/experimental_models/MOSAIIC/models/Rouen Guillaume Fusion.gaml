/**
 *  MOSAIIC Model - 16 octobre 2015
 *  Author: patricktaillandier
 *  Description: fusion avec la version de Guillaume
 */
 
model RoadTrafficComplex

global {   
	bool small <- true parameter: true;
	file shape_file_roads  <- small ? file("../includes/roads_7200_with_pm_small_small.shp") : file("../includes/roads_finalL93_delete_mathilde_2.shp") ;
	file shape_file_nodes  <-  small ?file("../includes/nodes_7200_with_pm_small_small_Nettoye.shp") : file("../includes/node_finalL93_delete_mathilde_2.shp");
	file shape_urgence  <-  small ?file("../includes/Evacuation_L93_small.shp") : file ("../includes/Evacuation_L93_big.shp") ;
	file shape_fuite <- file("../includes/Fuite_L93.shp");
	
	geometry shape <- envelope(shape_file_roads);
	graph road_network_speed;  
	graph road_network_custom;  
	
	map<road,float> general_speed_map_speed;
	
	float proportion_speed_lane <- 1.0;
	float proportion_speed <- 0.25;
	float proportion_distance <- 0.25;
	
	float min_embouteillage_people_creation <- 30.0;
	float min_embouteillage_people_destruction <- 20.0;
	float speed_coeff_traffic_jam <- 3.0;
	float time_to_consider_traffic_jam <- 2#mn;
	float distance_see_traffic_jams <- 500.0; // changé, initialement 500m. 
	
	float accepted_evacuation_distance <- 10.0; //distance d'un point d'évacuation à partir de laquelle on est "safe" -> l'agent est "tué"
	
	
	int time_accident <- 1;
	float prop_agent_evacuation <- 1.0;
	
	int nb_agents_in_traffic_jam update:people count (each.in_traffic_jam);
	int nb_driving_agent update:people count (each.real_speed > 1#km /#h);
	int nb_agent_speed_30 update:people count (each.real_speed < 30#km /#h); 
	int nb_agent_speed_zero update:people count (each.real_speed < 1#km /#h);
	int nb_traffic_signals_green update:node_ count (each.is_green);
	int nb_agent_update  update:length(people);
	
	float traffic_jam_length <- 0.0 update: sum(embouteillage collect (each.shape.perimeter));
	
	int traffic_jam_nb_roads <- 0 update: length(embouteillage);
	
	float mean_real_speed <- 0.0 update: mean((people) collect (each.real_speed)) #h/#km;
	
	list<node_> traffic_signals;
	list<node_> connected_nodes;
	list<road> real_roads;
	
	float min_length <- 0.0;
	
 	list<node_> vertices;
	
	float proba_avoid_traffic_jam_global <- 0.8;
	float proba_know_map <- 0.5;
	int nb_avoid_max <- 10;
	
	file file_ssp_speed;
	
	int nb_path_recompute;
	
	list<people> people_moving ;
	
	float coeff_nb_people <- 1.0;
	
	float max_priority;
	
	
	//****** UTILISER POUR L'OPTIMISATION DU MODELE *****
	float t1;
	float t2;
	float t3;
	float t4;
	float t5;
	float t6;
	float t7;
	
	//****************************************
		
	init {  
		create evacuation_urgence from: shape_urgence;
		create node_ from: shape_file_nodes with:[is_traffic_signal::(string(read("type")) = "traffic_signals"), is_crossing :: (string(read("crossing")) = "traffic_signals")];
		loop pt over: remove_duplicates(node_ collect (each.location)) {
			list<node_> nds <- node_ overlapping pt;
			nds >> one_of(nds);
			ask nds {
				do die;
			}
		}
		ask evacuation_urgence {
			noeud_evacuation <- node_ closest_to self;
		}


		create road from: shape_file_roads with:[nb_agents::int(read("NB_CARS")), name::string(read("name")),highway::string(get("highway")),junction::string(read("junction")),lanes::int(read("lanes")), maxspeed::float(read("maxspeed")) #h/#km, oneway::string(read("oneway")), lanes_forward ::int (get( "lanesforwa")), lanes_backward :: int (get("lanesbackw"))] {
			if maxspeed <= 0 {maxspeed <- 50 #km/#h;}
			if lanes <= 0 {lanes <- 1;}
			capacite_max <- 1+ int(lanes * shape.perimeter/(5.0));
			min_traffic_jam_people_destroy <- int(min([capacite_max, min_embouteillage_people_destruction]));
			min_traffic_jam_people_creation <- int( min([capacite_max, min_embouteillage_people_creation]));
			geom_display <- (shape + (2.5 * lanes));
			max_embouteillage_vitesse <- maxspeed / speed_coeff_traffic_jam;
		}	
		
		real_roads <- road where (each.shape.perimeter > min_length);
		general_speed_map_speed <- road as_map (each::(each.shape.perimeter / each.maxspeed)); 
		
		
		road_network_speed <-  (as_driving_graph(road, node_))  with_weights general_speed_map_speed;
		road_network_custom <- (as_driving_graph(road, node_)) use_cache false;
		ask road {
			neighbours <- node_ at_distance distance_see_traffic_jams;
		}
		vertices <- list<node_>(road_network_speed.vertices);
		loop i from: 0 to: length(vertices) - 1 {
			vertices[i].id <- i; 
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
		do fill_matrix;
		ask road {
			int nb <- int(0.5 + nb_agents/coeff_nb_people);
			if (nb > 0) {
				ask world{do create_people_road(myself,nb);} 
			}
			
		}  
		nb_agent_update <- length(people);  
		do compute_road_priority;
	}
	
	reflex scnerio_evac when: cycle = time_accident  {
		ask (prop_agent_evacuation * length(people)) among people {
			target_node <- nil;
			color_behavior <- #red;
			targets <- [];
			current_path <- nil;
			size <- 10;
		}
	}
	
	
	action fill_matrix {
		file_ssp_speed <- csv_file("shortest_paths_speed_2.csv",";");
	}
	
	action create_people_road(road a_road, int nb) {
		list<point> pts <- points_on(a_road,a_road.shape.perimeter/(nb/a_road.lanes));
		loop pt over: pts {
			loop i from: 0 to: a_road.lanes -1 {
				create people  { 
					speed <- 50 #km /#h ;
					real_speed <- 50 #km /#h ;
					vehicle_length <- 3.0 #m;
					right_side_driving <- true;
					proba_lane_change_up <-1.0;// 0.5 + (rnd(500) / 500);
					proba_lane_change_down <- 1.0;//0.7+ (rnd(300) / 500);
					location <- pt;
					current_lane <- i;
					init_rd <- a_road;
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
	
	action compute_road_priority2 {
		map<list<node_>,float> nodes2dist <- [];
		ask road {
			priority <- 999999999.9;
			loop e over: evacuation_urgence {
				if (e.noeud_evacuation = target_node) {
					priority <- 0.0;
					break;
				} 
				list nodes <- world.nodes_for_path(node_(target_node),e.noeud_evacuation, file_ssp_speed);
				float dist <- 0.0;
				if length(nodes) > 1 {
					loop i from: 0 to: length(nodes) - 2 {
						node_ n1 <- node_(nodes[0]);
						node_ n2 <- node_(nodes[1]);
						if [n1,n2] in nodes2dist.keys {
							dist <- dist + nodes2dist[[n1,n2]];
						} else {
							float pp <- (road(road_network_speed edge_between (n1::n2))).shape.perimeter;
							nodes2dist[[n1,n2]] <- pp;
							dist <- dist + pp;
						}
					}
					priority <- min([priority, dist]);
				}
			}
		}
	}
	
	action compute_road_priority {
		map<list<node_>,float> nodes2dist <- [];
		ask road {
			priority <- 999999999.9;
			loop e over: evacuation_urgence {
				if (e.noeud_evacuation = target_node) {
					priority <- 0.0;
					break;
				} 
				path p <-  path_between(road_network_speed, target_node, e.noeud_evacuation);
				if (p != nil and p.shape != nil) {
					priority <- min([priority, p.shape.perimeter]);
				}
			}
		}
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
	
	
	reflex general_dynamic {
		float t <- machine_time;
		ask traffic_signals {
			do dynamic_node;
		}
		t1 <- t1 + machine_time - t;
		t <- machine_time;
		if (every(5)) {
			ask real_roads {
				do dynamic_road;
			}	
		}
		t2 <- t2 + machine_time - t;
		t <- machine_time;
		
		ask people where (each.target_node = nil ){
			do choose_target_node;
		}
		t3 <- t3 + machine_time - t;
		t <- machine_time;
		
		ask people where ((each.current_path = nil or each.recompute_path or each.final_target = nil)and each.target_node != nil) {
			 do choose_a_path; 
		}
		t4 <- t4 + machine_time - t;
		t <- machine_time;
		
		people_moving <- (people where (each.current_path != nil and each.final_target != nil ));
		
		ask people_moving {
			val_order <- (road(current_road).priority * 1000000 - 10000 * segment_index_on_road + distance_to_goal);
		}
		people_moving <- people_moving sort_by each.val_order;
		t5 <- t5 + machine_time - t;
		t <- machine_time;
		int cpt <- 0;
		ask people_moving{
			order <- cpt;
			do driving;
			cpt <- cpt + 1;
		}
		
		t6 <- t6 + machine_time - t;
		t <- machine_time;
		ask people where (each.target_node != nil and (each.location distance_to each.target_node.location < 10)){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			do die;
		}
		t7 <- t7 + machine_time - t;
	}
	
	//****** UTILISER POUR L'OPTIMISATION DU MODELE *****
	reflex info when: every(60)  { 
		write "\n ******** " + cycle + "********";
		write "temps node : " + t1/1000;
		write "temps routes : " + t2/1000;
		write "temps choose target : " + t3/1000;
		write "temps choose path : " + t4/1000;
		write "temps tri : " + t5/1000;
		write "temps driving : " + t6/1000;
		write "temps arriver objectif : " + t7/1000;
	}
	
	//****************************************
		
	
	reflex end when: length(people) = 0  { 
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
	action dynamic_node {
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
	float priority;
	
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
	
	
	aspect carto {
		if highway = "trunk" or highway="trunk_link" or highway = "motorway" or highway = "motorway_link" {draw (shape + 3) color: #red;}
		else if highway = "primary" or highway="primary_link"{draw (shape + 2) color: #orange;}
		else if highway = "secondary" or highway="secondary_link" {draw (shape + 1) color: #yellow;}
		else {draw shape color: #black end_arrow: 5; }
	}
	aspect pp {
		draw shape color: #black end_arrow: 5; 
		draw (""+int(self)+" -> "+length(all_agents)) color: #black size: 10; 
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
	
	
}

species embouteillage {
	list<people> personnes_bloquees;
	road route_concernee;
	float counter <- 0.0 update: counter + step;
	bool real_before <- false;
	bool real <- false ; 
	
	reflex maj_neighbours{
		if (cycle < 10) {counter <- 5#mn;}
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
		draw shape + 5.0 color: real ? #red : #green;
	}
	
}

species evacuation_urgence {
	node_ noeud_evacuation ;
	aspect default {
		draw circle (10) color: #orange;
	}
}

species point_fuite {
	aspect default {
		draw circle (10) color: #pink;
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
	
	float val_order;
	
	int order;
	
	
	action choose_target_node  {
		if cycle >= time_accident {
			target_node <- (evacuation_urgence with_min_of (each distance_to self)).noeud_evacuation;
		}
		else {
			target_node <- one_of(connected_nodes);
		}
		if(location distance_to target_node.location < 10){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			do die;
		}	
	}
	
	
	
	action choose_a_path  { 
		current_node <- nil;
		if (init_rd != nil) {
			if (cycle > 2 or (init_rd distance_to self > 0.5) or (init_rd.target_node distance_to self < 0.5) ) {
				if ((init_rd.target_node distance_to self < 0.5) )
				{
					current_node <- node_(init_rd.target_node);
				}
				init_rd <- nil;
				
			} else {
				current_node <- node_(init_rd.target_node); 
			}
		}
		if (current_node = nil) {
			if (current_road != nil) {
				current_node <-node_([road(current_road).source_node, road(current_road).target_node] with_min_of (each distance_to self));
			} else {
				current_node <- (node_ at_distance 50) closest_to self;
			}
			
		}
		if (recompute_path) {
			do recomputing_path(general_speed_map_speed);
			recompute_path <- false;
		} else {
			list<node_> nodes <- world.nodes_for_path(current_node,target_node,file_ssp_speed);
			if (init_rd != nil) {
				add node_(init_rd.source_node) to: nodes at: 0;
			}
			if (length(nodes) > 1) {current_path <- path_from_nodes(graph: road_network_speed, nodes: nodes);}
			 
			if (current_path = nil) {
				if (init_rd != nil) {
					current_path <- compute_path(graph: road_network_speed, target: target_node, on_road: init_rd);
				} else {
					current_path <- compute_path(graph: road_network_speed, target: target_node);
				}
				nb_path_recompute <- nb_path_recompute + 1;	
			}
			if (current_path = nil) {
				if (current_road != nil) { 
					ask road(current_road) {
						do unregister(myself);
					}
				}
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
	
	action driving {
		if (distance_to_goal = 0 and real_speed = 0) {
			proba_respect_priorities <- proba_respect_priorities - 0.1;
		} else {
			proba_respect_priorities <- 1.0;
		}
		do drive;
		if ((location distance_to target_node.location) < 10.0){
			if (current_road != nil) {
				ask road(current_road) {
					do unregister(myself);
				}
			}
			do die;
		} 
	} 
	
	
	action compute_shortest_path (map<road,float> map_weights){
		map<road,float> rds <- [];
		loop rd over: roads_traffic_jam{
			float val <- map_weights[rd];
			rds[rd] <- val ;
			map_weights[rd] <-val * 10000;
		}
		road_network_custom <- road_network_custom  with_weights map_weights;
		current_path <- compute_path(graph: road_network_custom, target: target_node, source: current_node);
		nb_path_recompute <- nb_path_recompute + 1;
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
		draw triangle(3) color: color_behavior rotate:heading + 90;	
	} 
	
	aspect rang { 
		float val <- order * 3 /length(people) * 255;
		
		draw triangle(10) color: rgb(val, val, val) rotate:heading + 90;	
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

experiment sc_exceptionnel_optimized type: gui {
	parameter coeff_nb_people  var: coeff_nb_people among:[1.0] <- 1.0;
	output {
		monitor "nb people" value: length(people);
		monitor "nb path computation: " value: nb_path_recompute;
		
		display Graphiques refresh: every(10){
			chart "mean speed of people" type: series size:{0.5,0.5} position:{0.0,0.5} {
				data "mean speed of people" value: mean_real_speed color: #blue ;
			}
			
			chart "rapports" type: series size:{0.5,0.5} position:{0.5,3.5}{
				data "nb agents en mvt // nb agents" value: nb_driving_agent * 100 / (nb_agent_update) style: line color: #gray ;
				data "feux verts" value: (nb_traffic_signals_green * 100 / length(traffic_signals)) style: line color: #green ;
				data "routes embouteillees" value: (traffic_jam_nb_roads * 100 / length(road)) style: line color: #red ;
			}
			
			chart "infos agents" type: series size:{0.5,0.5} position:{0.0,3.5}{
				data "nb  agents" value: length(people) style: line color: #black ; //nb_agent_update
				data "nb driving agent" value: nb_driving_agent style: line color: #red ;
				data "nb agents in t-jam" value: nb_agents_in_traffic_jam style: line color: #orange ;
				data "nb agent speed < 30 km/h" value: nb_agent_speed_30 color: #gray;
				data "nb agent speed == 0 km/h" value: nb_agent_speed_zero color: #purple;
			}
			chart "traffic jam length" type: series size:{0.5,0.5} position:{0.5,0.5}{
				data "traffic jam meters (cummulative)" value: traffic_jam_length color: #black;
			}
		}
	}
	
}

experiment traffic_simulation_sc_exceptionnel type: gui {
	output {
		monitor "nb people" value: length(people);
		monitor "nb path computation: " value: nb_path_recompute;
		display carte_embouteillage{
			species road aspect: carto refresh: false;
			species embouteillage aspect: base_width ;
		}
		
		display city_display2D type: opengl refresh: every(5){
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
			
			chart "infos agents" type: series size:{0.5,0.5} position:{0.0,3.5}{
				data "nb  agents" value: length(people) style: line color: #black ; //nb_agent_update
				data "nb driving agent" value: nb_driving_agent style: line color: #red ;
				data "nb agents in t-jam" value: nb_agents_in_traffic_jam style: line color: #orange ;
				data "nb agent speed < 30 km/h" value: nb_agent_speed_30 color: #gray;
				data "nb agent speed == 0 km/h" value: nb_agent_speed_zero color: #purple;
			}
			chart "traffic jam length" type: series size:{0.5,0.5} position:{0.5,0.5}{
				data "traffic jam meters (cummulative)" value: traffic_jam_length color: #black;
			}
		}
	}
	
}
 