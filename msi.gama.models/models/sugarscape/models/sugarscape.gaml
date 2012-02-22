model sugarscape


global {
	var sugarGrowthRate type: int init: 1 parameter: 'Growth rate of sugar:' category: 'Environment';
	var minDeathAge type: int init: 60 parameter: 'Minimum age of death:' category: 'Agents';
	var maxDeathAge type: int init: 100 parameter: 'Maximum age of death:' category: 'Agents';
	var maxMetabolism type: int init: 3 parameter: 'Maximum metabolism:' category: 'Agents';
	var maxInitialSugar type: int init: 25 parameter: 'Maximum initial sugar per cell:' category: 'Environment';
	var minInitialSugar type: int init: 5 parameter: 'Minimum initial sugar per cell:' category: 'Environment';
	var maxRange type: float init: 6 parameter: 'Maximum range of vision:' category: 'Agents';
	var replace type: bool init: true parameter: 'Replace dead agents ?' category: 'Agents';
	const red type: rgb init: rgb('red');
	const white type: rgb init: rgb('white');
	const FFFFAA type: rgb init: rgb('#FFFFAA');
	const FFFF55 type: rgb init: rgb('#FFFF55');
	const yellow type: rgb init: rgb('yellow');
	const dark_yellow type: rgb init: rgb('#EEB422');
	const pink type: rgb init: rgb('pink');
	const less_red type: rgb init: rgb('#FF5F5F');
	var numberOfAgents type: int init: 400 parameter: 'Number of agents:' category: 'Agents';
	const types type: file init: file('../images/sugarscape.pgm'); // as_matrix {50,50};
	init {
		create species: animal number: numberOfAgents;
		ask target: list(sugar_cell) {
			set maxSugar value: (types at {grid_x,grid_y});
		}
	}
}
environment width: 50 height: 50 {
	grid sugar_cell width: 50 height: 50 neighbours: 4 {
		const multiagent type: bool init: false;
		var maxSugar type: int;
		var sugar type: int init: maxSugar value: min ([maxSugar, sugar + sugarGrowthRate]);
		var color type: rgb value: [white,FFFFAA,FFFF55,yellow,dark_yellow] at sugar;
	}
}
entities {
	species animal {
		const color type: rgb init: red;
		const speed type: float init: 1;
		const metabolism type: int init: rnd(maxMetabolism) min: 1;
		var sugar type: int min: 0 init: (rnd (maxInitialSugar - minInitialSugar)) + minInitialSugar value: sugar - metabolism;
		const vision type: int init: rnd(maxRange) min: 1;
		const maxAge type: int min: minDeathAge max: maxDeathAge init: rnd (maxDeathAge - minDeathAge) + minDeathAge;
		var age type: int max: maxAge init: 0 value: age + step;
		var place type: sugar_cell init: location as sugar_cell; 
		const size type: float init: 0.5;
		
		reflex basic_move { 
			set sugar value: sugar + place.sugar;
			set place.sugar value: 0;
			let neighbours value: topology(sugar_cell) neighbours_of (place::vision) of_species sugar_cell;
			let poss_targets type: list of: sugar_cell value: (neighbours) where (each.sugar > 0);
			set place value: empty(poss_targets) ? one_of (neighbours) : one_of (poss_targets);
			set location value: place.location;
		}
		reflex end_of_life when: (sugar = 0) or (age = maxAge) {
			if condition: replace {
				create species: animal number: 1;
			}
			do action: die;
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
