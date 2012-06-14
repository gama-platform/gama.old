model life

global {  
	int environment_width  <- 200 min: 10 max: 400;
	int environment_height <- 200 min: 10 max: 400;  
	bool torus_environment <- true;
	int density  <- 25 min: 1 max: 99; 
	list living_conditions <- [2,3];  
	list birth_conditions <- [3]; 
	const black type: rgb init: rgb('black') ;
	const white type: rgb init: rgb('white') ;
	
	reflex main {
		ask list (life_cell) {
			do evolve;
		}
		ask list (life_cell) {
			do update;
		}
	} 
}

environment width: environment_width height: environment_height {
	grid life_cell width: environment_width height: environment_height neighbours: 8 torus: torus_environment {
		bool new_state;
		bool state <- (rnd(100)) < density ;
		rgb color <- state ? black : white update: state ? black : white ;
		
		action evolve {
			let living type: int <- (self neighbours_at 1)count each.state ;
			set new_state <- state ? living in living_conditions : living in birth_conditions ;
		}
		action update {
			set state <- new_state;
		}	
	}
}
	
experiment life type: gui {
	parameter 'Width:' var: environment_width  category: 'Board' ;
	parameter 'Height:' var: environment_height category: 'Board' ;  
	parameter 'Torus?:' var: torus_environment category: 'Board' ;
	parameter 'Initial density of live cells:' var: density category: 'Cells' ; 
	parameter 'Numbers of live neighbours required to stay alive:' var: living_conditions category: 'Cells' ;  
	parameter 'Numbers of live neighbours required to become alive:' var: birth_conditions category: 'Cells' ; 
	
	output {
		display Life {
			grid life_cell ;
		}
	}
}

