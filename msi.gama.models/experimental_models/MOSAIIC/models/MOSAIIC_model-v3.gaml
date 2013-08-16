/**
 *  simpleOSMLoading
 *  Author: patricktaillandier
 *  Description: 
 */

model simpleOSMLoading
 
global skills: [osm]{
	
	file osmfile <- file("../includes/rouen.osm");
	geometry shape <- envelope(osmfile);
	map<string, rgb> roads_colors <- ["primary"::rgb("pink"), "secondary"::rgb("blue"), "tertiary"::rgb("green"), "motorway"::rgb("black"), "living_street"::rgb("yellow"),"residential"::rgb("yellow"), "unclassified"::rgb("yellow")];
	map<string, rgb> buildings_colors <- ["yes"::rgb(200,200,200), "church"::rgb("pink")];
	file stop_icon <- file("../includes/stop.png");
	file feu_rouge_icon <- file("../includes/traffic_light_red.png");
	file feu_vert_icon <- file("../includes/traffic_light_green.png");
	
	graph road_network;
	list<road> carRoad <- [];
	people people_a_suivre;
	//map<point,node> nodes; 
	
	list<people> people_moving <- [];
	float acceleration_max <- 5/3.6;
	list<node> feux;
	int min_embouteillage_people <- 5;
	float max_embouteillage_vitesse <- 1.0;
	
	int nb_people <- 500;
	init {
		
		do load_osm(file:osmfile, road_species: road, building_species: building, node_species: node);
		list<node> noeuds_a_conserver <- [];
		ask (road) {
			if (highway in roads_colors.keys and (shape.points[0] != shape.points[length(shape.points) - 1])) {
				color <- roads_colors at highway;
				if (lanes = 0) {lanes <- 1;}
				if (maxspeed = 0.0) {maxspeed <- 30;}	
				shape_display <- shape + lanes;
				add self to: carRoad;
				angle_source <- shape.points[0] direction_to location;
				angle_dest <- shape.points[length(shape.points) - 1] direction_to location;
				agents_on <- [];
				loop times:lanes {
					add [] to: agents_on;
				}
				add node(source_node) to: noeuds_a_conserver;
				add node(target_node) to: noeuds_a_conserver;
			} else {
				do die;
			}
		}
		ask node {
			if not (self in noeuds_a_conserver)  {do die;}
			else {
				list<road> a_conserver <- [];
				loop ag over: roads_in {
					road rd <- road(ag);
					if (not dead(rd)) {
						add rd to: a_conserver;
					}
				}
				roads_in <- a_conserver;
				list<road> a_conserver2 <- [];
				loop ag over: roads_out {
					road rd <- road(ag);
					if (not dead(rd)) {
						add rd to: a_conserver2;
					}
				}
				roads_out <- a_conserver2;
				//put self in:nodes at: location;
				if (highway = "stop") {
					est_feu <- false;
					est_stop <- true;
					size <- 5.0;
					icon <- stop_icon;
				} else if (highway = "traffic_signals") {
					est_feu <- true;
					est_stop <- false;
					size <- 10.0; 
					icon <- est_vert ? feu_vert_icon :feu_rouge_icon;
				} else {
					est_feu <- false;
					est_stop <- false;
					size <- 5.0;
					icon <- nil;
				}
			}
		}
		do init_feux_rouges();
		road_network <- directed(as_edge_graph(carRoad)) ;
		
		ask building {
			if ((self.building in buildings_colors.keys) and ! bridge) {
				color <- buildings_colors at self.building;
				if (shop != nil) {color <-rgb("cyan");}	
				node_link <- (node as list) closest_to self;
			} else {
				do die;
			}
		} 
		create people number: nb_people { 
			speed_map <- carRoad as_map (each::(each.shape.perimeter * (3600.0 / (each.maxspeed * 1000.0))));
			speed <- 0;
			int nb_arrets <- 2 + rnd(2);
			loop times: nb_arrets {
				building bd <- one_of(building);
				if (empty(listes_etapes) or bd != last(listes_etapes)) {
					add bd to: listes_etapes;
				}
			}
			location <- any_location_in (listes_etapes[0]); 
		}
		people_a_suivre <- one_of(people);	
	}
	
	action init_feux_rouges { 
		feux <- node where (each.est_feu);
		list<list<node>> groupes <- feux simple_clustering_by_distance 50.0; 
		loop gp over: groupes {
			int cpt_init <- rnd(60);
			bool vert <- flip(0.5);
			
			if (length(gp) = 1) {
				ask (node(first(gp))) {
					if (vert) {do passage_vert;} 
					else {do passage_rouge;}
					do calcule_croissement;
				}	
			} else {
				point centroide <- mean (gp collect (node(each)).location);
				int angle_ref <- centroide direction_to node(first(gp)).location;
				bool first <- true;
				int ref_angle <- 0;
				loop si over: gp {
					node ns <- node(si);
					bool vert_si <- vert;
					int ang <- abs((centroide direction_to ns.location) - angle_ref);
					if (ang > 45 and ang < 135) or  (ang > 225 and ang < 315) {
						vert_si <- not(vert_si);
					}
					ask ns {
						compteur <- cpt_init;
						if (vert_si) {do passage_vert;} 
							else {do passage_rouge;}
						est_croissement <- crossing = "traffic_signals";
						if (not empty(roads_in)) {
							if (est_croissement) {
								if (first) {
									ref_angle <-  road(roads_in[0]).angle_dest;
									first <- false;
								}
								loop rd over: roads_in {
									int ang <- abs(road(rd).angle_dest - ref_angle);
									if (ang > 45 and ang < 135) or  (ang > 225 and ang < 315) {
										add road(rd) to: inverse;
									}
								}
							} else {do calcule_croissement;}
						}
					}	
				}
			}
		} 
	}
		
}


