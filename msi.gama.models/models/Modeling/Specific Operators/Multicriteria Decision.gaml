/**
* Name: Multicriteria
* Author: Patrick Taillandier
* Description: This model shows how to use different methods of multicriteria analysis to look for the best house. Five methods are used : 
* the Weighted_Means method , the Electre method , the Promethee method, the Fuzzy Choquet Integral  anc the Evidence Theory method. Each method uses 
* different criteria with different weights according to the methods used. 
* Tags: multi_criteria
*/

model multicriteria

global {
	float weight_standing <- 1.0;
	float weight_price <- 1.0;
	float weight_distance <- 1.0;
	float weight_area <- 1.0;
	float p_standing <- 0.5;
	float p_price <- 0.5;
	float p_distance <- 0.5;
	float p_area <- 0.5;
	float q_standing <- 0.0;
	float q_price <- 0.0;
	float q_distance <- 0.0;
	float q_area <- 0.0;
	float v_standing <- 1.0;
	float v_price <- 1.0;
	float v_distance <- 1.0;
	float v_area <- 1.0;
	float s_standing <- 1.0;
	float s_price <- 1.0;
	float s_distance <- 1.0;
	float s_area <- 1.0;
	float s1_standing <- 0.0;
	float s1_price <- 0.0;
	float s1_distance <- 0.0;
	float s1_area <- 0.0;
	float s2_standing <- 1.0;
	float s2_price <- 1.0;
	float s2_distance <- 1.0;
	float s2_area <- 1.0;
	float v1p_standing <- 0.0;
	float v1p_price <- 0.0;
	float v1p_distance <- 0.0;
	float v1p_area <- 0.0;
	float v2p_standing <- 1.0;
	float v2p_price <- 1.0;
	float v2p_distance <- 1.0;
	float v2p_area <- 1.0;
	float v1c_standing <- 0.0;
	float v1c_price <- 0.0;
	float v1c_distance <- 0.0;
	float v1c_area <- 0.0;
	float v2c_standing <- 0.0;
	float v2c_price <- 0.0;
	float v2c_distance <- 0.0;
	float v2c_area <- 0.0;
	
	//Lists that will store the different criteria to use for each method linked with their weights
	list<string> criteria <- ["standing", "price", "distance", "area"];
	list criteria_WM <- [["name"::"standing", "weight" :: weight_standing],["name"::"price", "weight" :: weight_price],["name"::"distance", "weight" ::weight_distance],["name"::"area", "weight" :: weight_area]]; 
	list criteria_Electre <- [["name"::"standing", "weight" :: weight_standing, "p"::p_standing, "q"::q_standing, "v"::v_standing, "maximize" :: true],["name"::"price", "weight" :: weight_price, "p"::p_price, "q"::q_price, "v"::v_price,  "maximize" :: true],["name"::"distance", "weight" ::weight_distance, "p"::p_distance, "q"::q_distance, "v"::v_distance,  "maximize" :: true],["name"::"area", "weight" :: weight_area,  "p"::p_area, "q"::q_area, "v"::v_area,  "maximize" :: true]]; 
	list criteria_Promethee <- [["name"::"standing", "weight" :: weight_standing, "p"::p_standing, "q"::q_standing, "s"::s_standing, "maximize" :: true],["name"::"price", "weight" :: weight_price, "p"::p_price, "q"::q_price, "s"::s_price,  "maximize" :: true],["name"::"distance", "weight" ::weight_distance, "p"::p_distance, "q"::q_distance, "s"::s_distance,  "maximize" :: true],["name"::"area", "weight" :: weight_area,  "p"::p_area, "q"::q_area, "s"::s_area,  "maximize" :: true]];  
	list criteria_ET <- [["name"::"standing", "s1"::s1_standing, "s2"::s2_standing, "v1p"::v1p_standing, "v2p"::v2p_standing, "v1c"::v1c_standing, "v2c"::v2c_standing,"maximize" :: true],["name"::"price", "s1"::s1_price, "s2"::s2_price, "v1p"::v1p_price, "v2p"::v2p_price, "v1c"::v1c_price, "v2c"::v2c_price,  "maximize" :: true],["name"::"distance", "s1"::s1_distance, "s2"::s2_distance, "v1p"::v1p_distance, "v2p"::v2p_distance, "v1c"::v1c_distance, "v2c"::v2c_distance,  "maximize" :: true],["name"::"area", "s1"::s1_area, "s2"::s2_area, "v1p"::v1p_area, "v2p"::v2p_area, "v1c"::v1c_area, "v2c"::v2c_area, "maximize" :: true]];
	map<list<string>,float> criteria_FC <- map<list<string>, float>([ 
		[["standing"]::weight_standing, ["price"]::weight_price, ["distance"]:: weight_distance, ["area"]::weight_area,
		["standing","price"]::(weight_standing+weight_price) * 2/3,["area","price"]::(weight_standing+weight_price) * 2/3 , 
		["standing","price","area"]::(weight_standing+weight_price + weight_area) * 2/3]
	]); 
	
	init {
		create people;
		geometry free_space <- copy(shape);
		free_space <- free_space - 10;
		create house number: 15 {
			location <- any_location_in (free_space);
			free_space <- free_space - (shape + 10);
		}
	}
	
	reflex reset_selected {
		ask house {
			is_selected_WM <- false;
			is_selected_electre <- false;
			is_selected_promethee <- false;
			is_selected_ET <- false;
			is_selected_choquet <- false;
		}
		criteria_WM <- [["name"::"standing", "weight" :: weight_standing],["name"::"price", "weight" :: weight_price],["name"::"distance", "weight" ::weight_distance],["name"::"area", "weight" :: weight_area]]; 
		criteria_Electre <- [["name"::"standing", "weight" :: weight_standing, "p"::p_standing, "q"::q_standing, "v"::v_standing, "maximize" :: true],["name"::"price", "weight" :: weight_price, "p"::p_price, "q"::q_price, "v"::v_price,  "maximize" :: true],["name"::"distance", "weight" ::weight_distance, "p"::p_distance, "q"::q_distance, "v"::v_distance,  "maximize" :: true],["name"::"area", "weight" :: weight_area,  "p"::p_area, "q"::q_area, "v"::v_area,  "maximize" :: true]]; 
		criteria_Promethee <- [["name"::"standing", "weight" :: weight_standing, "p"::p_standing, "q"::q_standing, "s"::s_standing, "maximize" :: true],["name"::"price", "weight" :: weight_price, "p"::p_price, "q"::q_price, "s"::s_price,  "maximize" :: true],["name"::"distance", "weight" ::weight_distance, "p"::p_distance, "q"::q_distance, "s"::s_distance,  "maximize" :: true],["name"::"area", "weight" :: weight_area,  "p"::p_area, "q"::q_area, "s"::s_area,  "maximize" :: true]]; 
		criteria_ET <- [["name"::"standing", "s1"::s1_standing, "s2"::s2_standing, "v1p"::v1p_standing, "v2p"::v2p_standing, "v1c"::v1c_standing, "v2c"::v2c_standing,"maximize" :: true],["name"::"price", "s1"::s1_price, "s2"::s2_price, "v1p"::v1p_price, "v2p"::v2p_price, "v1c"::v1c_price, "v2c"::v2c_price,  "maximize" :: true],["name"::"distance", "s1"::s1_distance, "s2"::s2_distance, "v1p"::v1p_distance, "v2p"::v2p_distance, "v1c"::v1c_distance, "v2c"::v2c_distance,  "maximize" :: true],["name"::"area", "s1"::s1_area, "s2"::s2_area, "v1p"::v1p_area, "v2p"::v2p_area, "v1c"::v1c_area, "v2c"::v2c_area, "maximize" :: true]];
		criteria_FC <- map<list<string>, float>([ 
		[["standing"]::weight_standing, ["price"]::weight_price, ["distance"]:: weight_distance, ["area"]::weight_area,
		["standing","price"]::(weight_standing+weight_price) * 2/3,["area","price"]::(weight_standing+weight_price) * 2/3 , 
		["standing","price","area"]::(weight_standing+weight_price + weight_area) * 2/3]]);
	}
	
}
	

