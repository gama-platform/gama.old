model boids 
global torus: torus_environment{ 
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
	int bounds <- int(width_and_height_of_environment / 20) depends_on: [width_and_height_of_environment]; 
	point wind_vector <- {0,0}; 
	int goal_duration <- 30 update: (goal_duration - 1); 
	point goal <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 }; 
	list images of: file <- [file('../images/bird1.png'),file('../images/bird2.png'),file('../images/bird3.png')]; 
	int xmin <- bounds depends_on: [bounds];    
	int ymin <- bounds depends_on: [bounds];  
	int xmax <- (width_and_height_of_environment - bounds);     
	int ymax <- (width_and_height_of_environment - bounds);   

	geometry shape <- square(width_and_height_of_environment);
	init { 
		create boids number: number_of_agents { 
			 location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		} 
				 
		create obstacle number: number_of_obstacles {
			location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 }; 
		}
		
		create  boids_goal {
			location <- goal;
		}	
	}	
}

entities {
	species name: boids_goal skills: [moving] {
		const range type: float <- 20.0;
		const size type: float <- 10.0;
		
		reflex wander {  
			do  wander amplitude: 45 speed: 20;  
			goal <- location;
		}
		
		aspect default {
			draw shape: circle(10) color: rgb('red') ;
			draw shape: circle(40) color: rgb('orange') empty: true;
		}
	} 
	
	species boids skills: [moving] {
		float speed max: maximal_speed <- maximal_speed;
		float range <- minimal_distance * 2;
		int heading ;
		point velocity <- {0,0};
		int size <- 5;
			
		reflex separation when: apply_separation {
			point acc <- {0,0};
			ask (boids overlapping (circle(minimal_distance)))  {
				acc <- acc - ((location) - myself.location);
			}  
			velocity <- velocity + acc;
		}
		
		reflex alignment when: apply_alignment {
			list others  <- ((boids overlapping (circle (range)))  - self);
			point acc <- mean (others collect (each.velocity)) - velocity;
			velocity <- velocity + (acc / alignment_factor);
		}
		 
		reflex cohesion when: apply_cohesion {
			list others <- ((boids overlapping (circle (range)))  - self);
			point mass_center <- (length(others) > 0) ? mean (others collect (each.location)) : location;

			point acc <- mass_center - location;
			acc <- acc / cohesion_factor; 
			velocity <- velocity + acc;   
		}
		
		reflex avoid when: apply_avoid { 
			point acc <- {0,0};
			list<obstacle> nearby_obstacles <- (obstacle overlapping (circle (range)) );
			loop obs over: nearby_obstacles {
				acc <- acc - ((location of obs) - my (location));
			}
			velocity <- velocity + acc; 
		}
		
		action bounding {
			if  !(torus_environment) {
				if  (location.x) < xmin {
					velocity <- velocity + {bounds,0};
				} else if (location.x) > xmax {
					velocity <- velocity - {bounds,0};
				}
				
				if (location.y) < ymin {
					velocity <- velocity + {0,bounds};
				} else if (location.y) > ymax {
					velocity <- velocity - {0,bounds};
				}
				
			}
		}
		
		reflex follow_goal when: apply_goal {
			velocity <- velocity + ((goal - location) / cohesion_factor);
		}
		
		reflex wind when: apply_wind {
			velocity <- velocity + wind_vector;
		}
		  
		action do_move {  
			if (((velocity.x) as int) = 0) and (((velocity.y) as int) = 0) {
				velocity <- {(rnd(4)) -2, (rnd(4)) - 2};
			}
			point old_location <- copy(location);
			do goto target: location + velocity;
			velocity <- location - old_location;
		}
		
		reflex movement {
			do do_move;
		}
		
		aspect image {
			draw (images at (rnd(2))) size: 35 rotate: heading color: rgb([0,0,rnd(200) + 55]);      
		}
		aspect circle { 
			draw circle(15) rotate: 90 + heading color: rgb('red');
		}
		
		aspect default { 
			draw triangle(15) rotate: 90 + heading color: rgb('yellow');
		}
	} 
	
	species obstacle skills: [moving] {
		float speed <- 2.0;
		geometry shape <- triangle(15);
		
		reflex move_obstacles when: moving_obstacles {
			if flip(0.5)  
			{ 
				do goto target: one_of(boids);
			} 
			else{ 
				do wander amplitude: 360;   
			}
		}
		aspect default {
			draw  triangle(20) color: rgb('yellow') ;
		}

		
		aspect geom {
			draw shape color: rgb('yellow');
		}
	}
}

experiment Boids2 type: gui {
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
		display Sky refresh_every: 1 {
			image 'background' file:'../images/sky.jpg';
			species boids aspect: image;
			species boids_goal;
			species obstacle;
		}
	}
}
