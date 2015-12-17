/**
 *  CatastropheRouenBDI
 *  Author: mathieu
 *  Description: Model of the traffic in Rouen with an industrial accident and two shelters. The model is simple but it use the BDI architecture.

 */

model CatastropheRouenBDI

global schedules: [world] + traffic_signals + real_roads + (people sort_by (- 1000000 * each.segment_index_on_road + each.distance_to_goal)){   
	bool small <- true parameter: true;
	file shape_file_roads  <- small ? file("../includes/roads_finalL93_small.shp") : file("../includes/roads_finalL93_delete_mathilde_2.shp") ;
	file shape_file_nodes  <-  small ?file("../includes/node_finalL93_small.shp") : file("../includes/node_finalL93_delete_mathilde_2.shp");
	
	geometry shape <- envelope(shape_file_roads);
	graph road_network_speed;  
	graph road_network_custom;  
	int nb_people <- small ? 500: 2000;
	
	map<road,float> general_speed_map_speed;
	
	float proportion_speed_lane <-1.0;
	float proportion_speed <- 0.25;
	float proportion_distance <-0.25;
	
	float min_embouteillage_people_creation <- 30.0;
	float min_embouteillage_people_destruction <- 30.0;
	float speed_coeff_traffic_jam <- 5.0;
	float time_to_consider_traffic_jam <- 3#mn;
	float distance_see_traffic_jams <- 500.0;
	
	float time_end_final <- 3 #h;
	
	list<node_> traffic_signals;
	list<node_> connected_nodes;
	list<road> real_roads;
	
	float min_length <- 0.0;
	
 	list<node_> vertices;
	
	float proba_avoid_traffic_jam_global <- 1.0;
	float proba_know_map <- 0.0;
	int nb_avoid_max <- 10;
	
	int nbRefuge <- 0;
	int nbRefuge2 <- 0;
	int cata <- 0;
	
		
	reflex info{
		write "----------";
		write "nbRefuge : "+ nbRefuge;
		write "nbRefuge2 : "+ nbRefuge2;
		write cata;
	}
	
	reflex stop{
		if length(people)=0{
			do halt;
		}
	}
		
	init {
		create catastrophe number:1;  
		create refuge number:1;
		create refuge2 number:1;
		create node_ from: shape_file_nodes with:[is_traffic_signal::(string(read("type")) = "traffic_signals"), is_crossing :: (string(read("crossing")) = "traffic_signals")];
		create road from: shape_file_roads with:[name::string(read("name")),highway::string(get("highway")),junction::string(read("junction")),lanes::int(read("lanes")), maxspeed::float(read("maxspeed")) #km/#h, oneway::string(read("oneway")), lanes_forward ::int (get( "lanesforwa")), lanes_backward :: int (get("lanesbackw"))] {
			capacite_max <- 1+ int(lanes * shape.perimeter/(4.0));
			min_traffic_jam_people_destroy <- int(min([capacite_max, min_embouteillage_people_destruction]));
			min_traffic_jam_people_creation <- int( min([capacite_max, min_embouteillage_people_creation]));
			if lanes = 0 {lanes <- 1;}
			
			geom_display <- (shape + (2.5 * lanes));
			max_embouteillage_vitesse <- maxspeed / speed_coeff_traffic_jam;
			do manage_oneway_attribute;
		}	
		ask road {neighbours <- node_ at_distance distance_see_traffic_jams;}
		real_roads <- road where (each.shape.perimeter > min_length);
		general_speed_map_speed <- road as_map (each::(each.shape.perimeter / each.maxspeed)); 
		
		
		road_network_speed <-  (as_driving_graph(road, node_))  with_weights general_speed_map_speed;
		road_network_custom <- (as_driving_graph(road, node_)) use_cache false;
		
	
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
		loop times: nb_people{do create_people;}
	}
	
	
	action create_people {
		create people  { 
			speed <- 130 #km /#h ;
			real_speed <- 130 #km /#h ;
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
			
			do add_desire(bouger);
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
	
	reflex end when: time = time_end_final{
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

	
species people skills: [advanced_driving] control: simple_bdi { 
	rgb color <-rnd_color(255) ;
	rgb color_behavior <-#yellow;
	bool in_traffic_jam <- false;
	node_ target_node;
	list<road> roads_traffic_jam;
	
	bool is_stopped <- false;
	bool recompute_path <- false;
	bool to_delete <- false;
	float proba_avoid_traffic_jam;
	node_ current_node;
	bool mode_avoid <- false;
	int cpt_avoid <- 0;
	
	predicate bouger <- new_predicate("bouger");
	bool probabilistic_choice <- true;
	
	refuge monRefuge <- first(refuge);
	refuge2 monRefuge2 <- first(refuge2);
	
	int refugeChoisi <-0;
	bool catast <- false;
	predicate diying <- new_predicate("diying");
	
	reflex choose_target_node when:target_node = nil {
		target_node <- one_of(connected_nodes);
	}
	
	reflex driving when: current_path != nil and final_target != nil {
		if (distance_to_goal = 0 and real_speed = 0) {
			proba_respect_priorities <- proba_respect_priorities - 0.1;
		} else {
			proba_respect_priorities <- 1.0;
		}
		do drive;
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
	
//	reflex infoCata{
//		if(has_belief("location_catastrophe")){
//			catast <- true;
//			ask world{
//				cata<-cata+1;
//			}
//		}
//	}
	
	perceive target:catastrophe in: 100{
		focus var: location /*agent: myself*/;
		ask myself{
			do clear_intentions();
			do clear_desires();
		}
		if(!myself.catast){
			myself.catast <- true;
			myself.color_behavior <- #red;
			myself.current_path <-nil;
			ask world{
					cata<-cata+1;
			}
		}
	}
	
	perceive target:refuge in : 50{
		ask myself{
			if((has_desire(new_predicate("shelter"))) and (refugeChoisi=1)){
				do add_belief(new_predicate("shelter"));
				ask world{
					nbRefuge <- nbRefuge+1;
				}
				ask road(current_road) {
					do unregister(myself);
				}
			}
		}
	}
	
	perceive target:refuge2 in : 50{
		ask myself{
			if((has_desire(new_predicate("shelter"))) and (refugeChoisi=2)){
				do add_belief(new_predicate("shelter"));
				ask world{
					nbRefuge2 <- nbRefuge2+1;
				}
				ask road(current_road) {
					do unregister(myself);
				}
			}
		}
	}
	
	perceive target:people in : 20{
		predicate test <- get_belief_with_name("location_catastrophe");
		if((!myself.has_belief(test)) and has_belief(test)){
			ask myself{
				do add_belief(test);
				do clear_intentions();
				do clear_desires();
			}
			if(!myself.catast){
				myself.catast <- true;
				myself.color_behavior <- #red;
				myself.current_path <-nil;
				ask world{
						cata<-cata+1;
				}
			}
		}
	}
	
	rule belief: new_predicate("location_catastrophe") new_desire: new_predicate("shelter") when: !has_belief(new_predicate("shelter"));
	rule belief: new_predicate("shelter") new_desire: diying;
	
	plan bouge when: (current_path = nil or recompute_path or final_target = nil)and target_node != nil and !catast intention: bouger 
		finished_when: (current_path != nil) or (has_belief(new_predicate("location_catastrophe"))){
		do chose_path;
	}
	
	plan evitement when: (current_path = nil or recompute_path or final_target = nil)and target_node != nil intention: new_predicate("shelter") finished_when: false
		priority : monRefuge distance_to point(get_belief_with_name("location_catastrophe").values["location_value"]){
		target_node <- monRefuge.noeudRelie;
		refugeChoisi <- 1;
		do chose_path;
	}
	
	plan evitement2 when: (current_path = nil or recompute_path or final_target = nil)and target_node != nil intention: new_predicate("shelter") finished_when: false
		priority : monRefuge2 distance_to point(get_belief_with_name("location_catastrophe").values["location_value"]){
		target_node <- monRefuge2.noeudRelie;
		refugeChoisi <- 2;
		do chose_path;
	}
	
	plan toDie intention:diying{
		do die;
	}
	
	action chose_path{
		if (recompute_path) {
			do recomputing_path(general_speed_map_speed);
			recompute_path <- false;
		} else {
			current_path <- compute_path(graph: road_network_speed, target: target_node);
			if (current_path = nil) {
				if (current_road != nil) { 
					ask road(current_road) {
						do unregister(myself);
					}
				}
				ask world {do create_people;}
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
	
	aspect base { 
		draw triangle(8) color: color_behavior rotate:heading + 90;	
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
		if (proba_avoid_traffic_jam > 0 and flip(proba_avoid_traffic_jam)) {
			current_node <- node_(new_road.source_node);
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

species catastrophe{
	init{
		location <- one_of(node_).location;
	}
	
	aspect base{
		draw circle(100) color:#blue;
	}
}

species refuge{
	node_ noeudRelie;
	
	init{
		location <- one_of(node_).location;
		noeudRelie <- first(node_ where(each.location=location));  
	}
	
	aspect base{
		draw circle(50) color:#green;
	}
}

species refuge2{
	node_ noeudRelie;
	
	init{
		location <- one_of(node_).location;
		noeudRelie <- first(node_ where(each.location=location));  
	}
	
	aspect base{
		draw circle(50) color:#yellow;
	}
}

experiment traffic_simulation type: gui {
	output {
		display carte_embouteillage {
			species road aspect: base_gray refresh: false;
			species embouteillage aspect: base_width ;
			species catastrophe aspect: base;
			species refuge aspect: base;
			species refuge2 aspect: base;
		}
		
		display city_display2D {
			species road aspect: carto refresh: false;
			species node_ aspect: base;
			species people aspect: base; 
			species catastrophe aspect: base;
			species refuge aspect: base;
			species refuge2 aspect: base;
		}
	}
}
