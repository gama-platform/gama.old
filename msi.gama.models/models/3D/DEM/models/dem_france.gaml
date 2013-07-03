model tutorial_gis_city_traffic

global {
    
    
    file shape_file_river <- file(project_path + 'DEM/includes/Mekong_River/majortribdskratie.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;	
    file mntImageRaster <- file(project_path + 'DEM/includes/France/france_mnt_gray_scale_100.png') ;
    int nb_rows <- 100;
    int nb_lines <- 100;
	
	init {

		ask cell as list {	
			set color <- mntImageRaster at {grid_x,grid_y} ;
			set z <- 255-mean(list (color));
		}
		
		ask cell as list {		
			let cells_possibles type: list of: cell <- (self neighbours_at 2) + self;
			loop i from: 0 to: length(shape.points) - 1{ 
				let geom type: geometry <- square(1.0);
				set geom <- geom translated_to (shape.points at i);
				let myCells type: list of: cell <-  cells_possibles where (each.shape intersects geom);
				let z type: float <- mean (myCells collect (each.z));
				set shape <- shape add_z_pt {i,(z^2.2)};
			}
		}	 
	}

}
	

environment bounds: shape_file_river{
	grid cell  width:nb_rows  height: nb_lines neighbours: 4 {
		float z; 
		float water <- 0.0;
		float entry_water <- 0.0;
		list voisins of: cell <- self neighbours_at 1;
		cell voisin_min_elevation;
		
		aspect base {
			draw shape color:color ; 
		}

	}
}

experiment display  type: gui {
	
	output {
	display city_display  type: opengl refresh_every: 1 tesselation:false{
		species cell aspect: base refresh:false;
		//image name: 'Background' file: mntImageRaster.path;
	}
}
}






