/**
* Name: ExempleModele
* Author: mbourgais
* Description:  Modèle d'éxemple pour l'article des JFSMA
* Tag : Tag1, Tag2, TagN
*/

model ExempleModele

global {
	file shapefile_roads <- file("../includes/Rouen roads.shp");
	file shapefile_hazard <- file("../includes/Technological hazard.shp");
	file shapefile_shelters <- file("../includes/Escapes.shp");
	geometry shape <- envelope(shapefile_roads);
	graph road_network;
	map<road,float> current_weights;
	
	float hazard_distance <- 400.0;
	float catastrophe_distance <- 100.0;
	float proba_detect_hazard <- 0.2;
	float proba_detect_other_escape <- 0.01;
	float other_distance <- 10.0;
	
	init {
		create road from: shapefile_roads;
		create hazard from: shapefile_hazard;
		create catastrophe;
		create shelter from: shapefile_shelters;
		create people number: 500{
			location <- any_location_in(one_of(road));
			do add_desire(at_target);
			if(flip(0.9)){
				fearful<-true;
			}else{
				fearful <- false;
			}
			charisma<-rnd(1.0);
			receptivity<-rnd(1.0);
      	}
      	road_network <- as_edge_graph(road);
      	current_weights <- road as_map (each::each.shape.perimeter);
	}
	
	reflex update_speeds when: every(10){
		current_weights <- road as_map (each::each.shape.perimeter / each.speed_coeff);
		road_network <- road_network with_weights current_weights;
	}
	
	reflex stop_sim when: empty(people) {
		do pause;
	}
}
 
species people skills: [moving] control: simple_bdi{
	point target;
	float speed <- 30 #km/#h;
	rgb color <- #blue;
	bool escape_mode <- false;
	predicate at_target <- new_predicate("at_target") with_priority 1;
	predicate in_shelter <- new_predicate("shelter") with_priority 5;
	predicate has_target <- new_predicate("has target") with_priority 2;
	predicate has_shelter <- new_predicate("has shelter") with_priority 10;
	bool noTarget<-true;
	
	float charisma <- rnd(1.0)/*0.5*/;
	float receptivity <-rnd(1.0)/*0.5*/;
	bool fearful;
	
	bool use_emotions_architecture <- true;
	
	perceive target:hazard in: hazard_distance when: not escape_mode and flip(proba_detect_hazard){
		
		ask myself {
			do add_uncertainty(new_predicate("catastrophe"));
			do add_desire(new_predicate("catastrophe",false) with_priority 0.0);
			if(fearful){
				do to_escape_mode;
			}else{
				color<-#darkgreen;
			}
		}
	}
	
	perceive target:catastrophe in:catastrophe_distance{
		ask myself{
			do add_belief(new_predicate("catastrophe"));
			if(not escape_mode){
				do to_escape_mode;
			}
		}
	}
	
	perceive target:people in: other_distance when: not escape_mode {
		
		unconscious_contagion emotion:new_emotion("fearConfirmed",new_predicate("catastrophe"))
		 when:fearful;
		unconscious_contagion emotion:new_emotion("fearConfirmed",new_predicate("catastrophe"))
		 when: has_emotion(new_emotion("fear",new_predicate("catastrophe")));
		unconscious_contagion emotion:new_emotion("fear") charisma: charisma receptivity:receptivity;
		conscious_contagion emotion_detected:new_emotion("fearConfirmed",new_predicate("catastrophe"))
		 emotion_created:new_emotion("fear",new_predicate("catastrophe")) charisma:charisma receptivity: receptivity;
	}
	
	rule emotion:new_emotion("fear" ,new_predicate("catastrophe")) new_desire:in_shelter remove_intention:at_target when: fearful ;
	rule emotion:new_emotion("fearConfirmed",new_predicate("catastrophe")) remove_intention: at_target new_desire:in_shelter;
	rule belief:new_predicate("catastrophe") remove_intention:at_target new_desire:in_shelter;
	
	
	
	plan evacuationFast intention: in_shelter emotion: new_emotion("fear_confirmed",new_predicate("catastrophe")) priority:2 {
		
		color <- #yellow;
		speed <- 60 #km/#h;
		if (target = nil or noTarget) {
			target <- (shelter closest_to self).location;
			noTarget <- false;
		}
		else  {
			do goto target: target on: road_network move_weights: current_weights recompute_path: false;
			if (target = location)  {
				do die;
			}		
		}
	}	
	
	plan evacuation intention: in_shelter /*when: not has_emotion(new_emotion("fear_confirmed",new_predicate("catastrophe")))*/{
		
		color <-#darkred;
		if (target = nil or noTarget) {
			target <- (shelter closest_to self).location;
			noTarget <- false;
		}
		else  {
			do goto target: target on: road_network move_weights: current_weights recompute_path: false;
			if (target = location)  {
				do die;
			}		
		}
	}
	
	plan normal_move intention: at_target  {
		
		if (target = nil) {
			target <- any_location_in(one_of(road));
		} else {
			do goto target: target on: road_network move_weights: current_weights recompute_path: false;
			if (target = location)  {
				target <- nil;
				noTarget<-true;
			}
		}
	}
	
	action to_escape_mode {
		escape_mode <- true;
		color <- #darkred;
		target <- nil;	
		noTarget <- true;
		do remove_intention(at_target, true);
	}
	
	
	aspect default {
		draw triangle(10) rotate: heading + 90 color: color;
	}
}

species road {
	float capacity <- 1 + shape.perimeter/50;
	int nb_people <- 0 update: length(people at_distance 1);
	float speed_coeff <- 1.0 update:  exp(-nb_people/capacity) min: 0.1;
	
	aspect default {
		draw shape color: #black;
	}
}

species shelter {
	aspect default {
		draw circle(10) color: #green;
	}
}

species hazard {
	aspect default {
		draw circle(hazard_distance) empty: true color: #red depth:1;
	}
}

species catastrophe{
	init{
		location <- first(hazard).location;
	}
	aspect default{
		draw circle(catastrophe_distance)empty: true color: #green depth:2;
	}
}

experiment main type: gui {
	output {
		display map type: opengl{
			species shelter refresh: false;
			species catastrophe refresh: false;
			species hazard refresh: false;
			species road refresh: false;
			species people;
		}
	}
}
