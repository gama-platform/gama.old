/***
* Name: CityEscape
* Author: kevinchapuis
* Description: An evacuation model of a theoretical city with different alert communication strategies. Hazard is a very simple flood
* and we make the hypothesis that people die when they are in the flood (e.g. Tsunami). The flooding start x time after the beginning
* of the simulation. People escape when they perceive the flood or when they are alerted. There are 3 communication strategies: 
* - 'EVERYONE' = to alert everyone at the start of the simulation
* - 'STAGED' = to alert nb_people/nb_stages random people every (time_of_hazard-buffer_time)/nb_stages minutes
* - 'SPATIAL' = to alert nb_people/nb_stages closest people to exits every (time_of_hazard-buffer_time)/nb_stages minutes
* Where (time_of_hazard-buffer_time)/nb_stages correspond to the time between stages computed using the three parameters:
* - Time before hazard = time_of_hazard
* - Time alert buffer before hazard = buffer_time
* - Number of stages = nb_stages
* Tags: evacuation, traffic, hazard, gis, water
***/
model CityEscape

global {
	
	// Starting date of the simulation
	date starting_date <- #now;
	
	// Time step to represent very short term movement (for congestion)
	float step <- 10#sec;
	
	int nb_of_people;
	
	// To initialize perception distance of inhabitant
	float min_perception_distance <- 10.0;
	float max_perception_distance <- 30.0;
	
	// Represents the capacity of a road indicated as: number of inhabitant per #m of road
	float road_density;
	
	// Parameters of the strategy
	int time_after_last_stage;
	string the_alert_strategy;
	int nb_stages;
	
	// Parameters of hazard
	int time_before_hazard;
	float flood_front_speed;
	
	file road_file <- file("../includes/city_environment/road_environment.shp");
	file buildings <- file("../includes/city_environment/building_environment.shp");
	file evac_points <- file("../includes/city_environment/evacuation_environment.shp");
	file water_body <- file("../includes/city_environment/sea_environment.shp");
	geometry shape <- envelope(envelope(road_file)+envelope(water_body));
	
	// Graph road
	graph<geometry, geometry> road_network;
	map<road,float> road_weights;
	
	// Output the number of casualties
	int casualties;
	
	init {
				
		create road from:road_file;
		create building from:buildings;
		create evacuation_point from:evac_points;
		create hazard from: water_body;
		
		create inhabitant number:nb_of_people {
			location <- any_location_in(one_of(building));
			safety_point <- any(evacuation_point);
			perception_distance <- rnd(min_perception_distance, max_perception_distance);
		}
		
		create crisis_manager;
		
		road_network <- as_edge_graph(road);
		road_weights <- road as_map (each::each.shape.perimeter);
	
	}
	
	// Stop the simulation when everyone is either saved :) or dead :(
	reflex stop_simu when:inhabitant all_match (each.saved or each.drowned) {
		do pause;
	}
	
}

/*
 * Agent responsible of the communication strategy
 */
