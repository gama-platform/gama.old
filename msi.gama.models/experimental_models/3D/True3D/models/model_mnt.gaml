model tutorial_gis_city_traffic

global {
	file shape_file_commune <- file('../includes/test.shp') parameter: 'Shapefile for the communes:' category: 'GIS' ;

    
    //file shape_file_building <- file('../includes/BAT/BAT.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
    
    file mntImageRaster <- file('../includes/MNT10001.png') ;
    
	list ptsSources of: cell;
	
	init {
		ask cell as list {		
			set color <- mntImageRaster at {grid_x,grid_y} ;
			set val <- mean(list (color));
		}	
		ask cell as list {		
			let cells_possibles type: list of: cell <- (self neighbours_at 2) + self;
			loop i from: 0 to: length(shape.points) - 1{ 
				let geom type: geometry <- square(1.0);
				set geom <- geom translated_to (shape.points at i);
				let myCells type: list of: cell <-  cells_possibles where (each.shape intersects geom);
				let val type: float <- mean (myCells collect (each.val));
				set shape <- shape add_z_pt {i,val^2};
			}
			set voisin_min_elevation <- (self neighbours_at 1) with_min_of (each.val);
		}

		create commune from: shape_file_commune  { 
		
			loop i from: 0 to: length(shape.points) - 1{ 
				let myCell type: cell <- (shape.points at i) as cell;
				let val type: float <- mean(list (myCell.color));
				set shape <- shape add_z_pt {i,val^2};
				//do write message: 'z:' + val;
			}      
	  	}
	  	
	  	set ptsSources <- cell as list where (each.name = "cell6459" or each.name="cell8085") ;
	  
	  
	}
	
	
	reflex pluie {
		ask ptsSources{
	  		set water <- 5;
	  	}
	}
		reflex dynamic  {
	  		ask cell as list  {
	  			do diffuse;
	  		}
	  		ask cell as list {
	  			do update;
	  		}
	  	}
	  	
	

}
entities {
	species commune {
		string type; 
		rgb color <- rgb('green')  ; 
		aspect base {
			draw shape: geometry color: color ; 
		}
	}
	
	
}
//environment bounds: shape_file_commune ;
environment bounds: shape_file_commune{
	grid cell  width:121  height: 67 neighbours: 4 {
		float val; 
		float water <- 0.0;
		float entry_water <- 0.0;
		list voisins of: cell <- self neighbours_at 1;
		cell voisin_min_elevation;
		
		aspect base {
			draw shape: geometry color: water > 0 ? rgb('blue') : color ; 
		}
		
		action update {
			set water <- entry_water;
			set entry_water <- 0;
		}
		
		/*action diffuse {
			let voisins_min_elevation type: list of: cell <- ((shuffle(voisins)) where ((each.val + each.water) < (self.val + self.water)) ); 
			//write voisins_min_elevation collect (each.val + each.water);
			ask voisins_min_elevation {
				if (myself.water > 0) {
					let val_water type: float <- min ([myself.water, max ([0, (myself.val + myself.water) - (self.val + self.water)])]);
					set entry_water <- entry_water + val_water;
					set myself.water <- myself.water - val_water;
				}
			}
			if (water > 0) {
				set entry_water <- entry_water + water;
			}
			
		}*/
		
		action diffuse {
			//let quantity_water type: float <- min ([water, water + val - voisin_min_elevation.val]);
			ask self neighbours_at 1 with_min_of (each.water + each.val)  {
				set entry_water <- entry_water + myself.water * 0.8;
			}
			set entry_water <- entry_water + water * 0.2;
			
		}
		
	}
}

experiment Display  type: gui {
	
	output {
	display city_display type: opengl refresh_every: 1 {
		
		species cell aspect: base;
		species commune aspect:base ;
		//image name: 'Background' file: mntImageRaster.path;
		//species zh aspect:base ;
		//species ilot aspect:base;
		//species river aspect:base ;

	}
}
}






