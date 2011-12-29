model sort
// gen by Xml2Gaml

global {
	var white type: rgb init: rgb('white') ;
	var black type: rgb init: rgb('black') ;
	const colors type: list init: ['yellow','red', 'orange', 'blue', 'green','cyan', 'gray','pink','magenta'] ;
	var number_of_different_colors type: int init: 4 max: 9 parameter: 'Number of colors:' category: 'Environment' ;
	var density_percent type: int init: 30 min: 0 max: 99 parameter: 'Density of colors:' category: 'Environment' ;
	var number_of_objects_in_history type: int init: 3 min: 0 parameter: 'Number of similar colors in memory necessary to put down:' category: 'Agents' ;
	var number_of_objects_around type: int init: 5 min: 0 max: 8 parameter: 'Number of similar colors in perception necessary to pick up:' category: 'Agents' ;
	var width_and_height_of_grid type: int init: 100 max: 400 min: 20 parameter: 'Width and height of the grid:' category: 'Environment' ;
	var number_of_ants type: int init: 100 min: 1 parameter: 'Number of agents:' category: 'Agents' ;
	action description {
		do action: write {
			arg message value: "\\n Description. \\n This model is loosely based on the behavior of ants sorting different elements in their nest. \\n A set of mobile agents - the ants - is placed on a grid. The grid itself contains cells of different colors. Each step, the agents move randomly. If they enter a colored cell, they pick this color if its density in the neighbourhood is less than *number_of_objects_around*. If they have picked a color, they drop it on a black cell if they have encountered at least *number_of_objects_in_history* cells with the same color.\\n After a while, colors begin to be aggregated. " ;	
		}
	}
	init {
		do action: description ;
		create species: ant number: number_of_ants ;
	}
}
environment width: 100 height: 100 {
	grid ant_grid width: width_and_height_of_grid height: width_and_height_of_grid neighbours: 8 torus: true {
		var color type: rgb init: (rnd(100)) < density_percent ? (colors at rnd(number_of_different_colors - 1)) as rgb : world.black ;
		const neighbours type: list of: ant_grid init: self neighbours_at 1;
	}
}
entities {
	species ant skills: [moving, visible] control: fsm {
		var color type: rgb init: white ;
		var place type: ant_grid value: ant_grid (location) ;
		reflex wandering {
			do action: wander {
				arg amplitude type: int value: 120 ;
			}
		}
		state empty initial: true {
			transition to: full when: (place.color != black) and ((place.neighbours count (each.color = place.color)) < (rnd(number_of_objects_around))) {
				set color var: color value: place.color ;
				set place.color var: place.color value: black ;
			}
		}
		state full {
			enter {
				let encountered value: 0 type: int ;
			}
			if condition: place.color = color {
				set encountered value: encountered + 1 ;
			}
			transition to: empty when: (place.color = black) and (encountered > number_of_objects_in_history) {
				set place.color var: place.color value: color ;
				set color var: color value: black ;
			}
		}
		aspect default {
			draw shape: circle size: 2 empty: false color: color ;
		}
	}
}
output {
	display grille refresh_every: 100 {
		grid ant_grid size: {0.8,0.8} position: {0.1,0.1} ;
		species ant transparency: 0.5 size: {0.8,0.8} position: {0.1,0.1} ;
	}
}