species crisis_manager {
	
	/*
	 * Time between each alert stage (#s)
	 */
	float alert_range;
	
	/*
	 * The number of people to alert every stage
	 */
	int nb_per_stage;
	
	/*
	 * Parameter to compute spatial area of each stage in the SPATIAL strategy
	 */
	geometry buffer;
	float distance_buffer;
	
	init {
		// For stage strategy
		int modulo_stage <- length(inhabitant) mod nb_stages; 
		nb_per_stage <- int(length(inhabitant) / nb_stages) + (modulo_stage = 0 ? 0 : 1);
		
		// For spatial strategy
		buffer <- geometry(evacuation_point collect (each.shape buffer 1#m));
		distance_buffer <- world.shape.height / nb_stages;
		
		alert_range <- (time_before_hazard#mn - time_after_last_stage#mn) / nb_stages;
	}
	
	/*
	 * If the crisis manager should send an alert
	 */
	reflex send_alert when: alert_conditional() {
		ask alert_target() { self.alerted <- true; }
	}
	
	/*
	 * The conditions to send an alert : return true at cycle = 0 and then every(alert_range)
	 * depending on the strategy used
	 */
	bool alert_conditional {
		if(the_alert_strategy = "STAGED" or the_alert_strategy = "SPATIAL"){
			return every(alert_range);
		} else {
			if(cycle = 0){
				return true;
			} else {
				return false;
			}
		}
	}
	
	/*
	 * Who to send the alert to: return a list of inhabitant according to the strategy used
	 */
	list<inhabitant> alert_target {
		switch the_alert_strategy {
			match "STAGED" {
				return nb_per_stage among (inhabitant where (each.alerted = false));
			}
			match "SPATIAL" {
				buffer <- buffer buffer distance_buffer;
				return inhabitant overlapping buffer;
			}
			match "EVERYONE" {
				return list(inhabitant);
			}
			default {
				return [];
			}
		}
	}
	
}

/*
 * Represent the water body. When attribute triggered is turn to true, inhabitant
 * start to see water as a potential danger, and try to escape
 */
species hazard {
	
	// The date of the hazard
	date catastrophe_date;
	
	// Is it a tsunami ? (or just a little regular wave)
	bool triggered;
	
	init {
		catastrophe_date <- current_date + time_before_hazard#mn;
	}
	
	/*
	 * The shape the represent the water expend every cycle to mimic a (big) wave
	 */
	reflex expand when:catastrophe_date < current_date {
		if(not(triggered)) {triggered <- true;}
		shape <- shape buffer (flood_front_speed#m/#mn * step) intersection world;
	}
	
	aspect default {
		draw shape color:#blue;
	}
	
}

/*
 * Represent the inhabitant of the area. They move at foot. They can pereive the hazard or be alerted
 * and then will try to reach the one randomly choose exit point
 */
species inhabitant skills:[moving] {
	
	// The state of the agent
	bool alerted <- false;
	bool drowned <- false;
	bool saved <- false;
	
	// How far (#m) they can perceive
	float perception_distance;
	
	// The exit point they choose to reach
	evacuation_point safety_point;
	// How fast inhabitant can run
	float speed <- 10#km/#h;
	
	/*
	 * Am I drowning ?
	 */
	reflex drown when:not(drowned or saved) {
		if(first(hazard) covers self){
			drowned <- true;
			casualties <- casualties + 1; 
		}
	}
	
	/*
	 * Is there any danger around ?
	 */
	reflex perceive when: not(alerted or drowned) and first(hazard).triggered {
		if self.location distance_to first(hazard).shape < perception_distance {
			alerted <- true;
		}
	}
	
	/*
	 * When alerted people will try to go to the choosen exit point
	 */
	reflex evacuate when:alerted and not(drowned or saved) {
		do goto target:safety_point on: road_network move_weights:road_weights;
		if(current_edge != nil){
			road the_current_road <- road(current_edge);  
			the_current_road.users <- the_current_road.users + 1;
		}
	}
	
	/*
	 * Am I safe ?
	 */
	reflex escape when: not(saved) and location distance_to safety_point < 2#m{
		saved <- true;
		alerted <- false;
	}
	
	aspect default {
		draw  sphere(1#m) color:drowned ? #black : (alerted ? #red : #green);
	}
	
}

/*
 * The point of evacuation
 */
species evacuation_point {
	
	int count_exit <- 0 update: length((inhabitant where each.saved) at_distance 2#m);
		
	aspect default {
		draw circle(1#m+49#m*count_exit/nb_of_people) color:#green;
	}
	
}

/*
 * The roads inhabitant will use to evacuate. Roads compute the congestion of road segment
 * accordin to the Underwood function.
 */
species road {
	
	// Number of user on the road section
	int users;
	// The capacity of the road section
	int capacity <- int(shape.perimeter*road_density);
	// The Underwood coefficient of congestion
	float speed_coeff <- 1.0;
	
	// Update weights on road to compute shortest path and impact inhabitant movement
	reflex update_weights {
		speed_coeff <- max(0.05,exp(-users/capacity));
		road_weights[self] <- shape.perimeter / speed_coeff;
		users <- 0;
	}
	
	// Cut the road when flooded so people cannot use it anymore
	reflex flood_road {
		if(hazard first_with (each covers self) != nil){
			road_network >- self; 
			do die;
		}
	}
	
	aspect default{
		draw shape width: 4#m-(3*speed_coeff)#m color:rgb(55+200*users/capacity,0,0);
	}	
	
}

/*
 * People are located in building at the start of the simulation
 */
species building {
	aspect default {
		draw shape color: #gray border: #black depth: 1;
	}
}

experiment "Run" {
	float minimum_cycle_duration <- 0.1;
		
	parameter "Alert Strategy" var:the_alert_strategy init:"STAGED" among:["NONE","STAGED","SPATIAL","EVERYONE"] category:"Alert";
	parameter "Number of stages" var:nb_stages init:6 category:"Alert";
	parameter "Time alert buffer before hazard" var:time_after_last_stage init:5 unit:#mn category:"Alert";
	
	parameter "Road density index" var:road_density init:6.0 min:0.1 max:10.0 category:"Congestion";
	
	parameter "Speed of the flood front" var:flood_front_speed init:5.0 min:1.0 max:30.0 unit:#m/#mn category:"Hazard";
	parameter "Time before hazard" var:time_before_hazard init:5 min:0 max:10 unit:#mn category:"Hazard";
	
	parameter "Number of people" var:nb_of_people init:500 min:100 max:20000 category:"Initialization";
	
	output {
		display my_display type:3d axes:false{ 
			species road;
			species evacuation_point;
			species building;
			species hazard ;
			species inhabitant;
		}
		monitor "Number of casualties" value:casualties;
	}	
	
}



