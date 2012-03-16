model boids

global {
	var number_of_agents type: int parameter: 'true' init: 500 min: 1 max: 1000000;
	var number_of_obstacles type: int parameter: 'true' init: 0 min: 0;
	var maximal_speed type: float parameter: 'true' init: 15 min: 0.1 max: 15;
	var cohesion_factor type: int parameter: 'true' init: 200;
	var alignment_factor type: int parameter: 'true' init: 100;
	var minimal_distance type: float parameter: 'true' init: 10.0;
	var maximal_turn type: int parameter: 'true' init: 90 min: 0 max: 359;
	var width_and_height_of_environment type: int parameter: 'true' init: 2000;
	var torus_environment type: bool parameter: 'true' init: false;
	var apply_cohesion type: bool init: true parameter: 'true';
	var apply_alignment type: bool init: true parameter: 'true';
	var apply_separation type: bool init: true parameter: 'true'; 
	var apply_goal type: bool init: true parameter: 'true';
	var apply_avoid type: bool init: true parameter: 'true';
	var apply_wind type: bool init: true parameter: 'true';
	var moving_obstacles type: bool init: false parameter: 'true';
	var bounds type: int parameter: 'true' init: 50;
	var wind_vector type: point init: {0,0} parameter: 'true';
	var goal_duration type: int init: 30 value: goal_duration - 1;
	var goal type: point init: {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
	var images type: list of: string init: ['../images/bird1.png','../images/bird2.png','../images/bird3.png'];
	var xmin type: int value: bounds;
	var ymin type: int value: bounds;
	var xmax type: int value: width_and_height_of_environment - bounds;
	var ymax type: int value: width_and_height_of_environment - bounds; 
	init {
		create species: boids number: number_of_agents {
			set location value: {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		}
		create species: obstacle number: number_of_obstacles {
			set location value: {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		}
		create species: boids_goal number: 1 {
			set location value: goal;
		}
	}
}

environment width: width_and_height_of_environment height: width_and_height_of_environment torus: torus_environment;
entities {
	species boids_goal skills: moving {
		const range type: float init: 20;
		const size type: float init: 10;
		reflex {
			do  wander amplitude:45 speed: 20 ;
			set goal value: location;
		}
		aspect default {
			draw shape: circle color: 'red' size: 10;
			draw shape: circle color: 'orange' size: 40 empty: true;
		}
	}
	species boids skills: [moving] {
		var speed type: float max: maximal_speed init: maximal_speed;
		var range type: float init: minimal_distance * 2;
		var heading type: int max: heading + maximal_turn min: heading - maximal_turn;
		var velocity type: point init: {0,0};
		var others type: list value: ((self neighbours_at range) of_species boids) - self;
		const size type: int init: 5;
		action others_at type: list of: boids {
			arg distance type: float;
			return value: others where ((self distance_to each) < distance);
		}
		reflex separation when: apply_separation {
			let acc value: {0,0};
	
			loop boid over: (self others_at [distance:: minimal_distance]) of_species boids  {
				set acc value: acc - ((location of boid) - my location);
			}
			set velocity value: velocity + acc;
		}
		action compute_mass_center type: point {
			return value: (length(others) > 0) ? mean (others collect (each.location)) as point : location;
		}
		reflex alignment when: apply_alignment {
			let acc value: (mean (others collect (each.velocity)) as point) - velocity;
			set velocity value: velocity + (acc / alignment_factor);
		}
		reflex cohesion when: apply_cohesion {
			let acc value: ((self compute_mass_center []) as point) - location;
			set acc value: acc / cohesion_factor;
			set velocity value: velocity + acc;
		}
		reflex avoid when: apply_avoid {
			let acc value: {0,0};

			loop obs over: (self others_at [distance::(minimal_distance * 2)]) of_species obstacle  {
				set acc value: acc - ((location of obs) - my location);
			}
			set velocity value: velocity + acc;
		}
		action bounding {
			if condition: !torus_environment {
				if condition: (location.x) < xmin {
					set velocity value: velocity + {bounds,0};
				}
				if condition: (location.x) > xmax {
					set velocity value: velocity - {bounds,0};
				}
				if condition: (location.y) < ymin {
					set velocity value: velocity + {0,bounds};
				}
				if condition: (location.y) > ymax {
					set velocity value: velocity - {0,bounds};
				}
			}
		}
		reflex follow_goal when: apply_goal {
			set velocity value: velocity + ((goal - location) / cohesion_factor);
		}
		reflex wind when: apply_wind {
			set velocity value: velocity + wind_vector;
		}
		action do_move {
			if ((velocity.x) as int = 0) and ((velocity.y) as int = 0) {
				set velocity value: {(rnd(4)) -2, (rnd(4)) - 2};
			}
			let oldLocation <- location;
			do goto target: location + velocity;
			set velocity <- (location - oldLocation);
			}
			

		
		reflex movement {
			do  bounding;
			do  do_move;
		}
		aspect image {
			draw image: images at (rnd(2)) size: 35 rotate: heading color: 'black';
		}
		aspect default {
			draw shape: triangle size: 7 rotate: heading color: 'yellow';
		}
		
		
		
	}
	species obstacle skills: [moving] {
		var speed type: float init: 0.1;
		reflex when: moving_obstacles {
			if condition: flip(0.5) 
				{do goto target: one_of(boids) as list;}
			else 
				{do wander amplitude: 360;}
		}
		aspect default {
			draw shape: triangle color: rgb('yellow') size: 10;
		}
	}
	
output {
	inspect name: 'Inspector' type: agent;
	display Sky refresh_every: 1 {
				quadtree qt;
	
		species boids;
		species boids_goal;
		species obstacle;
}}}

