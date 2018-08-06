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
file ant_shape_empty const: true <- file('../icons/ant.png');
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
	bool is_nest <- (self distance_to center) < 4 ;
	bool is_food <- type = 2 ; 
	rgb color <- is_nest ? °sienna:((food > 0)? °brown : ((road < 0.001)? #darkgoldenrod: ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) update: is_nest ? °sienna:((food > 0)? °brown : ((road < 0.001)?#darkgoldenrod : ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) ;
	int food <- is_food ? 5 : 0 ;
	int nest const: true <- 300 - int(self distance_to center) ;
	
}
//Species ant that will move and follow a final state machine
species ant skills: [moving] control: fsm {	float speed <- 1.0;
	bool has_food <- false;

	//Reflex to place a pheromon stock in the cell
	reflex diffuse_road when: has_food = true {
		ant_grid(location).road <- ant_grid(location).road + 100.0;
	}
	//Action to pick food
	action pick (int amount) {
		has_food <- true;
		ant_grid place <- ant_grid(location);
		place.food <- place.food - amount;
	}
	//Action to drop food
	action drop {
		food_gathered <- food_gathered + 1;
		has_food <- false;
		heading <- heading - 180;
	}
	//Action to find the best place in the neighborhood cells
	point choose_best_place {
		container list_places <- ant_grid(location).neighbors;
		if (list_places count (each.food > 0)) > 0 {
			return point(list_places first_with (each.food > 0));
		} else {
			list_places <- (list_places where ((each.road > 0) and ((each distance_to center) > (self distance_to center)))) sort_by (each.road);
			return point(last(list_places));
		} 

	}
	//Reflex to drop food once the ant is in the nest
	reflex drop when: has_food and (ant_grid(location)).is_nest {
		do drop();
	}
	//Reflex to pick food when there is one at the same location
	reflex pick when: !has_food and (ant_grid(location)).food > 0 {
		do pick(1);
	}
	//Initial state to make the ant wander 
	state wandering initial: true {
		do wander(amplitude: 90.0);
		float pr <- (ant_grid(location)).road;
		transition to: carryingFood when: has_food;
		transition to: followingRoad when: (pr > 0.05) and (pr < 4);
	}
	//State to carry food once it has been found
	state carryingFood {
		do goto(target: center);
		transition to: wandering when: !has_food; 
	}
	//State to follow a pheromon road if once has been found
	state followingRoad {
		point next_place <- choose_best_place();
		float pr <- (ant_grid(location)).road;
		location <- next_place;
		transition to: carryingFood when: has_food;
		transition to: wandering when: (pr < 0.05) or (next_place = nil);
	}
	
		aspect info {
			draw ant_shape_empty size: {7, 5} rotate: my heading + 1; 
		draw circle(1) empty: !has_food color: #red;
		if (destination != nil) {
			draw line([location + {0, 0, 0.5}, {location.x + 5 * cos(heading), location.y + 5 * sin(heading)} + {0, 0, 0.5}]) + 0.1 color: #white border: false end_arrow: 1;
		}
		if (state != "wandering") {
		draw circle(4) empty: true color: #white;
		draw string(self as int) color: #white font: font("Helvetica", 14 , #bold) at: my location - {1, 1, -0.5};

		draw state color: #yellow font: font("Helvetica", 18, #bold) at: my location + {1, 1, 0.5} ;}
	}}

experiment Ant type: gui {
	parameter 'Number of ants:' var: ants_number category: 'Model' ;
	parameter 'Evaporation of the signal (unit/cycle):' var: evaporation_per_cycle category: 'Model' ;
	parameter 'Rate of diffusion of the signal (%/cycle):' var: diffusion_rate category: 'Model' ;
	parameter 'Use icons for the agents:' var: use_icons category: 'Display' ;
	parameter 'Display state of agents:' var: display_state category: 'Display' ;
	output {
		display Ants  type: opengl synchronized: true {
			image '../images/soil3.jpg' ;
			agents "Grid" transparency: 0.4  value: ant_grid where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant aspect: info ;
		}
	}
}


