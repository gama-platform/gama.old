/**
* Name: Ant Foraging (Classic)
* Author: 
* Description: Toy Model ant using the question of how ants search food and use pheromons to return to their 
* nest once they did find food. This model is considered as the classic one.
* Tags: gui, skill, grid, diffusion
*/
model ants

global {
	//Number of ants
	int ants_number <- 100 min: 1 max: 2000 ;
	//Evaporation value per cycle
	float evaporation_per_cycle <- 5.0 min: 0.0 max: 240.0 ;
	//Diffusion rate for the pheromon diffused among the grid
	float diffusion_rate <- 0.5 min: 0.0 max: 1.0 ;
	bool use_icons <- true ;
	bool display_state <- true;
	//Size of the grid
	int gridsize <- 75 ;
	//Center of the grid that will be used as a nest for the ants
	point center const: true <- { (gridsize / 2),  (gridsize / 2)} ;
	file types const: true <- (pgm_file('../images/environment75x75.pgm')) ;
	string ant_shape_empty const: true <- '../icons/ant.png' ;
	string ant_shape_full const: true <- '../icons/full_ant.png'  ;
	rgb C00CC00 const: true <- rgb('#00CC00') ;    
	rgb C009900 const: true <- rgb('#009900') ; 
	rgb C005500 const: true <- rgb('#005500') ; 
	int food_gathered <- 0 ;    
	geometry shape <- square(gridsize);
	init{  
		//Creation of the ants that will be placed randomly in the nest
		create ant number: ants_number with: [location::any_location_in (ant_grid(center))] ;
	}
	//Reflex to diffuse the pheromon among the grid
	reflex diffuse {
      diffuse var:road on:ant_grid proportion: diffusion_rate radius:2 propagation: gradient;
   }

}

//Grid that will be used to place the food in a discretized space
grid ant_grid width: gridsize height: gridsize neighbors: 8 use_regular_agents: false {
	list<ant_grid> neighbours <- self neighbors_at 1;
	float road <- 0.0 max:240.0 update: (road<=evaporation_per_cycle) ? 0.0 : road-evaporation_per_cycle;
	int type <- int(types at {grid_x,grid_y}) ;
	bool isNestLocation <- (self distance_to center) < 4 ;
	bool isFoodLocation <- type = 2 ; 
	rgb color <- isNestLocation ? °sienna:((food > 0)? °brown : ((road < 0.001)? #darkgoldenrod: ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) update: isNestLocation ? °sienna:((food > 0)? °brown : ((road < 0.001)?#darkgoldenrod : ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) ;
	int food <- isFoodLocation ? 5 : 0 ;
	int nest const: true <- 300 - int(self distance_to center) ;
	
}
//Species ant that will move and follow a final state machine
species ant skills: [moving] control: fsm {
	float speed <- 2.0 ;
	ant_grid place update: ant_grid (location ); 
	string im <- 'ant_shape_empty' ;
	bool hasFood <- false ;
	//Reflex to allow the diffusion of the road of pheromon by putting pheromon inside a cell
	reflex diffuse_road when:hasFood=true{
      ant_grid(location).road <- ant_grid(location).road + 100.0;
   }
   //Action to pick the food
	action pick {
		im <- ant_shape_full ;
		hasFood <- true ;
		place.food <- place.food - 1 ;
	}
	//Action to drop the food
	action drop {
		food_gathered <- food_gathered + 1 ;  
		hasFood <- false ;
		heading <- heading - 180 ;
	}
	//Action to find the best cell in the neighbourhood of the ant
	action choose_best_place type: ant_grid {  
		list<ant_grid> list_places <- place.neighbours ;
		if (list_places count (each.food > 0)) > 0  { 
			return (list_places first_with (each.food > 0)) ;
		} else {
				int min_nest  <-  (list_places min_of (each.nest)) ;
				list_places <- list_places sort ((each.nest = min_nest) ? each.road :  0.0) ;
				return last(list_places) ;
			}
	} 
	//initial state of the ant that will make it wander until it finds food or a road
	state wandering initial: true { 
		do wander amplitude:120 ;
		transition to: carryingFood when: place.food > 0 {
			
			 
			do pick ;
		}
		transition to: followingRoad when: place.road > 0.05 ; 
	}
	//State to carry food to the nest once it has been found
	state carryingFood {
		do goto target: center ;
		transition to: wandering when: place.isNestLocation { 
			do drop ;
		}
	}
	//State to follow a pheromon road once it has been found
	state followingRoad {
		location <- (self choose_best_place()) as point ;
		transition to: carryingFood when: place.food > 0 {
			do pick ;
		}
		transition to: wandering when: (place.road < 0.05) ;
	}
	aspect text {
		if use_icons {
			draw  hasFood ? file(ant_shape_full) : file(ant_shape_empty) rotate: heading at: location size: {8,5} ;
		} else {
			draw circle(1.0) empty: !hasFood color: rgb ('orange') ;
		}
		if display_state {
			draw state at: location + {-3,1.5} color: °white font: font("Helvetica", 14 * #zoom, #plain) perspective:true;
		}
	} 
	aspect default {
		draw circle(1.0) empty: !hasFood color: #orange ; 
	}           
}

experiment Ant type: gui {
	parameter 'Number of ants:' var: ants_number category: 'Model' ;
	parameter 'Evaporation of the signal (unit/cycle):' var: evaporation_per_cycle category: 'Model' ;
	parameter 'Rate of diffusion of the signal (%/cycle):' var: diffusion_rate category: 'Model' ;
	parameter 'Use icons for the agents:' var: use_icons category: 'Display' ;
	parameter 'Display state of agents:' var: display_state category: 'Display' ;
	output {
		display Ants type: opengl  {
			grid ant_grid ;
			species ant aspect: text ;
		}
	}
}


