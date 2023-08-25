/**
* Name: sugarscape
* Author: 
* Description: A model with animal moving on a grid to find sugar. The animal agents 
* 	have a life duration and die if it is reached or if they don't have anymore sugar.
* Tags: grid
*/
model sugarscape

  
global {
	// Parameters 
	
	//Growth rate of the sugar
	int sugarGrowthRate <- 1;
	//Minimum age of death
	int minDeathAge <- 60;
	//Maximum age of death
	int maxDeathAge <- 100;
	//Metabolism maximum
	int maxMetabolism <- 3;
	
	//Maximum and Minimum initial sugar
	int maxInitialSugar <- 25 ;
	int minInitialSugar <- 5;
	//Maximum range
	int maxRange <- 6;
	bool replace <- true;
	//Number of agents
	int numberOfAgents <- 400;	
	
	// Environment
	geometry shape <- rectangle(50, 50);
		
	file<int> types  <- file<int>('../images/sugarscape.pgm');
	rgb FFFFAA const: true <- rgb('#FFFFAA');
	rgb FFFF55 const: true <- rgb('#FFFF55');
	rgb dark_yellow const: true <- rgb('#EEB422');
	rgb less_red const: true <- rgb('#FF5F5F');
	
	init {
		
		//Create the animal
		create animal number: numberOfAgents;
		
		//Ask to each sugar cell to set its sugar
		ask sugar_cell {
			maxSugar <- (types at {grid_x,grid_y});
			sugar <- maxSugar;
			color <- [#white,FFFFAA,FFFF55,#yellow,dark_yellow] at sugar;
		}
	}
}

	//Grid species representing the sugar cells
	grid sugar_cell width: 50 height: 50 neighbors: 4 use_individual_shapes: false use_regular_agents: false{ 
		//Maximum sugar
		int maxSugar;
		//Sugar contained in thecell
		int sugar update: sugar + sugarGrowthRate max: maxSugar;
		rgb color update: [#white,FFFFAA,FFFF55,#yellow,dark_yellow] at sugar;
		map<int,list<sugar_cell>> neighbours;
		
		//Initialization of the neighbours
		init {
			loop i from: 1 to: maxRange {
				neighbours[i] <- self neighbors_at i; 
			}
		}
	}	
	
//Species animal representing the animal agents
species animal {
	//Color of the animal
	rgb color  <- #red;
	//Speed of the animal
	float speed  <- 1.0;
	//Metabolism of the animal
	int metabolism  min: 1 <- rnd(maxMetabolism);
	//Perception range of the animal
	int vision  min: 1 <- rnd(maxRange);
	//Maximal age of the animal
	int maxAge  min: minDeathAge max: maxDeathAge <- rnd (maxDeathAge - minDeathAge) + minDeathAge;
	//Size of the animal
	float size  <- 0.5;
	//Sugar of the animal
	int sugar min: 0 <- (rnd (maxInitialSugar - minInitialSugar)) + minInitialSugar update: sugar - metabolism;
	//Age of the animal
	int age max: maxAge <- 0 update: int(age + step);
	//Place of the animal
	sugar_cell place ; 
	
	//Launched at the initialization of the animal agent
	init {
		//Set the place as one of the sugar cell
		place <- one_of(sugar_cell);
		location <- place.location;
	}
	//Move the agent to another place and collect the sugar of the previous place
	reflex basic_move { 
		sugar <- sugar + place.sugar;
		place.sugar <- 0;
		list<sugar_cell> neighbours <- place.neighbours[vision];
		list<sugar_cell> poss_targets <- (neighbours) where (each.sugar > 0);
		//If no sugar is found in the neighbours cells, move randomly
		place <- empty(poss_targets) ? one_of (neighbours) : one_of (poss_targets);
		location <- place.location;
	}
	//Reflex to kill the animal once it reaches its maximal age or it doesn't have sugar anymore
	reflex end_of_life when: (sugar = 0) or (age = maxAge) {
		if replace {
			create animal ;
		}
		do die;
	}
	aspect default {
		draw circle(0.5) color: #red;
	}
}

experiment sugarscape type: gui{
	float minimum_cycle_duration<-0.1;
	parameter 'Growth rate of sugar:' var: sugarGrowthRate category: 'Environment';
	parameter 'Minimum age of death:' var: minDeathAge <- 60 category: 'Agents';
	parameter 'Maximum age of death:' var: maxDeathAge <- 100 category: 'Agents';
	parameter 'Maximum metabolism:' var: maxMetabolism <- 3 category: 'Agents';
	parameter 'Maximum initial sugar per cell:'  var: maxInitialSugar <- 25 category: 'Environment';
	parameter 'Minimum initial sugar per cell:' var: minInitialSugar <- 5 category: 'Environment';
	parameter 'Maximum range of vision:' var: maxRange <- 6 category: 'Agents';
	parameter 'Replace dead agents ?' var: replace <- true category: 'Agents';
	parameter 'Number of agents:' var: numberOfAgents <- 400 category: 'Agents';
	
	output {
		display grille  type:2d antialias:false{
			grid sugar_cell;
			species animal;
		}
		display chart refresh: every(5#cycles)  type: 2d {
			chart 'Energy' type: pie background: #white style: exploded label_background_color:#white{
				data "strong" value: (animal as list) count (each.sugar > 8) color: #green;
				data "weak" value: (animal as list) count (each.sugar < 9) color: #red;
			}
		}
		display chart2 refresh: every(5#cycles) type: 2d  {
			chart 'Energy' type: histogram background: #white label_background_color:#white{
				data "strong" value: (animal as list) count (each.sugar > 8)  color: #green;
				data "weak" value: (animal as list) count (each.sugar < 9)  color: #red;
			}
		}
	}
}
