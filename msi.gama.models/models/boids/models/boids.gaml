model boids 
global { 
	int number_of_agents <- 100 min: 1 max: 1000000;
	int number_of_obstacles <- 5 min: 0;
	float maximal_speed <- 15.0 min: 0.1 max: 15.0;
	int cohesion_factor <- 200;
	int alignment_factor <- 100; 
	float minimal_distance <- 10.0; 
	int maximal_turn <- 90 min: 0 max: 359; 
	int width_and_height_of_environment <- 800;  
	bool torus_environment <- false; 
	bool apply_cohesion <- true ;
	bool apply_alignment <- true ;
	bool apply_separation <- true;
	bool apply_goal <- true;
	bool apply_avoid <- true;  
	bool apply_wind <- true;   
	bool moving_obstacles <- false;   
	int bounds <- int(width_and_height_of_environment / 20); 
	point wind_vector <- {0,0}; 
	int goal_duration <- 30 update: (goal_duration - 1); 
	point goal <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 }; 
	list images of: string <- ['../images/bird1.png','../images/bird2.png','../images/bird3.png']; 
	int xmin <- bounds;    
	int ymin <- bounds;  
	int xmax <- (width_and_height_of_environment - bounds);    
	int ymax <- (width_and_height_of_environment - bounds);   

	init {
		create boids number: number_of_agents { 
			set location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		} 
		 
		create obstacle number: number_of_obstacles {
			set location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 }; 
		}
		
		create  boids_goal number: 1 {
			set location <- goal;
		}
	}	
}
	
environment width: width_and_height_of_environment height: width_and_height_of_environment torus: torus_environment;

entities {
	species name: boids_goal skills: [moving] {
		const range type: float init: 20.0;
		const size type: float init: 10.0;
		
		reflex wander { 
			do  wander amplitude: 45 speed: 20;  
			set goal value: location;
		}
		
		aspect default {
			draw shape: circle color: rgb('red') size: 10;
			draw shape: circle color: rgb('orange') size: 40 empty: true;
		}
	} 
	
	species boids skills: [moving] {
		float speed max: maximal_speed <- maximal_speed;
		float range <- minimal_distance * 2;
		int heading max: heading + maximal_turn min: heading - maximal_turn;
		point velocity <- {0,0};
		int size <- 5;
		
		list others update: ((boids overlapping (circle (range)))  - self);
		
		point mass_center update:  (length(others) > 0) ? (mean (others collect (each.location)) ) as point : location;
		
		reflex separation when: apply_separation {
			let acc value: {0,0};
			loop boid over: (boids overlapping (circle(minimal_distance)))  {
				set acc <- acc - ((location of boid) - location);
			}  
			set velocity <- velocity + acc;
		}
		
		reflex alignment when: apply_alignment {
			let toto <- others collect each;
			let acc <- (mean (others collect (each.velocity)) as point) - velocity;
			set velocity <- velocity + (acc / alignment_factor);
		}
		 
		reflex cohesion when: apply_cohesion {
			let acc value: mass_center - location;
			set acc value: acc / cohesion_factor;
			set velocity value: velocity + acc; 
		}
		
		reflex avoid when: apply_avoid {
			let acc <- {0,0};
			let nearby_obstacles <- (obstacle overlapping (circle (range)) );
			loop obs over: nearby_obstacles {
				set acc <- acc - ((location of obs) - my (location));
			}
			set velocity <- velocity + acc; 
		}
		
		action bounding {
			if  !(torus_environment) {
				if  (location.x) < xmin {
					set velocity <- velocity + {bounds,0};
				} else if (location.x) > xmax {
					set velocity <- velocity - {bounds,0};
				}
				
				if (location.y) < ymin {
					set velocity <- velocity + {0,bounds};
				} else if (location.y) > ymax {
					set velocity <- velocity - {0,bounds};
				}
				
			}
		}
		
		reflex follow_goal when: apply_goal {
			set velocity <- velocity + ((goal - location) / cohesion_factor);
		}
		
		reflex wind when: apply_wind {
			set velocity <- velocity + wind_vector;
		}
		  
		action do_move {  
			if ((velocity.x) as int = 0) and ((velocity.y) as int = 0) {
				set velocity <- {(rnd(4)) -2, (rnd(4)) - 2};
			}
			let old_location <- location;
			do goto target: location + velocity;
			set velocity <- location - old_location;
		}
		
		reflex movement {
			do bounding;
			do do_move;
		}
		
		aspect image {
			draw image: images at (rnd(2)) size: 35 rotate: heading color: rgb('black');      
		}
		
		aspect default { 
			draw shape: triangle  size: 15 rotate: 90 + heading color: rgb('yellow');
		}
	} 
	
	species obstacle skills: [moving] {
		float speed <- 2.0;
		  
		reflex toto when: moving_obstacles {
			if flip(0.5)  
			{ 
				do goto target: one_of(boids);
			} 
			else{ 
				do wander amplitude: 360;   
			}
		}
		aspect default {
			draw shape: triangle color: rgb('yellow') size: 20;
		}
	}
}

experiment boids type: gui {
	parameter 'Number of agents' var: number_of_agents;
	parameter 'Number of obstacles' var: number_of_obstacles;
	parameter 'Maximal speed' var: maximal_speed;
	parameter 'Cohesion Factor' var: cohesion_factor;
	parameter 'Alignment Factor' var: alignment_factor; 
	parameter 'Minimal Distance'  var: minimal_distance; 
	parameter 'Maximal Turn'  var: maximal_turn; 
	parameter 'Width/Height of the Environment' var: width_and_height_of_environment ;  
	parameter 'Toroidal Environment ?'  var: torus_environment ; 
	parameter 'Apply Cohesion ?' var: apply_cohesion ;
	parameter 'Apply Alignment ?' var: apply_alignment ;   
	parameter 'Apply Separation ?' var: apply_separation ;   
	parameter 'Follow Goal ?' var: apply_goal ; 
	parameter 'Apply Avoidance ?' var: apply_avoid ;   
	parameter 'Apply Wind ?' var: apply_wind ;     
	parameter 'Moving Obstacles ?' var: moving_obstacles  ;    
	parameter 'Direction of the wind' var: wind_vector ;  
	
	output {
		inspect name: 'Inspector' type: agent;
		display Sky refresh_every: 1 {
			image name:'background' file:'../images/sky.jpg';
			species boids aspect: image;
			species boids_goal;
			species obstacle;
		}
	}
}
