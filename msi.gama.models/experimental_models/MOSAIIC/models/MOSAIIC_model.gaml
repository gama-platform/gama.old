/**
 *  simpleOSMLoading
 *  Author: patricktaillandier
 *  Description: 
 */

model simpleOSMLoading
 
global skills: [osm]{
	
	file osmfile <- file("../includes/rouen.osm");
	geometry shape <- envelope(osmfile);
	map<string, rgb> roads_colors <- ["primary"::rgb("red"), "secondary"::rgb("blue"), "tertiary"::rgb("green"), "motorway"::rgb("black"), "living_street"::rgb("yellow"),"residential"::rgb("yellow"), "unclassified"::rgb("yellow")];
	map<string, rgb> buildings_colors <- ["yes"::rgb("gray"), "church"::rgb("pink")];
	list<string> signals_str <- ["traffic_signals", "stop"];
	file stop_icon <- file("../includes/stop.png");
	file feu_rouge_icon <- file("../includes/traffic_light_red.png");
	file feu_vert_icon <- file("../includes/traffic_light_green.png");
	//map<string, file> signal_icons <- ["traffic_signals"::file("../includes/traffic_light.gif"), "stop"::file("../includes/stop.png")];
	
	graph road_network;
	list<road> carRoad <- [];
	map<road,float> speed_map;
	map<road,float> distance_map;
	
	list<people> people_moving <- [];
	float acceleration_max <- 5/3.6;
	
	init {
		list<agent> createdAgents <- load_osm(file:osmfile, road_species: road, building_species: building,signal_species:signal, node_species: node);
		ask (road) {
			if (highway in roads_colors.keys and (shape.points[0] != shape.points[length(shape.points) - 1])) {
				color <- roads_colors at highway;
				if (lanes = 0) {lanes <- 1;}
				if (maxspeed = 0.0) {maxspeed <- 30;}	
				shape_display <- shape + lanes;
				add self to: carRoad;
				agents_on <- [];
				loop times:lanes {
					add [] to: agents_on;
				}
			} else {
				do die;
			}
		}
		ask node {
			if (empty(road overlapping self)) {do die;}
		}
		ask signal {
			if (highway in signals_str) {
				est_feu <- highway = "traffic_signals" ;
				size <- est_feu ? 10.0 : 20.0;
				icon <- est_feu ? (est_vert ? feu_vert_icon :feu_rouge_icon) : stop_icon;
			} else {
				do die;
			}
		}
		speed_map <- carRoad as_map (each::(each.shape.perimeter * (3600.0 / (each.maxspeed * 1000.0))));
		distance_map <- carRoad as_map (each::(each.shape.perimeter));
		road_network <- directed(as_edge_graph(carRoad))  with_weights speed_map ;
		loop nd1 over: road_network.vertices {
			create node {
				shape <- nd1;
			}
		}
		ask building {
			if ((self.building in buildings_colors.keys) and ! bridge) {
				color <- buildings_colors at self.building;
				if (shop != nil) {color <-rgb("cyan");}	
				node_link <- (node as list) closest_to self;
			} else {
				do die;
			}
		} 
		create people number: 500 { 
			speed <- 1;
			int nb_arrets <- 2 + rnd(2);
			loop times: nb_arrets {
				building bd <- one_of(building);
				if (empty(listes_etapes) or bd != last(listes_etapes)) {
					add bd to: listes_etapes;
				}
			}
			location <- any_location_in (listes_etapes[0]); 
		}	
	}	
}


species road parent: osm_road {
	rgb color <-  rgb("black");
	geometry shape_display;
	bool prise <- false;
	
	/*reflex info when: not empty(agents_on[0]){
		string info_to <- "";
		loop ls over: agents_on[0] {
			info_to <- info_to + (people(ls)).name + " -> " + ((shape distance_to (people(ls))) with_precision 2)+ " ; ";
		}
		write info_to;
	}*/
	
	aspect base {
		draw shape_display color: color;
		/*if (prise) {
			draw name + " ok !" size: 10;
		}*/
	}
	aspect test {
		draw shape color: rgb(rnd(255),rnd(255),rnd(255));
	}
	aspect max_speed {
		draw shape_display color: maxspeed < 30 ? rgb("red") : ( maxspeed < 50 ? rgb("orange") : ( maxspeed < 70 ? rgb("blue"):rgb("green")) );
	}
} 
	
species node parent: osm_node {
	aspect base {
		draw square(5) color: rgb("yellow") ;
	}
} 
	
species building parent: osm_building {
	rgb color <-  rgb("gray");
	node node_link ;
		
	aspect base { 
		draw shape color: color;
	}
}  

