/**
* Name: HexagonalGrid
* Author: Patrick Taillandier
* Description: 
* Tags: grid, hexagon
*/

model HexagonalGrid

global {
	string orientation <- "horizontal" among: ["horizontal", "vertical"] parameter: true;	
	
	init {
		ask cell {color <- #grey;}
	}
	reflex show_neighborhood {
		ask cell {color <- #grey;}
		ask one_of(cell) {
			color <- #red;
			ask neighbors {
				color <- #green;
			}
		}
	}
}

grid cell height: 10 width: 10 neighbors: 6 horizontal_orientation: orientation = "horizontal";


experiment hexagonal type: gui {
	output {
		display view type: opengl{
			grid cell lines: #black ;
		}
	}
}