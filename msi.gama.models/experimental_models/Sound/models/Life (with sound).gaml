model life_with_sound

global torus: torus_environment {
	int environment_width <- 200 min: 10 max: 1000;
	int environment_height <- 200 min: 10 max: 1000;
	bool torus_environment <- true;
	int density <- 25 min: 1 max: 99;
	list<int> living_conditions <- [2, 3];
	list<int> birth_conditions <- [3];
	rgb livingcolor <- rgb('white');
	rgb dyingcolor <- rgb('red');
	rgb emergingcolor <- rgb('orange');
	rgb deadcolor <- rgb('black');
	geometry shape <- rectangle(environment_width, environment_height);
	init {
		do description;
	}

	reflex generation {
		ask life_cell {
			do evolve;
		}

		ask life_cell {
			do update;
		}

	}

	reflex play_music when: (time = 1) {
//		start_sound source: '../includes/sounds/Life Song - Casting Crowns.mp3' repeat: true;
	}

	action description {
		write
		'Description. The Game of Life is a cellular automaton devised by the British mathematician John Horton Conway in 1970. It is the best-known example of a cellular automaton. The game is a zero-player game, meaning that its evolution is determined by its initial state, requiring no further input from humans. One interacts with the Game of Life by creating an initial configuration and observing how it evolves.  The universe of the Game of Life is an infinite two-dimensional orthogonal grid of square cells, each of which is in one of two possible states, live or dead. Every cell interacts with its eight neighbors, which are the cells that are directly horizontally, vertically, or diagonally adjacent. At each step in time, the following transitions occur: \\n\\t 1.Any live cell with fewer than two live neighbours dies, as if caused by underpopulation. \\n\\t 2.Any live cell with more than three live neighbours dies, as if by overcrowding. \\n\\t 3.Any live cell with two or three live neighbours lives on to the next generation. \\n\\t 4.Any dead cell with exactly three live neighbours becomes a live cell. The initial pattern constitutes the seed of the system. The first(generation) is created by applying the above rules simultaneously to every cell in the seed�births and deaths happen simultaneously, and the discrete moment at which this happens is sometimes called a tick (in other words, each generation is a pure function of the one before). The rules continue to be applied repeatedly to create further generations.';
	}

}

entities {
	grid life_cell width: environment_width height: environment_height neighbours: 8  use_individual_shapes: false use_regular_agents: false frequency: 0
	
	use_neighbours_cache: false {
		bool new_state;
		list<life_cell> neighbours <- self neighbours_at 1;
		bool state <- (rnd(100)) < density;
		rgb color <- state ? livingcolor : deadcolor;
		action evolve {
			int living <- neighbours count each.state;
			if state {
				new_state <- living in living_conditions;
				color <- new_state ? livingcolor : dyingcolor;
			} else {
				new_state <- living in birth_conditions;
				color <- new_state ? emergingcolor : deadcolor;
			}

		}

		action update {
			state <- new_state;
		}
		
	

	}

}

experiment life_with_sound type: gui {
	parameter 'Width:' var: environment_width category: 'Board';
	parameter 'Height:' var: environment_height category: 'Board';
	parameter 'Torus?:' var: torus_environment category: 'Board';
	parameter 'Initial density of live cells:' var: density category: 'Cells';
	parameter 'Numbers of live neighbours required to stay alive:' var: living_conditions category: 'Cells';
	parameter 'Numbers of live neighbours required to become alive:' var: birth_conditions category: 'Cells';
	parameter 'Color of live cells:' var: livingcolor category: 'Colors';
	parameter 'Color of dying cells:' var: dyingcolor category: 'Colors';
	parameter 'Color of emerging cells:' var: emergingcolor category: 'Colors';
	parameter 'Color of dead cells:' var: deadcolor category: 'Colors';
	output {
		display Life {
			grid life_cell;
		}

	}

}
