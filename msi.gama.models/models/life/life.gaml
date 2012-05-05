model life
// gen by Xml2Gaml


global {  
	var environment_width type: int init: 200 min: 10 max: 400 parameter: 'Width:' category: 'Board' ;
	var environment_height type: int init: 200 min: 10 max: 400 parameter: 'Height:' category: 'Board' ;  
	var torus_environment type: bool init: true parameter: 'Torus?:' category: 'Board' ;
	var density type: int init: 25 min: 1 max: 99 parameter: 'Initial density of live cells:' category: 'Cells' ; 
	var living_conditions type: list init: [2,3] parameter: 'Numbers of live neighbours required to stay alive:' category: 'Cells' ;  
	var birth_conditions type: list init: [3] parameter: 'Numbers of live neighbours required to become alive:' category: 'Cells' ; 
	var livingcolor type: rgb init: rgb('white') parameter: 'Color of live cells:' category: 'Colors' ;
	var dyingcolor type: rgb init: rgb('red') parameter: 'Color of dying cells:' category: 'Colors' ; 
	var emergingcolor type: rgb init: rgb('orange') parameter: 'Color of emerging cells:' category: 'Colors' ;
	var deadcolor type: rgb init: rgb('black') parameter: 'Color of dead cells:' category: 'Colors' ;
	action description  {
		do action: write { 
			arg message value: 'Description. The Game of Life is a cellular automaton devised by the British mathematician John Horton Conway in 1970. It is the best-known example of a cellular automaton. The game is a zero-player game, meaning that its evolution is determined by its initial state, requiring no further input from humans. One interacts with the Game of Life by creating an initial configuration and observing how it evolves.  The universe of the Game of Life is an infinite two-dimensional orthogonal grid of square cells, each of which is in one of two possible states, live or dead. Every cell interacts with its eight neighbors, which are the cells that are directly horizontally, vertically, or diagonally adjacent. At each step in time, the following transitions occur: \\n\\t 1.Any live cell with fewer than two live neighbours dies, as if caused by underpopulation. \\n\\t 2.Any live cell with more than three live neighbours dies, as if by overcrowding. \\n\\t 3.Any live cell with two or three live neighbours lives on to the next generation. \\n\\t 4.Any dead cell with exactly three live neighbours becomes a live cell. The initial pattern constitutes the seed of the system. The first(generation) is created by applying the above rules simultaneously to every cell in the seed—births and deaths happen simultaneously, and the discrete moment at which this happens is sometimes called a tick (in other words, each generation is a pure function of the one before). The rules continue to be applied repeatedly to create further generations.' ;
		}
	} 
	init { 
		do action: description ;  
	}
	reflex main {
		ask target: list (life_cell) {  
			do action: evolve ;
		} 
		ask target: list (life_cell) {
			do action: update;
		}
	}
}

	grid life_cell width: environment_width height: environment_height neighbours: 8 torus: torus_environment {
		var new_state type: bool;
		var state type: bool init: (rnd(100)) < density ;
		var color type: rgb init: state ? livingcolor : deadcolor ;  
		
		action evolve {
			let living type: int value: ((self neighbours_at 1) of_species life_cell) count each.state ;
			if condition: state {
				set new_state value: living in living_conditions ;
				set color value: new_state? livingcolor : dyingcolor ;
			} else {
					set new_state value: living in birth_conditions ;
					set color value: new_state? emergingcolor : deadcolor ;
				}
		}
		
		action update {
			set state value: new_state;
		} 
	}

environment width: environment_width height: environment_height {}
output {
	display Life {
		grid life_cell ;
	}
	inspect name: 'Agents' type: agent ;
}