species people  {
	aspect default {
		draw sphere(2) color: #red;
	}
	
	reflex choose_house_weighted_means {
		list<list> cands <- houses_eval();
		int choice <- weighted_means_DM(cands, criteria_WM);
		if (choice >= 0) {
			ask (house at choice) {is_selected_WM <- true;}
		}
	}
	
	reflex choose_house_fuzzy_choquet {
		list<list> cands <- houses_eval();
		int choice <- fuzzy_choquet_DM(cands, criteria, criteria_FC);
		if (choice >= 0) {
			ask (house at choice) {is_selected_choquet <- true;}
		}
	}
	
	reflex choose_house_promethee {
		list<list> cands <- houses_eval();
		int choice <- promethee_DM(cands, criteria_Promethee);
		if (choice >= 0) {
			ask (house at choice) {is_selected_promethee <- true;}
		}
	}
	reflex choose_house_electre {
		list<list> cands <- houses_eval();
		int choice <- electre_DM(cands, criteria_Electre, 0.7);
		if (choice >= 0) {
			ask (house at choice) {is_selected_electre <- true;}
		}
	}
	reflex choose_house_evidence_theory {
		list<list> cands <- houses_eval();
		int choice <- evidence_theory_DM(cands, criteria_ET, true);
		if (choice >= 0) {
			ask (house at choice) {is_selected_ET <- true;}
		}
	}
	
	list<list> houses_eval {
		list<list> candidates;
		loop bat over: house {
			list<float> cand;
			add bat.standing / 5 to: cand;
			add ((500000 - bat.price) / 500000) to: cand;
			add ((100 - (self distance_to bat)) / 100) to: cand;
			add (bat.shape.area / 15^2) to: cand;
			add cand to: candidates;
		}
		return candidates;
	}
	
}

