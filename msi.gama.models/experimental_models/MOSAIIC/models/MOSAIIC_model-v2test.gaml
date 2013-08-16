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
	
	graph road_network;
	list<road> carRoad <- [];
	map<road,float> speed_map;
	map<road,float> distance_map;
	map<point,node> nodes; 
	
	list<people> people_moving <- [];
	float acceleration_max <- 5/3.6;
	
	init {
		list<agent> createdAgents <- load_osm(file:osmfile, road_species: road, building_species: building, node_species: node);
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
			else {
				put self in:nodes at: location;
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
		create people number: 1 { 
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
	aspect base {
		draw shape_display color: color;
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
		the_target <-targets[index_path]; 
		add self to: route.agents_on[current_lane];
	}
	
	int choix_voie (road la_route) {
		if (la_route.lanes = 1) {return 0;}
		else{
			int cv <- 0;
			int nb <- length(people);
			loop i from: 0 to: la_route.lanes - 1{
				int nb_l <- length(la_route.agents_on[i]);
				if (nb_l < nb) {
					nb <- nb_l;
					cv <- i;
				}
			}
			return cv;
		}	
	}
	
	float choix_vitesse (road la_route) {
		return min([real_speed + acceleration_max, vitesse_coeff * (road(la_route).maxspeed/3.6)]);
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
				remove last(targets) from: targets;
				add the_final_target to: targets;
				deplacement <- true;
				road route <- road(the_path.edges[index_path]);
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
	
	bool pret_engagement (road route, int lane) {
		list<people> people_on_road <- route.agents_on[lane];
		return (empty(people_on_road at_distance (security_distance + 2 * vehicle_length)));
	}
	
	bool engagement_prochaine_route(road route, int lane, bool vient_arriver) {
		bool engagement <- true;
		node noeud  <- nodes at location;
		if (noeud = nil) {
			engagement <- pret_engagement(route, lane);
		} else {
			
		}
		return engagement;
	} 

	reflex move when: deplacement {
		float tps_restant <- 1.0;
		loop while: (tps_restant > 0.0 and deplacement) {
			float tps_before_temp <- tps_restant;
			speed <- choix_vitesse(road(current_road));
			tps_restant <- (osm_follow (path: the_path, target: the_target,speed: speed,time: tps_restant, move_weights: distance_map)) with_precision 2; 
			deplacement <- (location != the_final_target) ;
			if (deplacement and (location = the_target or tps_restant = tps_before_temp) ) {
				road route <- road(the_path.edges[index_path + 1]); 
				int lane <- choix_voie(route); 
				list<people> people_on_road <- route.agents_on[lane];
				if (location != the_target) or (engagement_prochaine_route(route, lane,tps_restant  < 1.0)) {
					index_path <- index_path + 1;
					road last <- (road(current_road));
					remove self from: last.agents_on[current_lane];
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
	} 
}
 
experiment main_experiment type: gui {
	output {
		display map {
			species road aspect: base ;
			species building aspect: base ;
			species node aspect: base;
			species people aspect: base ;
		}
	}
}
