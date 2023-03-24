/**
* Name: Boids
* Author: 
* Description: This model shows the movement of boids following a goal, and creating a flock. 
* The experiment the boids in 3D and allows users to move the goal.
* Tags: gui, skill
*/

model boids 
global torus: torus_environment{ 
	//Number of boids that will be created
	int number_of_agents <- 50 min: 1 max: 500;
	//Number of obstacles for the boids movement to represent
	int number_of_obstacles <- 0 min: 0;
	//Maximal speed of the boids
	float maximal_speed <- 15.0 min: 0.1 max: 15.0;
	//Factors for the group of boids
	int cohesion_factor <- 200;
	int alignment_factor <- 100; 
	//Variables for the movement of the boids
	float minimal_distance <- 30.0; 
	
	int width_and_height_of_environment <- 1000;  
	bool torus_environment <- false; 
	bool apply_cohesion <- true ;
	bool apply_alignment <- true ;
	bool apply_separation <- true;
	bool apply_avoid <- true;  
	bool apply_wind <- true;   
	bool moving_obstacles <- false;   
	int bounds <- int(width_and_height_of_environment / 20); 
	//Vector for the wind
	point wind_vector <- {0,0}; 
	list<image_file> images  <- [image_file('../images/bird1.png'),image_file('../images/bird2.png'),image_file('../images/bird3.png')]; 
	int xmin <- bounds;   
	int ymin <- bounds;  
	int xmax <- (width_and_height_of_environment - bounds);     
	int ymax <- (width_and_height_of_environment - bounds);   
	
	//Action to move the goal to the mouse location
	action move_goal {
		ask first(boids_goal) {
			do goto target: #user_location speed: 30.0;
		}
	}
	
	geometry shape <- square(width_and_height_of_environment);
	
	init { 
		//Create the boids agents
		create boids number: number_of_agents { 
			 location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		} 
		//Create the obstacles agents
		create obstacle number: number_of_obstacles {
			location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 }; 
		}
		//Create the goal that boids will follow
		create  boids_goal;	
	}	
}

//Species boids goal which represents the goal that will be followed by boids agents using the skill moving
species boids_goal skills: [moving] {
	float range  <- 20.0;
	
	//If the mouse is not used, then the goal just wander
	reflex wander {  
		do  wander amplitude: 45.0 speed: 20.0;  
	}
	
	aspect default {
		draw circle(10) color: #red ;
		draw circle(40) color: #orange wireframe: true;
	}
} 
//Species boids which represents the boids agents whom follow the boid goal agents, using the skill moving
species boids skills: [moving] {
	//Speed of the boids agents
	float speed max: maximal_speed <- maximal_speed;
	//Range used to consider the group of the agent
	float range <- minimal_distance * 2;
	point velocity <- {0,0};
		
	//Reflex used when the separation is applied to change the velocity of the boid
	reflex separation when: apply_separation {
		point acc <- {0,0};
		ask (boids overlapping (circle(minimal_distance)))  {
			acc <- acc - ((location) - myself.location);
		}  
		velocity <- velocity + acc;
	}
	
	//Reflex to align the boid with the other boids in the range
	reflex alignment when: apply_alignment {
		list others  <- ((boids overlapping (circle (range)))  - self);
		point acc <- mean (others collect (each.velocity)) - velocity;
		velocity <- velocity + (acc / alignment_factor);
	}
	 
	//Reflex to apply the cohesion of the boids group in the range of the agent
	reflex cohesion when: apply_cohesion {
		list others <- ((boids overlapping (circle (range)))  - self);
		point mass_center <- (length(others) > 0) ? mean (others collect (each.location)) : location;

		point acc <- mass_center - location;
		acc <- acc / cohesion_factor; 
		velocity <- velocity + acc;   
	}
	
	//Reflex to avoid the obstacles
	reflex avoid when: apply_avoid { 
		point acc <- {0,0};
		list<obstacle> nearby_obstacles <- (obstacle overlapping (circle (range)) );
		loop obs over: nearby_obstacles {
			acc <- acc - ((location of obs) - my (location));
		}
		velocity <- velocity + acc; 
	}
	
	//action to represent the bounding of the environment considering the velocity of the boid
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
		} else {
			if (location.x) < 0.0 {
				location <- {width_and_height_of_environment + location.x,location.y};
			} else if (location.x) > width_and_height_of_environment {
				location <- {location.x - width_and_height_of_environment ,location.y};
			}
			
			if (location.y) < 0.0 {
				location <- {location.x, width_and_height_of_environment + location.y};
			} else if (location.y) > width_and_height_of_environment {
				location <- {location.x,location.y - width_and_height_of_environment};
			}
			
		}
	}
	//Reflex to follow the goal 
	reflex follow_goal {
		velocity <- velocity + ((first(boids_goal).location - location) / cohesion_factor);
	}
	//Reflex to apply the wind vector on the velocity
	reflex wind when: apply_wind {
		velocity <- velocity + wind_vector;
	}
	
	//Action to move the agent  
	action do_move {  
		if (((velocity.x) as int) = 0) and (((velocity.y) as int) = 0) {
			velocity <- {(rnd(4)) -2, (rnd(4)) - 2};
		}
		point old_location <- copy(location);
		do goto target: location + velocity;
		velocity <- location - old_location;
	}
	
	//Reflex to apply the movement by calling the do_move action
	reflex movement {
		do do_move;
		do bounding;
	}
	
	aspect image {
		draw (images at (rnd(2))) size: {50,50} rotate: heading ;      
	}
	aspect circle { 
		draw circle(15)  color: #red;
	}
	
	aspect default { 
		draw circle(20) color: #lightblue wireframe: true;
	}
} 

