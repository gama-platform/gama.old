model bubblesort1D

global {
	int nb_cells <- 10;
	int cell_size<-10;
	geometry shape <- rectangle(cell_size,nb_cells*cell_size);
	bool change <- true;
	init {
		int curcell <- 0;
		create cells number: nb_cells {
			location <- { cell_size/2 , curcell*cell_size + cell_size/2};
			red <- rnd(255);
			color <- rgb(red,0,0);
			curcell <- curcell + 1;
		}

	}

	reflex end {
		if (not change) {
			do pause;
		}

		change <- false;
	}

}

species cells {
	rgb color;
	int red;
	list<cells> neigbhours update: cells at_distance (cell_size*1.1);
	cells upper_cell update: neigbhours first_with (shape.location.y > each.shape.location.y);
	
	reflex swap when: upper_cell != nil and red > upper_cell.red {
		point tmp1Loc <- location;
		location <- upper_cell.location;
		upper_cell.location <- tmp1Loc;
		change <- true;
	}
	
	

	aspect default {
		draw cube(cell_size) color: color border: color at: location;
		draw "red: " + red size:5 color:°black at:{location.x+10,location.y} bitmap:false;
	}

}

experiment Display type: gui {
	output {
		display View1 type: opengl draw_env:false{ 
			species cells;
			
			graphics "info"{
				draw "bubble sort 1D" size:5 color:°black at:{world.shape.width*0.05,-world.shape.height*0.05} bitmap:false;
			}
		}
	}

}


