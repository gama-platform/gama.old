/**
* Name: Ant Foraging (Simple)
* Author: 
* Description: Toy Model ant using the question of how ants search food and use pheromons to return to their 
* nest once they did find food. This model is the simple one.
* Tags: gui, skill, grid, batch, diffusion
*/
model ants

global {
	int t <- 1;
	//Evaporation value per cycle of the pheromons
	float evaporation_per_cycle <- 5.0 min: 0.01 max: 240.0 ;
	//Diffusion rate of the pheromons
	float diffusion_rate const: true <- 1.0 min: 0.0 max: 1.0 ;
	//Size of the grid
	int gridsize const: true <- 75; 
	//Number of ants to create
	int ants_number  <- 50 min: 1 max: 200 parameter: 'Number of Ants:';
	//Variable to keep information about the food remaining
	int food_remaining update: list ( ant_grid ) count ( each . food > 0) <- 10;
	//Center of the grid that will be considered as the nest of ants
	point center const: true <- { round ( gridsize / 2 ) , round ( gridsize / 2 ) };
	matrix<int> types <- matrix<int> (pgm_file ( '../images/environment75x75_scarce.pgm' )); 
	
	geometry shape <- square(gridsize);
	
	init {
		//Creation of the ants placed in the nest
		create ant number: ants_number with: [ location :: center ];
	} 
	
	//Different actions triggered by an user interaction
	action press 
	{
		point loc <- #user_location;
		list<ant> selected_agents <- ant overlapping (circle(10) at_location #user_location);
		write("press " + loc.x + " " + loc.y + " "+selected_agents);
	}
	action release 
	{
		write("release");
	}
	action click  
	{
		write("click");
	}
	action click2   
	{
		write("click2");
	}
	//Reflex to diffuse the pheromons among the grid
	reflex diffuse {
      diffuse var:road on:ant_grid proportion: diffusion_rate radius:2 propagation: gradient;
   }

} 
//Grid used to discretize space to place food in cells
grid ant_grid width: gridsize height: gridsize neighbors: 8 {
	bool isNestLocation  <- ( self distance_to center ) < 4;
	bool isFoodLocation <-  types[grid_x , grid_y] = 2;       
	list<ant_grid> neighbours <- self neighbors_at 1;  
	float road <- 0.0 max:240.0 update: (road<=evaporation_per_cycle) ? 0.0 : road-evaporation_per_cycle;
	rgb color <- rgb([ self.road > 15 ? 255 : ( isNestLocation ? 125 : 0 ) , self.road * 30 , self.road > 15 ? 255 : food * 50 ]) update: rgb([ self.road > 15 ? 255 : ( isNestLocation ? 125 : 0 ) ,self.road * 30 , self.road > 15 ? 255 : food * 50 ]); 
	int food <- isFoodLocation ? 5 : 0; 
	int nest const: true <- int(300 - ( self distance_to center ));
}

//Species ant that will move
species ant skills: [ moving ] {     
	rgb color <- #red;
	ant_grid place function: {ant_grid ( location )};
	bool hasFood <- false; 
	bool hasRoad <- false update: place . road > 0.05;
	
	//Reflex to diffuse pheromon on the cell once the agent has food
	reflex diffuse_road when:hasFood=true{
      ant_grid(location).road <- ant_grid(location).road + 100.0;
   }
	//Reflex to wander while the ant has no food
	reflex wandering when: ( ! hasFood ) and ( ! hasRoad ) and ( place . food = 0) {
		do wander amplitude: 120 speed: 1.0;
	}
	//Reflex to search food when the agent has no food nor pheromon road close
	reflex looking when: ( ! hasFood ) and ( hasRoad ) and ( place . food = 0 ) { 
		list<ant_grid> list_places <- place . neighbours;
		ant_grid goal <- list_places first_with ( each . food > 0 );
		if goal != nil {
			location <- goal.location ; 
		} else {
			int min_nest <- ( list_places min_of ( each . nest ) );
			list_places <- list_places sort ( ( each . nest = min_nest ) ? each . road : 0.0 ) ;
			location <- point ( last ( list_places ) ) ;
		}
	}
	//Reflex to take food
	reflex taking when: ( ! hasFood ) and ( place . food > 0 ) { 
		hasFood <- true ;
		place . food <- place . food - 1 ;
	}
	//Reflex to make the ant return to the nest once it has food
	reflex homing when: ( hasFood ) and ( ! place . isNestLocation ) {
		do goto target:center  speed:1.0;
	}
	//Reflex to drop food once the ant arrived at the nest
	reflex dropping when: ( hasFood ) and ( place . isNestLocation ) {
		hasFood <- false ;
		heading <- heading - 180 ;
	}
	aspect default {
		draw circle(2.0) color: color;
	}
	
}
//Experiment simple to display ant and have user interaction
experiment Simple type:gui {
	parameter 'Evaporation:' var: evaporation_per_cycle;
	parameter 'Diffusion Rate:' var: diffusion_rate;
	output { 
		display Ants refresh: every(2#cycles) { 
			grid ant_grid;
			species ant aspect: default;
			graphics 'displayText' {
				draw string ( food_remaining ) size: 24.0 at: { 20 , 20 } color: rgb ( 'white' );
			}
			//Event triggering the action passed in parameter
			event mouse_down action:press;
			event mouse_up action:release;
		}  
		display Ants_2 refresh: every(2#cycles) { 
			grid ant_grid;
			graphics 'displayText' {
				draw string ( food_remaining ) size: 24.0 at: { 20 , 20 } color: rgb ( 'white' );
			}
			event mouse_down action:press;
			event mouse_up action:click2;
		}  
	}
}

// This experiment explores two parameters with an exhaustive strategy, 
// repeating each simulation two times, in order to find the best combination 
// of parameters to minimize the time taken by ants to gather all the food
experiment 'Exhaustive optimization' type: batch repeat: 2 keep_seed: true until: ( food_remaining = 0 ) or ( time > 400 ) {
	parameter 'Evaporation' var: evaporation_per_cycle among: [ 0.1 , 1.0 , 2.0 , 5.0 ,  10.0 ];
	parameter 'Diffusion rate' var: diffusion_rate min: 0.1 max: 1.0 step:
	0.3;
	method exhaustive minimize: time;
}

// This experiment simply explores two parameters with an exhaustive strategy, 
// repeating each simulation two times
experiment Repeated type: batch repeat: 2 keep_seed: true until: (
food_remaining = 0 ) or ( time > 400 ) {
	parameter 'Evaporation' var: evaporation_per_cycle among: [ 0.1 , 1.0 , 2.0 , 5.0 ,  10.0 ];
	parameter 'Diffusion rate' var: diffusion_rate min: 0.1 max: 1.0 step:0.3;
}

// This experiment explores two parameters with a GA strategy, 
// repeating each simulation two times, in order to find the best combination 
// of parameters to minimize the time taken by ants to gather all the food 
experiment Genetic type: batch keep_seed: true repeat: 3 until: ( food_remaining
= 0 ) or ( time > 400 ) {
	parameter 'Evaporation' var: evaporation_per_cycle min: 0.05 max: 10.0
	step: 0.1;
	parameter 'Diffusion rate' var: diffusion_rate min: 0.0 max: 1.0 step:
	0.01;
	method genetic pop_dim: 5 crossover_prob: 0.7 mutation_prob: 0.1
	nb_prelim_gen: 1 max_gen: 20 minimize: time;
}
