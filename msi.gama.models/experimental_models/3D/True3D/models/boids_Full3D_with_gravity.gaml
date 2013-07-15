model boids_3D 
global { 
	int number_of_agents parameter: 'Number of agents' <- 100 min: 1 max: 1000000;
	int number_of_obstacles parameter: 'Number of obstacles' <- 0 min: 0;
	float maximal_speed parameter: 'Maximal speed' <- 15.0 min: 0.1 max: 15.0;
	int cohesion_factor parameter: 'Cohesion Factor' <- 200;
	int alignment_factor parameter: 'Alignment Factor' <- 100; 
	float minimal_distance parameter: 'Minimal Distance' <- 10.0; 
	int maximal_turn parameter: 'Maximal Turn' <- 90 min: 0 max: 359; 
	int width_and_height_of_environment parameter: 'Width/Height of the Environment' <- 800;  
	int z_max parameter: 'Z max of the Environment' <- 800;  
	bool torus_environment parameter: 'Toroidal Environment ?' <- false; 
	bool apply_cohesion <- true parameter: 'Apply Cohesion ?';
	bool apply_alignment <- true parameter: 'Apply Alignment ?';   
	bool apply_separation <- true parameter: 'Apply Separation ?';   
	bool apply_goal <- true parameter: 'Follow Goal ?'; 
	bool apply_wind <- true parameter: 'Apply Wind ?';     
	int bounds <- int(width_and_height_of_environment / 20); 
	point wind_vector <- {0,0,0}  parameter: 'Direction of the wind';   
	int goal_duration <- 30 value: (goal_duration - 1); 
	point goal <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 ,(rnd(z_max - 2) + 1)}; 
	list images of: file <- [file('../images/bird1.png'),file('../images/bird2.png'),file('../images/bird3.png')]; 
	int xmin <- bounds;    
	int ymin <- bounds;  
	int xmax <- (width_and_height_of_environment - bounds);    
	int ymax <- (width_and_height_of_environment - bounds);   
	
	Physical3DWorld myWorld;
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 10;


	// flock's parameter 
	const two_boids_distance type: int init: 30;  
	const merging_distance type: int init: 30;
	var create_flock type: bool init: false;  
	
	init {
		write "" + wind_vector;
		create boids number: number_of_agents { 
			set location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 , (rnd(z_max - 2) + 1)};
		} 
		 
		create  boids_goal number: 1 {
			set location <- goal;
		}
		
	    create rain number: 1000{
			set radius <-2;
			set location <-  {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)} add_z  (rnd(width_and_height_of_environment));
			set mass <-0.001;
			set collisionBound <-  ["shape"::"sphere","radius"::radius];
		}
		
		create Physical3DWorld {
			set gravity <- true;
		}
		set myWorld <- first(Physical3DWorld as list);
		ask myWorld {set registeredAgents <-  (rain as list);}
	
	}
	
	//Update physical world
	reflex computeForces  {
			ask myWorld {do computeForces timeStep : 0.1;}
    } 
}

environment width: width_and_height_of_environment height: width_and_height_of_environment torus: torus_environment;

entities {
	
	
	species rain skills: [physical3D] {  
		rgb color;
		int radius;
		int size  <- size_of_agents;
		int range  <- range_of_agents; 
		float speed  <- speed_of_agents;  
		int heading <- rnd(359);

		geometry shape <- circle (10);// buffer(12);
				
		aspect sphere{
			draw geometry: geometry (point(self.location)) color: rgb('blue') depth:radius;
		}
		
	}
	
	species name: boids_goal skills: [moving] {
		const range type: float init: 20.0;
		const size type: float init: 10.0;
		
		reflex wander { 
			do  wander_3D amplitude: 45 speed: 20;  
			set goal value: location;
		}
		
		aspect default { 
			let shape_to_display type: geometry <- (shape + 10.0) add_z location.z;
			draw geometry: shape_to_display color: rgb('red') ;
		}
	} 
	
	
	species boids skills: [moving] {
		float speed max: maximal_speed <- maximal_speed;
		float range <- minimal_distance * 2;
		int heading max: heading + maximal_turn min: heading - maximal_turn;
		point velocity <- {0,0, 0} ;
		int size <- 5;
		
		list others update: ((boids at_distance range)  - self);
		
		point mass_center update:  (length(others) > 0) ? (mean (others collect (each.location)) )  : location;
		
		reflex separation when: apply_separation {
			let acc value: {0,0, 0};
			loop boid over: (boids overlapping (circle(minimal_distance)))  {
				set acc <- acc - ((location of boid) - location);
			}  
			set velocity <- velocity + acc;
		}
		
		reflex alignment when: apply_alignment {
			let acc <- (length(others) > 0) ? (mean (others collect (each.velocity))) : {0.0,0.0 , 0};
			set acc <- acc - velocity;
			set velocity <- velocity + (acc / alignment_factor);
		}
		 
		reflex cohesion when: apply_cohesion {
			let acc value: mass_center - location;
			set acc value: acc / cohesion_factor;
			set velocity value: velocity + acc; 
		}
		
		action bounding {
			if  !(torus_environment) {
				if  (location.x) < xmin {
					set velocity <- velocity + {bounds,0, 0.0};
				} else if (location.x) > xmax {
					set velocity <- velocity - {bounds,0, 0.0};
				}
				
				if (location.y) < ymin {
					set velocity <- velocity + {0,bounds, 0.0};
				} else if (location.y) > ymax {
					set velocity <- velocity - {0,bounds, 0.0};
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
			if ((velocity.x) as int = 0) and ((velocity.y) as int = 0) and ((velocity.z) as int = 0) {
				set velocity <- {(rnd(4)) -2, (rnd(4)) - 2,  ((rnd(4)) - 2)} ; 
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
			draw images at (rnd(2)) size: 35 rotate: heading color: rgb('black');      
		}
		
		aspect default { 
			draw triangle(15) rotate: 90 + heading color: rgb('yellow');
		}
		
		aspect sphere {
			draw geometry: geometry (point(self.location)) depth:10;
		}
	} 
}


experiment boids_3D type: gui {
	output {
		display Sky1 type:opengl refresh_every: 1 {
			image name:'background' file:'../images/ocean.jpg' ;
			species boids aspect: image transparency: 0.5;
			species boids_goal;
			species rain aspect:sphere;
		}
		

	}
}
