/**
 *  gridloading
 *  Author: patricktaillandier
 *  Description: 
 */

model gridloading

global {
	file grid_file <- file("../includes/asc_grid/hab70.asc");
	map colors <- map([1:: rgb([178,180,176]), 2:: rgb([246,111,0]),3:: rgb([107,0,0]),4:: rgb([249,0,255]),5:: rgb([144,96,22]),6:: rgb([255,255,86]),7:: rgb([19,114,38]),8:: rgb("black"),9:: rgb([107,94,255]),10:: rgb([43,255,255]) ]);
	geometry shape <- envelope(grid_file);
}

entities {
	grid cell file: grid_file {
		init {
			//write " grid_value : " + grid_value;
			color <- colors at int(grid_value);
		}
	}
}

experiment gridloading type: gui {
	output {
		display gridTextured type:opengl {
			grid cell texture:true;
		}
		
		display grid type:opengl {
			grid cell texture:false text:true;
		}
		
		display java2D {
			grid cell ;
		}
	}
}
