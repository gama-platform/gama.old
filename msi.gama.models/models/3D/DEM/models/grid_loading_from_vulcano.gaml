/**
 *  gridloading
 *  Author: arnaudgrignard
 *  Description: 
 */

model gridloading

global {
	file grid_file <- file("../includes/asc_grid/vulcano_50.asc");
	file map_texture parameter: 'Texture' <- file('../includes/DEM-vulcano/DEM_50.png');
	map colors <- map([1:: rgb([178,180,176]), 2:: rgb([246,111,0]),3:: rgb([107,0,0]),4:: rgb([249,0,255]),5:: rgb([144,96,22]),6:: rgb([255,255,86]),7:: rgb([19,114,38]),8:: rgb("black"),9:: rgb([107,94,255]),10:: rgb([43,255,255]) ]);
	geometry shape <- envelope(grid_file);
}

entities {
	grid cell file: grid_file {
		init {
			//write " grid_value : " + grid_value;
			color <- rnd(255);//colors at int(grid_value);
		}
	}
}

experiment gridloading type: gui {
	output {
		display gridTextured type:opengl {
			grid cell texture:map_texture triangulation:false;
		}
		
		
		display grid type:opengl {
			grid cell texture:map_texture text:false triangulation:true;
		}
		
		display java2D {
			grid cell ;
		}
	}
}
