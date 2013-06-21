model sugarscape

  
global {
	int sugarGrowthRate <- 1;
	int minDeathAge <- 60;
	int maxDeathAge <- 100;
	int maxMetabolism <- 3;
	int maxInitialSugar <- 25 ;
	int minInitialSugar <- 5;
	float maxRange <- 6.0;
	bool replace <- true;
	int numberOfAgents <- 400;
	const types type: file <- file('../images/sugarscape.pgm');
	const red type: rgb <- rgb('red');
	const white type: rgb <- rgb('white');
	const FFFFAA type: rgb <- rgb('#FFFFAA');
	const FFFF55 type: rgb <- rgb('#FFFF55');
	const yellow type: rgb <- rgb('yellow');
	const dark_yellow type: rgb <- rgb('#EEB422');
	const pink type: rgb <- rgb('pink');
	const less_red type: rgb <- rgb('#FF5F5F');
	
	init {
		create animal number: numberOfAgents;
		ask sugar_cell {
			maxSugar <- (types at {grid_x,grid_y});
			sugar <- maxSugar;
			color <- [white,FFFFAA,FFFF55,yellow,dark_yellow] at sugar;
		}
	}
}
environment width: 50 height: 50 {
	grid sugar_cell width: 50 height: 50 neighbours: 4 { 
		const multiagent type: bool <- false;
		int maxSugar;
		int sugar update: sugar + sugarGrowthRate max: maxSugar;
		rgb color update: [white,FFFFAA,FFFF55,yellow,dark_yellow] at sugar;
	}
}
entities {
	species animal {
		const color type: rgb <- red;
		const speed type: float <- 1.0;
		const metabolism type: int min: 1 <- rnd(maxMetabolism);
		const vision type: int min: 1 <- rnd(maxRange);
		const maxAge type: int min: minDeathAge max: maxDeathAge <- rnd (maxDeathAge - minDeathAge) + minDeathAge;
		const size type: float <- 0.5;
		int sugar min: 0 <- (rnd (maxInitialSugar - minInitialSugar)) + minInitialSugar update: sugar - metabolism;
		int age max: maxAge <- 0 update: int(age + step);
		sugar_cell place <- location as sugar_cell; 
		
		reflex basic_move { 
			sugar <- sugar + place.sugar;
			place.sugar <- 0;
			list<sugar_cell> neighbours <- topology(sugar_cell) neighbours_of (place::vision) of_species sugar_cell;
			list<sugar_cell> poss_targets <- (neighbours) where (each.sugar > 0);
			place <- empty(poss_targets) ? one_of (neighbours) : one_of (poss_targets);
			location <- place.location;
		}
		reflex end_of_life when: (sugar = 0) or (age = maxAge) {
			if replace {
				create animal ;
			}
			do die;
		}
		aspect default {
			draw circle(1) color: red;
		}
	}
}
experiment sugarscape type: gui{
	parameter 'Growth rate of sugar:' var: sugarGrowthRate category: 'Environment';
	parameter 'Minimum age of death:' var: minDeathAge <- 60 category: 'Agents';
	parameter 'Maximum age of death:' var: maxDeathAge <- 100 category: 'Agents';
	parameter 'Maximum metabolism:' var: maxMetabolism <- 3 category: 'Agents';
	parameter 'Maximum initial sugar per cell:'  var: maxInitialSugar <- 25 category: 'Environment';
	parameter 'Minimum initial sugar per cell:' var: minInitialSugar <- 5 category: 'Environment';
	parameter 'Maximum range of vision:' var: maxRange <- 6.0 category: 'Agents';
	parameter 'Replace dead agents ?' var: replace <- true category: 'Agents';
	parameter 'Number of agents:' var: numberOfAgents <- 400 category: 'Agents';
	
	output {
		display grille {
			grid sugar_cell;
			species animal;
		}
		display chart refresh_every: 5 {
			chart name: 'Energy' type: pie background: rgb('lightGray') style: exploded {
				data "strong" value: (animal as list) count (each.sugar > 8) color: rgb("green");
				data "weak" value: (animal as list) count (each.sugar < 9) color: rgb("red");
			}
		}
		display chart2 refresh_every: 5 {
			chart name: 'Energy' type: histogram background: rgb('lightGray') {
				data "strong" value: (animal as list) count (each.sugar > 8)  color: rgb("green");
				data "weak" value: (animal as list) count (each.sugar < 9)  color: rgb("red");
			}
		}
	}
}
