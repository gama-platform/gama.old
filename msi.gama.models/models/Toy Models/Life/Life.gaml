/**
* Name: Life
* Author: 
* Description: A model using a cellular automata to represent the Game of Life, the most famous 
* 	example of cellular automata. Each cell will see if the number of living neighbours meets the 
* 	condition to emerge or to live.
* Tags: grid
*/
model life

//Declare the world as a torus or not torus environment
global torus: torus_environment {
	//Size of the environment
	int environment_width <- 200 min: 10 max: 1000;
	int environment_height <- 200 min: 10 max: 1000;
	bool parallel <- true;
	//Declare as torus or not
	bool torus_environment <- true;
	//Density 
	int density <- 25 min: 1 max: 99;
	//Conditions to live
	list<int> living_conditions <- [2, 3];
	//Conditions to birth
	list<int> birth_conditions <- [3];
	//Color for living cells
	rgb livingcolor <- #white;
	//Color for dying cells
	rgb dyingcolor <- #red;
	//Color for emerging cells
	rgb emergingcolor <- #orange;
	//Color for dead cells
	rgb deadcolor <- #black;
	//Shape of the environment
	geometry shape <- rectangle(environment_width, environment_height);
	
	//Initialization of the model by writing the description of the model in the console
	init {
		do description;
	}
	
	//Ask at each life_cell to evolve and update
	reflex generation {
		// The computation is made in parallel
		ask life_cell parallel: parallel {
			do evolve;
		}
	}
	//Write the description of the model in the console
	action description {
		write
		'Description. The Game of Life is a cellular automaton devised by the British mathematician John Horton Conway in 1970. It is the best-known example of a cellular automaton. The game is a zero-player game, meaning that its evolution is determined by its initial state, requiring no further input from humans. One interacts with the Game of Life by creating an initial configuration and observing how it evolves.  The universe of the Game of Life is an infinite two-dimensional orthogonal grid of square cells, each of which is in one of two possible states, live or dead. Every cell interacts with its eight neighbors, which are the cells that are directly horizontally, vertically, or diagonally adjacent. At each step in time, the following transitions occur: \n\t 1.Any live cell with fewer than two live neighbours dies, as if caused by underpopulation. \n\t 2.Any live cell with more than three live neighbours dies, as if by overcrowding. \n\t 3.Any live cell with two or three live neighbours lives on to the next generation. \n\t 4.Any dead cell with exactly three live neighbours becomes a live cell. The initial pattern constitutes the seed of the system. The first(generation) is created by applying the above rules simultaneously to every cell in the seed�births and deaths happen simultaneously, and the discrete moment at which this happens is sometimes called a tick (in other words, each generation is a pure function of the one before). The rules continue to be applied repeatedly to create further generations.';
	}

}

//Grid species representing a cellular automata
grid life_cell width: environment_width height: environment_height neighbors: 8  use_individual_shapes: false use_regular_agents: false 
use_neighbors_cache: false parallel: parallel{
	//Boolean to know if it is the new state of the cell
	bool new_state;
	//List of all the neighbours
	list<life_cell> neighbours <- self neighbors_at 1;
	//Boolean  to know if it is a living or dead cell
	bool alive <- (rnd(100)) < density;
	
	rgb color <- alive ? livingcolor : deadcolor;
	
	//Action to evolve the cell considering its neighbours
	action evolve {
		//Count the number of living neighbours of the cells
		int living <- neighbours count each.alive;
		if alive {
			//If the number of living respect the conditions, the cell is still alive
			new_state <- living in living_conditions;
			color <- new_state ? livingcolor : dyingcolor;
		} else {
			//If the number of living meets the conditions, the cell go to born
			new_state <- living in birth_conditions;
			color <- new_state ? emergingcolor : deadcolor;
		}

	}
	//Action to update the new state of the cell
	reflex update {
		alive <- new_state;
	}

}


experiment "Game of Life" type: gui {
	parameter "Run in parallel " var: parallel category: 'Board';
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
		display Life type: 3d axes:false antialias:false{
			grid life_cell;
		}

	}

}
