model life

global torus: torus_environment {  
	int environment_width  <- 200 min: 10 max: 1000;
	int environment_height <- 200 min: 10 max: 1000;  
	bool torus_environment <- true;
	int density  <- 25 min: 1 max: 99; 
	list<int> living_conditions <- [2,3];  
	list<int> birth_conditions <- [3]; 
	const black type: rgb init: rgb('black') ;
	const white type: rgb init: rgb('white') ;
	geometry shape <- rectangle( environment_width,environment_height);
	
	reflex main {
		ask life_cell {
			do evolve;
		}
		ask life_cell {
			do update;    
		}
	} 
}
entities {
	grid life_cell width: environment_width height: environment_height neighbours: 8 frequency: 0  use_regular_agents: false use_individual_shapes: false use_neighbours_cache: false {
		bool new_state;
		bool state <- (rnd(100)) < density ;
		rgb color <- state ? black : white ;
		list<life_cell> neighbours <- self neighbours_at 1;
		
		action evolve {
			int living  <- neighbours count each.state ;
			new_state <- state ? living in living_conditions : living in birth_conditions ;
		}
		action update {
			state <- new_state; 
			color <- state ? black : white;
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

