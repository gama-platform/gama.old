model circle


global {
	var number_of_agents type: int parameter: 'Number of Agents' init: 100 min: 1;
	var radius_of_circle type: int parameter: 'Radius of Circle' init: 15 min: 10;
	var repulsion_strength type: int parameter: 'Strength of Repulsion' init: 10 min: 1;
	var width_and_height_of_environment type: int parameter: 'Dimensions' init: 500 min: 10;
	var range_of_agents type: int parameter: 'Range of Agents' init: 4 min: 1;
	var speed_of_agents type: float parameter: 'Speed of Agents' init: 2 min: 0.1;
	const center type: point init: {width_and_height_of_environment/2,width_and_height_of_environment/2};
	init {
		create species: cells number: number_of_agents {
			set location value: {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
		}
	}
}
environment width: width_and_height_of_environment height: width_and_height_of_environment torus: true;
entities {
	species cells skills: [moving, situated, visible] {
		const color type: rgb init: [100 + rnd(155),100 + rnd(155), 100 + rnd(155)] as rgb;
		const size type: float init: 10 min: 1 max: 10;
		const range type: float init: range_of_agents;
		const speed type: float init: speed_of_agents;
		var heading type: int init: rnd(359);
		reflex go_to_center {
			set heading value: ((self distance_to center) > radius_of_circle) ? towards(center) : towards(center) - 180;
			do action: move;
		}
		reflex flee_others {
			let close value: one_of ((self neighbours_at range) of_species cells);
			if condition: close != nil {
				set heading value: towards(close) - 180;
				let dist value: self distance_to close;
				do action: move {
					arg distance value: dist / repulsion_strength;
				}
			}
		}
	}
}
output {
	display Circle refresh_every: 1 {
		species cells;
	}
}