species house {
	bool is_selected_WM <- false;
	bool is_selected_electre <- false;
	bool is_selected_promethee <- false;
	bool is_selected_ET <- false;
	bool is_selected_choquet <- false;
	geometry shape <- square(5 + rnd(10));
	float price <- 100000.0 + rnd (400000);
	int standing <- rnd(5);
	rgb color <- rgb(255 * (1 - standing/5.0),255 * (1 - standing/5.0),255);
	float height <- price / 50000;
	aspect weighted_means_aspect {
		if (is_selected_WM) {
			draw shape + 2.0 color: #red;
		}
		draw shape color: color depth: height;
	}
	aspect electre_aspect {
		if (is_selected_electre) {
			draw shape + 2.0 color: #red;
		}
		draw shape color: color depth: height;
	}
	aspect promethee_aspect {
		if (is_selected_promethee) {
			draw shape + 2.0 color: #red;
		}
		draw shape color: color depth: height;
	}
	aspect evidence_theory_aspect {
		if (is_selected_ET) {
			draw shape + 2.0 color: #red;
		}
		draw shape color: color depth: height;
	}
	aspect choquet_aspect {
		if (is_selected_choquet) {
			draw shape + 2.0 color: #red;
		}
		draw shape color: color depth: height;
	}
}


experiment multicriteria type: gui {
	parameter "weight of the standing criterion" var:weight_standing category: "Weight";
	parameter "weight of the price criterion" var:weight_price category: "Weight";
	parameter "weight of the distance criterion" var:weight_distance category: "Weight";
	parameter "weight of the area criterion" var:weight_area category: "Weight";
	parameter "preference threshold of the standing criterion" var:p_standing category: "Preference";
	parameter "preference threshold of the price criterion" var:p_price category: "Preference";
	parameter "preference threshold of the distance criterion" var:p_distance category: "Preference";
	parameter "preference threshold of the area criterion" var:p_area category: "Preference";
	parameter "indifference threshold of the standing criterion" var:q_standing category: "Indifference";
	parameter "indifference threshold of the price criterion" var:q_price category: "Indifference";
	parameter "indifference threshold of the distance criterion" var:q_distance category: "Indifference";
	parameter "indifference threshold of the area criterion" var:q_area category: "Indifference";
	parameter "veto threshold of the standing criterion" var:v_standing category: "Veto";
	parameter "veto threshold of the price criterion" var:v_price category: "Veto";
	parameter "veto threshold of the distance criterion" var:v_distance category: "Veto";
	parameter "veto threshold of the area criterion" var:v_area category: "Veto";
	parameter "max preference value of the standing criterion" var:v_standing category: "Max preference value";
	parameter "max preference value of the price criterion" var:v_price category: "Max preference value";
	parameter "max preference value of the distance criterion" var:v_distance category: "Max preference value";
	parameter "max preference value of the area criterion" var:v_area category: "Max preference value";
	parameter "min criterion threshold of the standing criterion" var:s1_standing category: "Evidence Theory";
	parameter "min criterion threshold of the price criterion" var:s1_price category: "Evidence Theory";
	parameter "min criterion threshold of the distance criterion" var:s1_distance category: "Evidence Theory";
	parameter "min criterion threshold of the area criterion" var:s1_area category: "Evidence Theory";
	parameter "max criterion threshold of the standing criterion" var:s2_standing category: "Evidence Theory";
	parameter "max criterion threshold of the price criterion" var:s2_price category: "Evidence Theory";
	parameter "max criterion threshold of the distance criterion" var:s2_distance category: "Evidence Theory";
	parameter "max criterion threshold of the area criterion" var:s2_area category:"Evidence Theory";
	parameter "min preference of the standing criterion" var:v1p_standing category: "Evidence Theory";
	parameter "min preference of the price criterion" var:v1p_price category: "Evidence Theory";
	parameter "min preference of the distance criterion" var:v1p_distance category: "Evidence Theory";
	parameter "min preference of the area criterion" var:v1p_area category: "Evidence Theory";
	parameter "max preference of the standing criterion" var:v2p_standing category: "Evidence Theory";
	parameter "max preference of the price criterion" var:v2p_price category: "Evidence Theory";
	parameter "max preference of the distance criterion" var:v2p_distance category: "Evidence Theory";
	parameter "max preference of the area criterion" var:v2p_area category: "Evidence Theory";
	parameter "min rejection of the standing criterion" var:v1c_standing category: "Evidence Theory";
	parameter "min rejection of the price criterion" var:v1c_price category: "Evidence Theory";
	parameter "min rejection of the distance criterion" var:v1c_distance category: "Evidence Theory";
	parameter "min rejection of the area criterion" var:v1c_area category: "Evidence Theory";
	parameter "max rejection of the standing criterion" var:v2c_standing category: "Evidence Theory";
	parameter "max rejection of the price criterion" var:v2c_price category: "Evidence Theory";
	parameter "max rejection of the distance criterion" var:v2c_distance category: "Evidence Theory";
	parameter "max rejection of the area criterion" var:v2c_area category: "Evidence Theory";
	output {
		layout #split;
		display Map_Weighted_Means type: 3d{
			species house aspect: weighted_means_aspect;
			species people;
		}
		display Map_Electre type: 3d{
			species house aspect: electre_aspect;
			species people;
		}
		display Map_Promethee type: 3d{
			species house aspect: promethee_aspect;
			species people;
		}
		display Map_Evidence_theory type: 3d{
			species house aspect: evidence_theory_aspect;
			species people;
		}
		display Map_Choquet type: 3d{
			species house aspect: choquet_aspect;
			species people;
		}
	}
}