species road parent: osm_road {
	rgb color <- rgb("black");
	geometry shape_display;
	int angle_source;
	int angle_dest;
	embouteillage embout_route <- nil;
	list<people> personnes_route update: agents_on accumulate (each);
	bool est_bloquee <- false;
	
	user_command "Bloquer la route" action: bloquer_route;
	user_command "Debloquer la route" action: debloquer_route;
	
	action bloquer_route {
		est_bloquee <- true;
	}
	
	action debloquer_route {
		est_bloquee <- false;
	}
	
	reflex maj_emboutillage when: embout_route != nil or (length(personnes_route) > min_embouteillage_people){
		list<people> bloques <- empty(personnes_route) ? [] : personnes_route where (people(each).real_speed < max_embouteillage_vitesse);
		if (length(bloques) < min_embouteillage_people) {
			if (embout_route != nil) {
				ask embout_route {do die;}
				embout_route <- nil;
			}
		} else {
			if (embout_route != nil) {
				embout_route.personnes_bloquees <- bloques; 
				ask embout_route{do maj_forme;}
			} else {
				create embouteillage returns: eb with: [personnes_bloquees::bloques,route_concernee::self];
				embout_route <- first(eb);
				ask embout_route{do maj_forme;}
			}
		}
	}
	aspect base_ligne {
		draw shape color: est_bloquee ? rgb("red") :rgb("black");
	}
	
	aspect base {
		draw shape_display color: est_bloquee ? rgb("red") : color;
	}
	aspect test {
		draw shape color: rgb(rnd(255),rnd(255),rnd(255));
	}
	aspect max_speed {
		draw shape_display color: maxspeed < 30 ? rgb("red") : ( maxspeed < 50 ? rgb("orange") : ( maxspeed < 70 ? rgb("blue"):rgb("green")) );
	}
	aspect base3D {
		draw shape_display color: est_bloquee ? rgb("red") : rgb("gray");
	}
} 
	
