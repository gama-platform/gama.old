model ant_sort

global torus: true {
	geometry ant_normal <- (geometry(svg_file("../images/ant_normal.svg")) rotated_by -90) scaled_to {10,10};
	// Parameters
	int number_of_different_colors <- 4 max: 9 ;
	int density_percent <- 30 min: 0 max: 99 ;
	int number_of_objects_in_history <- 3 min: 0 ;
	int number_of_objects_around  <- 5 min: 0 max: 8;
	int width_and_height_of_grid <- 100 max: 400 min: 20 ;
	int ants <- 100 min: 1 ;

	rgb black <- rgb("black")  ;	
	const colors type: list <- ["yellow","red", "orange", "blue", "green","cyan", "gray","pink","magenta"] ;
	
	action description {
		write "\n Description. \n This model is loosely based on the behavior of ants sorting different elements in their nest. \n A of mobile agents - the ants - is placed on a grid. The grid itself contains cells of different colors. Each step, the agents move randomly. If they enter a colored cell, they pick this color if its density in the neighbourhood is less than *number_of_objects_around*. If they have picked a color, they drop it on a black cell if they have encountered at least *number_of_objects_in_history* cells with the same color.\n After a while, colors begin to be aggregated. " ;	
	}  
	init { 
		do description ;
		create ant number: ants ;
	} 
}
entities {
	species ant skills: [ moving ] control: fsm { 
		rgb color <- rgb("white") ; 
		ant_grid place -> {ant_grid (location)} ;
		
		reflex wandering { 
			do wander amplitude: 120;
		}
		state empty initial: true {
			transition to: full when: (place.color != black) and ( (place.neighbours count (each.color = place.color)) < (rnd(number_of_objects_around))) {
				color <- place.color ;
				place.color <- black ; 
			}
		}
		state full {
			enter { 
				int encountered <- 0; 
			}
			if place.color = color { 
				encountered <- encountered + 1 ;
			}
			transition to: empty when: (place.color = black) and (encountered > number_of_objects_in_history) {
				place.color <- color ;
				color <- black ;
			}
		}
		aspect default {
			draw ant_normal rotate: heading at: location empty: false color: color;
		}
	}

	grid ant_grid width: width_and_height_of_grid height: width_and_height_of_grid neighbours: 8 use_regular_agents: false frequency: 0{
		rgb color <- (rnd(100)) < density_percent ? (colors at rnd(number_of_different_colors - 1)) as rgb : world.black ;
		list<ant_grid> neighbours <- self neighbours_at 1;   
	} 
}


	
experiment sort type: gui{
	parameter "Number of colors:" var: number_of_different_colors category: "Environment" ;
	parameter "Density of colors:" var: density_percent category: "Environment" ;
	parameter "Number of similar colors in memory necessary to put down:" var: number_of_objects_in_history category: "Agents" ;
	parameter "Number of similar colors in perception necessary to pick up:" var: number_of_objects_around category: "Agents" ;
	parameter "Width and height of the grid:" var: width_and_height_of_grid category: "Environment" ;
	parameter "Number of agents:" var: ants category: "Agents" ;
	
	output {
		display grid_display refresh_every: 1 type: opengl {
			grid ant_grid ;
			species ant transparency: 0.2 ;
		}
	}
}


