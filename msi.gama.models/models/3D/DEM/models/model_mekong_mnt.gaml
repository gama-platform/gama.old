model tutorial_gis_city_traffic

global {
    
    
    file shape_file_river <- file('../includes/Mekong_River/majortribdskratie.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
	
    file mntImageRaster <- file('../includes/Mekong_River/DEM_VMD_grey_scale_resize.png') ;
    int nb_rows <- 106;
    int nb_lines <- 112;
	
	init {
		let mat type: matrix <-	mntImageRaster as_matrix {nb_rows, nb_lines};
		ask cell as list {	
			set color <- mntImageRaster at {grid_x,grid_y} ;
			//write (color as list);
			set val <- 255-mean(list (color));
		}
		
		ask cell as list {		
			let cells_possibles type: list of: cell <- (self neighbours_at 2) + self;
			loop i from: 0 to: length(shape.points) - 1{ 
				let geom type: geometry <- square(1.0);
				set geom <- geom translated_to (shape.points at i);
				let myCells type: list of: cell <-  cells_possibles where (each.shape intersects geom);
				let val type: float <- mean (myCells collect (each.val));
				set shape <- shape add_z_pt {i,(val^2)};
				//do write message: "z"+val^2;
			}
			//set voisin_min_elevation <- voisins with_min_of (each.val);
		}	 
	}

}
	

//environment bounds: shape_file_commune ;
environment bounds: shape_file_river{
	grid cell  width:nb_rows  height: nb_lines neighbours: 4 {
		float val; 
		float water <- 0.0;
		float entry_water <- 0.0;
		list voisins of: cell <- self neighbours_at 1;
		cell voisin_min_elevation;
		
		aspect base {
			draw shape: geometry color: water > 0 ? rgb('blue') : color ; 
		}

	}
}

experiment display  type: gui {
	
	output {
	display city_display  type: opengl refresh_every: 1 {
		species cell aspect: base;
		image name: 'Background' file: mntImageRaster.path;
	}
}
}






