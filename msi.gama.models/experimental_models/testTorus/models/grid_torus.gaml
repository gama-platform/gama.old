model Grid
// proposed by Patrick Taillandier

global {
	reflex test when: time = 0 {
		let the_cell type: cell <- first(cell as list);
		ask the_cell neighbours_at 1 {
			set color <- rgb('red');
		}
	}
}
environment bounds: {5,5} { 
	grid cell width: 5 height: 5 neighbours: 8 torus: true {
		rgb color <- rgb('white');
		
	} 
}
entities {
}

experiment goto_grid type: gui {
	output {
		display grid_display {
			grid cell lines: rgb('black');
		}
	}
}