species node parent: osm_node {
	float size ;
	bool est_feu ;
	bool est_stop;
	bool est_vert;
	file icon <- nil;
	int compteur;
	int temps_rouge <- 45;
	int temps_vert <- 45;
	bool est_croissement <- false;
	list<road> inverse;
	
	action calcule_croissement{
		est_croissement <- crossing = "traffic_signals";
		if (est_croissement and not(empty(roads_in))) {
			int ref_angle <-  road(roads_in[0]).angle_dest;
			loop rd over: roads_in {
				int ang <- abs(road(rd).angle_dest - ref_angle);
				if (ang > 45 and ang < 135) or  (ang > 225 and ang < 315) {
					add road(rd) to: inverse;
				}
			}
		}
	}
	
	action passage_vert{
		est_vert <- true;
		icon <- feu_vert_icon;
	}
	action passage_rouge{ 
		est_vert <- false;
		icon <- feu_rouge_icon;  
	} 
	reflex passage_feu when: est_feu{
		compteur <- compteur + 1;
		if (est_vert and compteur >= temps_vert) { 
			compteur <- 0;
			do passage_rouge;
		} else if (not est_vert and compteur >= temps_rouge) {
			compteur <- 0;
			do passage_vert;
		}
	}
	aspect icon {
		if (icon != nil) {
			draw icon size: size;
		}
	}
	aspect base3D {
		if (est_feu) {
			draw sphere(5) color: est_vert ? rgb("green") : rgb("red");
		} else if (est_stop) {
			draw square(3)  color: rgb("orange") depth: 1;
		}
	}
	aspect base { 
		draw square(5) color: (!(est_feu) ? (est_stop ? rgb("orange") : rgb("yellow")) : (est_vert ? rgb("green") : rgb("red"))) ;
	}
} 
	
species building parent: osm_building frequency: 0{
	rgb color <-  rgb(200,200,200);
	node node_link ;
	float hauteur <- 10.0 + rnd(10);
	aspect base { 
		draw shape color: color;
	}
	
	aspect base3D { 
		draw shape color: color depth: hauteur;
	}
}  

