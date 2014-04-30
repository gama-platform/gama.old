/**
 *  multicriteria
 *  Author: Patrick Taillandier
 *  Description: shows how to use multicriteria operators
 */

model multicriteria

global {
	float weight_standing <- 1.0;
	float weight_price <- 1.0;
	float weight_distance <- 1.0;
	float weight_area <- 1.0;
	list criteria <- [["name"::"standing", "weight" :: weight_standing],["name"::"price", "weight" :: weight_price],["name"::"distance", "weight" ::weight_distance],["name"::"area", "weight" :: weight_area]]; 
	
	
	init {
		create people;
		geometry free_space <- copy(shape);
		free_space <- free_space - 10;
		create house number: 10 {
			location <- any_location_in (free_space);
			free_space <- free_space - (shape + 10);
		}
	}
	
	reflex reset_selected {
		ask house {is_selected <- false;}
		criteria <- [["name"::"standing", "weight" :: weight_standing],["name"::"price", "weight" :: weight_price],["name"::"distance", "weight" ::weight_distance],["name"::"area", "weight" :: weight_area]]; 
	}
	
}
	

species people parent: multicriteria_analyzer {
	aspect default {
		draw circle(2) color: °red;
	}
	
	reflex choose_house {
		list<list> cands <- houses_eval();
		int choice <- weighted_means_DM(cands, criteria);
		if (choice >= 0) {
			ask (house at choice) {is_selected <- true;}
		}
	}
	
	list<list> houses_eval {
		list<list> candidates <- [];
		loop bat over: house {
			list<float> cand <- [];
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
	bool is_selected <- false;
	geometry shape <- square(5 + rnd(10));
	float price <- 100000.0 + rnd (400000);
	int standing <- rnd(5);
	rgb color <- rgb(255 * (1 - standing/5.0),255 * (1 - standing/5.0),255);
	aspect default {
		if (is_selected) {
			draw shape + 2.0 color: °red;
		}
		draw shape color: color;
	}
}


experiment multicriteria type: gui {
	parameter "weight of the standing criterion" var:weight_standing;
	parameter "weight of the price criterion" var:weight_price;
	parameter "weight of the distance criterion" var:weight_distance;
	parameter "weight of the area criterion" var:weight_area;
	output {
		display map {
			species house;
			species people;
		}
	}
}
