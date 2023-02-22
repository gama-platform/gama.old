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
		ask cell {color <- #white;}
	}
	reflex show_neighborhood {
		ask cell {color <- #white;}
		ask one_of(cell) {
			color <- #red;
			ask neighbors {
				color <- #green;
			}
		}
	}
}

// the choices are 4,6 or 8 neighbors
grid cell height: 10 width: 10 neighbors: 6 horizontal_orientation: orientation = "horizontal";


experiment hexagonal type: gui{
	output  synchronized:true{
		display view type: 3d{
			grid cell border: #black ;
		}
	}
}