species embouteillage {
	list<people> personnes_bloquees;
	road route_concernee;
	action maj_forme{
	 	shape <- route_concernee.shape_display intersection union(personnes_bloquees collect (each.shape + 20));
	}
	aspect base { 
		draw shape color: rgb("red");
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
	float vehicle_length <- 3.0;
	list<point> targets <- [];
	float vitesse_coeff <- 1.2 - (rnd(400) / 1000);
	map<road,float> speed_map;
	
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
		float vitesse <- min([real_speed + acceleration_max, vitesse_coeff * (road(la_route).maxspeed/3.6)]);
		security_distance <- 1 + (vitesse * 5/9 * 3.6); //wikipedia 
		return vitesse;
	}
	
	path calcule_chemin{
		path chemin <- (road_network with_weights speed_map) path_between (location::the_final_target);
		if (chemin != nil and not (empty(chemin.segments))) {
			targets <- [];
			loop edge over: chemin.edges {
				add last(agent(edge).shape.points) to: targets;
			}
			index_path <- 0;
			remove last(targets) from: targets;
			add the_final_target to: targets;
			deplacement <- true;
			road route <- road(chemin.edges[index_path]); 
			add self to: people_moving;
			do enregistrement_route(route,chemin,0);
			return chemin;
		} else {
			return nil;
		}
	}
	
	reflex time_to_go when: not(deplacement) and cpt_wait <= 0 and etape_courante < (length(listes_etapes) - 1) {
		point pt_init <- copy(location); 
		location <- (listes_etapes[etape_courante]).node_link.location;
		if (empty(people_moving at_distance (2 * security_distance + vehicle_length))) {
			the_final_target <- (listes_etapes[etape_courante + 1]).node_link.location;
			the_path <- calcule_chemin();
			if (the_path = nil) {
				etape_courante <- etape_courante + 1;
				location <- pt_init;
			}
		} else {
			location <- pt_init;
		}
		  
	}
	
	bool pret_engagement (road route, int lane, node noeud) {
		list<people> people_on_road <- copy(route.agents_on[lane]);
		float angle_ref <- angle_between(noeud.location, current_road.location, route.location);
		loop ag over: noeud.roads_in {
			road rd <- road(ag);
			if (rd != current_road) {
				float angle <- angle_between(noeud.location, current_road.location, rd.location);
				if (angle > angle_ref) { 
					loop i from: 0 to: rd.lanes - 1 {
						loop agr over: rd.agents_on[i] {
							people pp <- people (agr);
							if (pp.real_speed with_precision 2 > 0.0) {
								add pp to: people_on_road;
							}
						} 
					}	
				}
			}
		}
		return (empty(people_on_road at_distance (security_distance + 2 * vehicle_length)));
	}
	
	bool engagement_prochaine_route(road route, int lane, bool vient_arriver) {
		bool engagement <- true;
		node noeud  <- node(route.source_node);//nodes at location;
		if (noeud = nil) {
			engagement <- pret_engagement(route, lane,noeud);
		} else {
			if (noeud.est_stop) {
				engagement <- not (vient_arriver) and pret_engagement(route, lane,noeud);
			} else if (noeud.est_feu) {
				if (noeud.est_croissement and current_road in noeud.inverse) {
					engagement <- not(noeud.est_vert) and pret_engagement(route, lane,noeud);
				} else {
					engagement <- noeud.est_vert and pret_engagement(route, lane,noeud);
				}
			} else {
				engagement <- pret_engagement(route, lane,noeud);
			}  
		}
		return engagement;
	}  

	reflex move when: deplacement {
		float tps_restant <- 1.0;
		loop while: (tps_restant > 0.0 and deplacement) {
			float tps_before_temp <- tps_restant;
			speed <- choix_vitesse(road(current_road)); 
			tps_restant <- (osm_follow (path: the_path, target: the_target,speed: speed,time: tps_restant)) with_precision 2; 
			deplacement <- (location != the_final_target) ;
			if (deplacement and (location = the_target /*or tps_restant = tps_before_temp*/) ) {
				road route <- road(the_path.edges[index_path + 1]); 
				if (route.est_bloquee or route.embout_route != nil) {
					put (route.est_bloquee ? 9999999 : 99999) at: route in: speed_map;
					path un_nv_chemin <- calcule_chemin();
					if (un_nv_chemin != nil) {
						the_path <- un_nv_chemin;
					}
				}else{
					int lane <- choix_voie(route); 
					list<people> people_on_road <- route.agents_on[lane]; 
					if (engagement_prochaine_route(route, lane,tps_restant  < 1.0)) {
						index_path <- index_path + 1;
						road last <- (road(current_road));
						remove self from: last.agents_on[current_lane];
						do enregistrement_route(route, the_path, lane); 	
					} else {tps_restant <- 0.0;} 
				} 
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
		draw rectangle(4,vehicle_length*2) rotate: 90 + heading color: color;
	} 
	aspect base3D {
		draw rectangle(2,vehicle_length) depth: 2 rotate: 90 + heading color: color;
	} 
}
 
experiment experiment_debug type: gui {
	output {
		display carte_principale {
			species road aspect: base ;
			species building aspect: base ;
			species node aspect: base  ;
			species people aspect: base ;
		}
		
		display carte_embouteillage {
			species road aspect: base_ligne ;
			species embouteillage aspect: base ;
		}
	}
}

experiment experiment_3D type: gui {
	output {
		display carte_principale type: opengl ambient_light: 100{
			species road aspect: base3D refresh: false;
			species building aspect: base3D refresh: false;
			species node aspect: base3D  ;
			species people aspect: base3D ; 
		}
		/*display FirstPerson  type:opengl ambient_light:100 camera_pos:{people_a_suivre.location.x,-people_a_suivre.location.y,20} 
		camera_look_pos:{cos(people_a_suivre.heading)*world.shape.width,-sin(people_a_suivre.heading)*world.shape.height,0} camera_up_vector:{0.0,0.0,1.0} {	
			species road aspect: base3D refresh: false;
			species building aspect: base3D refresh: false;
			species node aspect: base3D  ;
			species people aspect: base3D ; 		
		}*/
	}
}