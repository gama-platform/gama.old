model fire
// gen by Xml2Gaml


global {
	var text_position type: point parameter: 'Position of text:' init: {0.1,0.1} category: 'Display';
	var tree_transparency type: float parameter: 'Transparency of trees' init: 0.5 min: 0 max: 1 category: 'Display';
	var width type: int parameter: 'Width of the environment (in meters):' init: 500 min: 10 max: 1000 category: 'Environment';
	var height type: int parameter: 'Height of the environment (in meters):' init: 500 min: 10 max: 1000 category: 'Environment';
	var fires_number type: int parameter: 'Number of fire starting points:' init: 10 min: 0 category: 'Environment';
	var trees_number type: int parameter: 'Number of trees:' init: 5000 min: 1 category: 'Trees';
	var tree_burning_time type: int init: 100 parameter: 'Number of steps taken by a tree to burn entirely:' category: 'Trees';
	var tree_propagating_time type: int init: 35 parameter: 'Number of steps before a tree begins to propagate the fire:' category: 'Trees';
	var tree_drying_time type: int init: 100 parameter: 'Number of steps before a tree dries after having been watered:' category: 'Trees';
	var tree_max_size type: float parameter: 'Max. diameter of trees (in meters):' init: 12#m min: 1#m max: 30#m category: 'Trees';
	var tree_propagation_probability type: float init: 0.7 parameter: 'Probability for each burning tree to propagate fire:' category: 'Trees';
	var tree_propagation_distance type: int init: 6 parameter: 'Max. propagation distance for fire between trees (in meters):' category: 'Trees';
	var fireman_perception_range type: int init: 30 parameter: 'Range of vision of firemen (in meters):' min: 1 category: 'Firemen';
	var fireman_watering_range type: int init: 3 parameter: 'Max. range of watering of firemen (in meters):' min: 1 category: 'Firemen';
	var fireman_watering_distance type: int init: 6 parameter: 'Distance to a fire at which firemen begin to water (in meters):' min: 1 category: 'Firemen';
	var fireman_speed type: float parameter: 'Speed of firemen (in m/s):' init: 3 min: 0 category: 'Firemen';
	var firemen_number type: int parameter: 'Number of firemen:' init: 8 min: 0 category: 'Firemen';
	var fireman_security_distance type: int parameter: 'Security distance for firemen (in meters):' init: 3 min: 1 category: 'Firemen';
	var fireman_color type: rgb parameter: 'Display color of firemen:' init: rgb [255,255,255] category: 'Firemen';
	init {
		create species: tree number: trees_number;
		ask target: fires_number among (tree as list) {
			set state value: 'burning';
		}
		create species: fireman number: firemen_number;
	}
}
environment width: width height: height torus: false;
entities {
	species tree skills: [situated, visible] control: fsm {
		const original_color type: rgb init: [0, rnd(200) + 55, 0] as rgb;
		var color type: rgb init: original_color;
		const size type: float init: ((rnd(100) / 100) * tree_max_size) + 1;
		const location type: point init: {rnd(width), rnd(height)};
		const range type: float init: tree_propagation_distance + (size / 2);
		const own_max_burning_time type: int init: tree_burning_time - (tree_burning_time / size);
		state intact initial: true {
			enter {
				set color value: original_color;
			}
		}
		state burning {
			enter {
				let duration value: 0;
			}
			set duration value: duration + 1;
			set color value: rgb [255,rnd(255), 0];
			if condition: (duration > tree_propagating_time) and (flip(tree_propagation_probability)) {
				ask target: ((self neighbours_at range) of_species tree) where (each.state = 'intact') {
					set state value: 'burning';
				}
			}
			transition to: destroyed when: duration >= own_max_burning_time;
		}
		state destroyed {
			create species: dead_tree number: 1 with: [location::my location];
			do action: die;
		}
		state protected {
			enter {
				set color value: rgb [0, 0, 100 + rnd(155)];
				let duration value: 0;
			}
			set duration value: duration + 1;
			transition to: intact when: duration >= tree_drying_time;
		}
		aspect {
			draw shape: circle size: size color: color;
		}
	}
	species dead_tree skills: [situated,visible] {
		const color type: rgb init: rgb [rnd(100),rnd(30), rnd(30)];
		const size type: float value: 3;
		aspect default {
			draw shape: square color: color size: 3;
		}
	}
	species fireman skills: [visible, moving] {
		var range type: float value: fireman_perception_range;
		var dynamic_range type: float init: 0 value: dynamic_range > range ? 10 : dynamic_range + 10;
		var speed type: float value: fireman_speed;
		var goal type: tree;
		var location type: point init: {(rnd(1000) / 1000) * width, (rnd(1000) / 1000) * height};
		action communicate_goal {
			let others value: (fireman as list - self);
			ask target: others {
				do action: receive_goal {
					arg new_goal value: myself.goal;
				}
			}
		}
		action receive_goal {
			arg new_goal;
			if condition: goal = nil {
				set goal value: new_goal;
			}
		}
		action water {
			ask target: ((self neighbours_at rnd(fireman_watering_range)) of_species tree) {
				set state value: 'protected';
			}
		}
		reflex updating when: (goal != nil) {
			set goal value: !(goal.state != 'burning') ? goal : nil;
		}
		reflex patrolling when: goal = nil {
			do action: wander {
				arg amplitude value: 180;
			}
			let burning_neighbours value: ((self neighbours_at range) of_species tree) where (each.state = 'burning');
			let possible_goal value: first (burning_neighbours sort_by (self distance_to each));
			if condition: (goal = nil) or ((goal != nil) and (possible_goal != nil) and ((self distance_to goal) > (self distance_to possible_goal))) {
				set goal value: possible_goal;
				do action: communicate_goal;
			}
		}
		reflex targeting when: (goal != nil) and (self distance_to goal > fireman_security_distance) {
			do action: goto {
				arg target value: goal;
			}
		}
		reflex watering when: (goal != nil) and (self distance_to goal < fireman_watering_distance) {
			do action: water;
		}
		aspect {
			draw shape: circle color: fireman_color size: 4;
			if condition: goal = nil {
				draw shape: circle color: fireman_color size: dynamic_range empty: true;
				else {
					draw shape: line to: goal color: fireman_color;
				}
			}
		}
	}
}
output {
	display Forest {
		image name: 'background' file: '../images/soil.jpg';
		species dead_tree aspect: default;
		species tree transparency: tree_transparency;
		species fireman;
		text Legend value: 'Number of burnt trees : ' + dead_tree as int position: text_position color: rgb('white') size: 0.04;
	}
	display Chart refresh_every: 10 {
		chart name: 'Distribution of sizes' type: pie style: exploded {
			data name: "under 4m" value: tree as list count (each.size <= 4);
			data name: "between 4 - 7m" value: tree as list count ((each.size > 4) and (each.size <= 7));
			data name: "above 7m" value: tree as list count (each.size > 7);
		}
	}
	monitor name: "% burnt" value: ((dead_tree as int * 100 / trees_number) as int) as string + '%';
	monitor name: "Time" value: time;
}