species signal parent: osm_traffic_signal {
	float size ;
	bool est_feu ;
	bool est_vert <- flip(0.5);
	file icon;
				
	aspect base {
		draw icon size: size;
	}
} 
	
	
species people skills: [osm_driving]{ 
	rgb color <- rgb(rnd(255), rnd(255), rnd(255)) ;
	list<building> listes_etapes;
	point the_target <- nil ; 
	point the_final_target <- nil;
	path the_path <- nil;
	int index_path; 
	int etape_courante <- 0;
	int cpt_wait <- 1 + rnd(50) update: cpt_wait - 1;
	bool deplacement <- false;
	float vehicle_length <- 5.0;
	float security_distance <- 20.0;
	list<point> targets <- [];
	float vitesse_coeff <- 1.2 - (rnd(400) / 1000);
	
	action enregistrement_route (road route, path the_path, int lane) {
		current_road <- route;
		current_lane <- lane;
		//route.color <- rgb("magenta");
		//route.prise <- true;
		the_target <-targets[index_path]; 
		add self to: route.agents_on[current_lane];
		//write name + "enregistrement sur la route : " + route + " distance ? " + (location distance_to route);
		
	}
	reflex time_to_go when: not(deplacement) and cpt_wait <= 0 and etape_courante < (length(listes_etapes) - 1) {
		point pt_init <- copy(location);
		
		location <- (listes_etapes[etape_courante]).node_link.location;
		if (empty(people_moving at_distance (2 * security_distance + vehicle_length))) {
			the_final_target <- (listes_etapes[etape_courante + 1]).node_link.location;
			the_path <- road_network path_between (location::the_final_target);
			if (the_path != nil and not (empty(the_path.segments))) {
				targets <- [];
				loop edge over: the_path.edges {
					add last(agent(edge).shape.points) to: targets;
				}
				index_path <- 0;
				//write "targets : " + targets;
				remove last(targets) from: targets;
				add the_final_target to: targets;
				deplacement <- true;
				list<geometry> segments <- the_path.segments;
					
				road route <- road(the_path.edges[index_path]);//(listes_etapes[etape_courante]).road_link;
				add self to: people_moving;
				do enregistrement_route(route,the_path,0);
			} else {
				etape_courante <- etape_courante + 1;
				location <- pt_init;
			}
		} else {
			location <- pt_init;
		}
		  
	}	 

	reflex move when: deplacement {
		float tps_restant <- 1.0;
		loop while: (tps_restant > 0.0 and deplacement) {
			//write "cycle :" + cycle + "tps_restant : " + tps_restant;
			//write "location :" + location + " the_target : " + the_target + " current_road : " + current_road.shape.points;
			float tps_before_temp <- tps_restant;
			speed <- min([real_speed + acceleration_max, vitesse_coeff * (road(current_road).maxspeed/3.6)]);
			tps_restant <- (osm_follow (path: the_path, target: the_target,speed: speed,time: tps_restant, move_weights: distance_map)) with_precision 2; 
			deplacement <- (location != the_final_target) ;
			if (deplacement and (location = the_target or tps_restant = tps_before_temp) ) {
				road route <- road(the_path.edges[index_path + 1]); 
				int lane <- 0; 
				list<people> people_on_road <- route.agents_on[lane];
				if (location != the_target) or (empty(people_on_road at_distance (security_distance + 2 * vehicle_length))) {
					
					index_path <- index_path + 1;
					road last <- (road(current_road));
					//last.color <- rgb("black");
				 
					remove self from: last.agents_on[current_lane];
					//road route <- road(the_path.edges[index_path]);   
						
					//write "targets : " + length(targets) + " - " + targets ;
					//road route <- nil;
					//if (location = the_target) {	 
					//route <- road(the_path.edges[index_path]); 
					//} else {
					//	route <- road closest_to self;
					//}
						
					do enregistrement_route(route, the_path, lane); 	
				} else {tps_restant <- 0.0;}
			}
		} 
		if not deplacement { 
			etape_courante <- etape_courante + 1; 
			building bat <- listes_etapes[etape_courante];
			location <- any_location_in(bat);
			remove self from: road(current_road).agents_on[current_lane];
			cpt_wait <- 1 + rnd(50);
			remove self from: people_moving;
		}
	}
	aspect base {
		draw circle(10) color: color;
		//if (the_target != nil) {draw circle(10) at: the_target color: rgb("cyan");}
	} 
}
 
experiment main_experiment type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display map {
			species road aspect: base ;
			species building aspect: base ;
			species node aspect: base;
			species signal aspect: base z: 0.01; 
			species people aspect: base ;
		}
		
		//graphdisplay monNom2 graph: road_network lowquality:true;
		
		/*display map_max_speed {
			species road aspect: max_speed;
		}*/
	}
}