//Species obstacle that represents the obstacles avoided by the boids agents using the skill moving
species obstacle skills: [moving] {
	float speed <- 2.0;

	init {
		shape <- triangle(15);
	}	
	//Reflex to move the obstacles if it is available
	reflex move_obstacles when: moving_obstacles {
		//Will make the agent go to a boid with a 50% probability
		if flip(0.5)  
		{ 
			do goto target: one_of(boids);
		} 
		else{ 
			do wander amplitude: 360.0;   
		}
	}
	aspect default {
		draw  triangle(20) color: #black ;
	}

}


experiment "Basic" type: gui {
	parameter 'Number of agents' var: number_of_agents;
	parameter 'Number of obstacles' var: number_of_obstacles;
	parameter 'Maximal speed' var: maximal_speed;
	parameter 'Cohesion Factor' var: cohesion_factor;
	parameter 'Alignment Factor' var: alignment_factor; 
	parameter 'Minimal Distance'  var: minimal_distance; 
	parameter 'Width/Height of the Environment' var: width_and_height_of_environment ;  
	parameter 'Toroidal Environment ?'  var: torus_environment ; 
	parameter 'Apply Cohesion ?' var: apply_cohesion ;
	parameter 'Apply Alignment ?' var: apply_alignment ;   
	parameter 'Apply Separation ?' var: apply_separation ;   
	parameter 'Apply Avoidance ?' var: apply_avoid ;   
	parameter 'Apply Wind ?' var: apply_wind ;     
	parameter 'Moving Obstacles ?' var: moving_obstacles  ;    
	parameter 'Direction of the wind' var: wind_vector ;  
	
	//Minimum duration of a step to better see the movements
	float minimum_cycle_duration <- 0.01;

	output synchronized: true {
		display Sky type: 3d axes:false{ 
			image '../images/sky.jpg' refresh: false;
			species boids aspect: image;
			species boids_goal;
			species obstacle;
		}

	}
}


experiment "Interactive" type: gui autorun: true{
	parameter 'Number of agents' var: number_of_agents;
	parameter 'Number of obstacles' var: number_of_obstacles;
	parameter 'Maximal speed' var: maximal_speed;
	parameter 'Cohesion Factor' var: cohesion_factor;
	parameter 'Alignment Factor' var: alignment_factor; 
	parameter 'Minimal Distance'  var: minimal_distance; 
	parameter 'Width/Height of the Environment' var: width_and_height_of_environment ;  
	parameter 'Toroidal Environment ?'  var: torus_environment ; 
	parameter 'Apply Cohesion ?' var: apply_cohesion ;
	parameter 'Apply Alignment ?' var: apply_alignment ;   
	parameter 'Apply Separation ?' var: apply_separation ;   
	parameter 'Apply Avoidance ?' var: apply_avoid ;   
	parameter 'Apply Wind ?' var: apply_wind ;     
	parameter 'Moving Obstacles ?' var: moving_obstacles  ;    
	parameter 'Direction of the wind' var: wind_vector ;  
	bool previous_state <- gama.pref_synchronize_quadtree;
	
	init {
		// The preference is explicitly set so as to avoid concurrency problems 
		gama.pref_synchronize_quadtree <- true;
	}
	
	abort {
		gama.pref_synchronize_quadtree <- previous_state;
	}
	
	//Minimum duration of a step to better see the movements
	float minimum_cycle_duration <- 0.01;

	output synchronized: true {
		display Sky  background: #blue type: 3d fullscreen: 0 toolbar: false axes:false{ 
			image '../images/sky.jpg' refresh: false;
			species boids aspect: image trace: 10 fading: true ;
			species boids_goal;
			species obstacle;
			//Event to call the action move_goal in global if the mouse move within the experiment
			event #mouse_move action: move_goal;
		}

	}
}


