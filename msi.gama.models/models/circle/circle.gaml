model circle

global {
	var number_of_agents type: int parameter: 'Number of Agents' init: 100 min: 1;
	var radius_of_circle type: int parameter: 'Radius of Circle' init: 690 min: 10;
	var repulsion_strength type: int parameter: 'Strength of Repulsion' init: 5 min: 1;
	var width_and_height_of_environment type: int parameter: 'Dimensions' init: 1600 min: 10; 
	var range_of_agents type: int parameter: 'Range of Agents' init: 25 min: 1;
	var speed_of_agents type: float parameter: 'Speed of Agents' init: 2 min: 0.1; 
	var size_of_agents type: int init: 10;
	const center type: point init: {width_and_height_of_environment/2,width_and_height_of_environment/2};

	init {
		create species: cells number: number_of_agents { 
			set location value: {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
		}
	}  
} 

environment width: width_and_height_of_environment height: width_and_height_of_environment torus: true; 

entities {
	species cells skills: [moving] {  
		const color type: rgb init: [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		const size type: float init: size_of_agents;
		const range type: float init: range_of_agents; 
		const speed type: float init: speed_of_agents;  
		var heading type: int init: rnd(359);
		var shape type: geometry init: circle (12) value: circle (size);
		
		reflex go_to_center {
			set heading value: (((self distance_to center) > radius_of_circle) ? self towards center : (self towards center) - 180);
			do move speed: speed; 
		}
		
		reflex flee_others {
			let close type: cells value: one_of ( ( (self neighbours_at range) of_species cells) sort_by (self distance_to each) );
			if close != nil {
				set heading value: (self towards close) - 180;
				let dist value: self distance_to close;
				do move speed: dist / repulsion_strength heading: heading;
			}
		}
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
}

output {
	display Circle refresh_every: 1 {
		species cells;
	}
}
