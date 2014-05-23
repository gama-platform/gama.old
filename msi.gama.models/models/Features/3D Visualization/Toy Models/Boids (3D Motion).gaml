model boids_3D 
global torus: torus_environment{ 
	int number_of_agents parameter: 'Number of agents' <- 100 min: 1 max: 1000000;
	int number_of_obstacles parameter: 'Number of obstacles' <- 0 min: 0;
	int boids_size parameter: 'Boids size' <- 50 min: 1;
	float maximal_speed parameter: 'Maximal speed' <- 15.0 min: 0.1 max: 15.0;
	int cohesion_factor parameter: 'Cohesion Factor' <- 100; 
	int alignment_factor parameter: 'Alignment Factor' <- 100; 
	float minimal_distance parameter: 'Minimal Distance' <- 10.0; 
	int maximal_turn parameter: 'Maximal Turn' <- 90 min: 0 max: 359; 
	int width_and_height_of_environment parameter: 'Width/Height of the Environment' <- 800;  
	int z_max parameter: 'Z max of the Environment' <- 400;  
	bool torus_environment parameter: 'Toroidal Environment ?' <- false; 
	bool apply_cohesion <- true parameter: 'Apply Cohesion ?';
	bool apply_alignment <- true parameter: 'Apply Alignment ?';   
	bool apply_separation <- true parameter: 'Apply Separation ?';   
	bool apply_goal <- true parameter: 'Follow Goal ?'; 
	bool apply_wind <- true parameter: 'Apply Wind ?';     
	point wind_vector <- {0,0,0}  parameter: 'Direction of the wind';   
	int goal_duration <- 30 update: (goal_duration - 1); 
	point goal <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 ,(rnd(z_max - 2) + 1)}; 
	list images of: file <- [file('images/bird1.png'),file('images/bird2.png'),file('images/bird3.png')]; 
	geometry shape <- square(width_and_height_of_environment);
	init {
		create boids number: number_of_agents { 
			location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 , (rnd(z_max - 2) + 1)};
		} 
		 
		create boids_goal {
			location <- goal;
		}
	}
}

entities {
	species boids_goal skills: [moving] {
		const range type: float init: 20.0;
		const size type: float init: 10.0;
		
		reflex wander { 
			do  wander_3D amplitude: 45 speed: 20; 
			if (location.z) < 0 {
				location <- {location.x,location.y,0};
			} else if (location.z) > z_max {
				location <- {location.x,location.y,z_max};
			}
			goal <- location;
		}
		
		aspect default { 
			draw sphere(size) color: rgb('red') ;
		}
	} 
	
	
	species boids skills: [moving] {
		float speed max: maximal_speed <- maximal_speed;
		float range <- minimal_distance * 2;
		point velocity <- {0,0, 0} ;
		int size <- 5;
		
		list others update: ((boids at_distance range)  - self);
		point mass_center update:  (length(others) > 0) ? (mean (others collect (each.location)) )  : location;
		
		reflex separation when: apply_separation {
			point acc <- {0,0,0};
			loop boid over: (boids at_distance (minimal_distance))  {
				acc <- acc - ((location of boid) - location);
			}  
			velocity <- velocity + acc;
		}
		
		reflex alignment when: apply_alignment {
			point acc <- (length(others) > 0) ? (mean (others collect (each.velocity))) : {0.0,0.0,0.0};
			acc <- acc - velocity;
			velocity <- velocity + (acc / alignment_factor);
		}
		 
		reflex cohesion when: apply_cohesion {
			point acc <- mass_center - location;
			acc <- acc / cohesion_factor;
			velocity <- velocity + acc; 
		}
		
		action bounding {
			if (location.z) < 0 {
				location <- {location.x,location.y,0};
			} else if (location.z) > z_max {
				location <- {location.x,location.y,z_max};
			}
		}
		
		reflex follow_goal when: apply_goal {
			velocity <- velocity + ((goal - location) / cohesion_factor);
		}
		
		reflex wind when: apply_wind {
			velocity <- velocity + wind_vector;
		}
		  
		action do_move {  
			if (((velocity.x) as int) = 0) and (((velocity.y) as int) = 0) and (((velocity.z) as int) = 0) {
				velocity <- {(rnd(4)) -2, (rnd(4)) - 2,  ((rnd(4)) - 2)} ; 
			}
			point old_location <- location;
			do goto target: location + velocity;
			velocity <- location - old_location;
		}
		
		reflex movement {
			do bounding;
			do do_move;
		}
		
		aspect sphere {
			draw sphere(10) color: rgb("green");
		}
		
		aspect image {
			draw (images at (rnd(2))) size: boids_size rotate: heading color: rgb('black') ;      
		}

	}  
}


experiment boids_3D type: gui {
	
	
	output {
		
		display Sky1 type:opengl refresh_every: 1 z_fighting:false{
			image 'background' file:'images/ocean.jpg' ;
			species boids aspect: image;
			species boids_goal;	
		}
		

	}
}
