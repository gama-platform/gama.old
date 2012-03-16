model sugarscape

  
global {
	int sugarGrowthRate <- 1 parameter: 'Growth rate of sugar:' category: 'Environment';
	int minDeathAge <- 60 parameter: 'Minimum age of death:' category: 'Agents';
	int maxDeathAge <- 100 parameter: 'Maximum age of death:' category: 'Agents';
	int maxMetabolism <- 3 parameter: 'Maximum metabolism:' category: 'Agents';
	int maxInitialSugar <- 25 parameter: 'Maximum initial sugar per cell:' category: 'Environment';
	int minInitialSugar <- 5 parameter: 'Minimum initial sugar per cell:' category: 'Environment';
	float maxRange <- 6 parameter: 'Maximum range of vision:' category: 'Agents';
	bool replace <- true parameter: 'Replace dead agents ?' category: 'Agents';
	int numberOfAgents <- 400 parameter: 'Number of agents:' category: 'Agents';
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
		ask list(sugar_cell) {
			set maxSugar <- (types at {grid_x,grid_y});
			set sugar <- maxSugar;
			set color <- [white,FFFFAA,FFFF55,yellow,dark_yellow] at sugar;
		}
	}
}
environment width: 50 height: 50 {
	grid sugar_cell width: 50 height: 50 neighbours: 4 { 
		const multiagent type: bool <- false;
		int maxSugar;
		int sugar update: min ([maxSugar, sugar + sugarGrowthRate]);
		rgb color update: [white,FFFFAA,FFFF55,yellow,dark_yellow] at sugar;
	}
}
entities {
	species animal {
		const color type: rgb <- red;
		const speed type: float <- 1;
		const metabolism type: int min: 1 <- rnd(maxMetabolism);
		const vision type: int min: 1 <- rnd(maxRange);
		const maxAge type: int min: minDeathAge max: maxDeathAge <- rnd (maxDeathAge - minDeathAge) + minDeathAge;
		const size type: float <- 0.5;
		int sugar min: 0 <- (rnd (maxInitialSugar - minInitialSugar)) + minInitialSugar update: sugar - metabolism;
		int age max: maxAge <- 0 update: age + step;
		sugar_cell place <- location as sugar_cell; 
		
		reflex basic_move { 
			set sugar <- sugar + place.sugar;
			set place.sugar <- 0;
			let neighbours type: list of: sugar_cell <- topology(sugar_cell) neighbours_of (place::vision) of_species sugar_cell;
			let poss_targets type: list of: sugar_cell <- (neighbours) where (each.sugar > 0);
			set place <- empty(poss_targets) ? one_of (neighbours) : one_of (poss_targets);
			set location <- place.location;
		}
		reflex end_of_life when: (sugar = 0) or (age = maxAge) {
			if replace {
				create animal number: 1;
			}
			do die;
		}
		aspect default {
			draw shape: circle color: red size: 1;
		}
	}
}
output {
	display grille {
		grid sugar_cell;
		species animal;
	}
	display chart refresh_every: 5 {
		chart name: 'Energy' type: pie background: rgb('lightGray') style: exploded {
			data strong value: (animal as list) count (each.sugar > 8);
			data weak value: (animal as list) count (each.sugar < 9);
		}
	}
	display chart2 refresh_every: 5 {
		chart name: 'Energy' type: histogram background: rgb('lightGray') {
			data strong value: (animal as list) count (each.sugar > 8);
			data weak value: (animal as list) count (each.sugar < 9);
		}
	}
}
