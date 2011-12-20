model life
// gen by Xml2Gaml


global {
	var environment_width type: int init: 200 min: 10 max: 400 parameter: 'Width:' category: 'Board' ;
	var environment_height type: int init: 200 min: 10 max: 400 parameter: 'Height:' category: 'Board' ;
	var torus_environment type: bool init: true parameter: 'Torus?:' category: 'Board' ;
	var density type: int init: 20 min: 1 max: 99 parameter: 'Initial density of live cells:' category: 'Cells' ;
	var living_conditions type: list init: [2,3] parameter: 'Number of live neighbours required for each cell to stay alive:' category: 'Cells' ;
	var birth_conditions type: list init: [3] parameter: 'Number of live neighbours required for each celle to become alive:' category: 'Cells' ;
	const black type: rgb init: rgb('black') ;
	const white type: rgb init: rgb('white') ;
	
	reflex main {
		ask target: list (life_cell) {
			do action: evolve;
		}
		ask target: list (life_cell) {
			do action: update;
		}
	}
}
environment width: environment_width height: environment_height torus: torus_environment {
	grid life_cell width: environment_width height: environment_height neighbours: 8 torus: torus_environment {
		var new_state type: bool;
		var state type: bool init: (rnd(100)) < density;
		var color type: rgb value: state ? black : white ;
		
		action evolve {
			let living type: int value: ((self neighbours_at 1) of_species life_cell) count each.state ;
			set new_state value: state ? living in living_conditions : living in birth_conditions ;
		}
		action update {
			set state value: new_state;
		}
	}
}
output {
	display Life {
		grid life_cell ;
	}
}
