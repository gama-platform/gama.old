model wagama_mnt

global {
    
    
    file nodes_file <- file('../includes/nodes.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
    file env_file <- file('../includes/environment.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
    file mntImageRaster <- file('../images/mnt.png') ;
    int nb_rows <- 100;
    int nb_lines <- 100;
    
    list nodes of: node function: {node as list};
	
	init {
		let mat type: matrix <-	mntImageRaster as_matrix {nb_rows, nb_lines};
		create node from: nodes_file with: [id::read("ID"), id_next::read("ID_NEXT"), source::read("SOURCE")];
		
		ask nodes {
			set next_node <- nodes first_with (each.id = id_next);
		}
		
		ask cell as list {	
			set color <- mntImageRaster at {grid_x,grid_y} ;
			set val <- mean(list (color));
			//do write message: val;
		}
		
		ask cell as list {		
			let cells_possibles type: list of: cell <- (self neighbours_at 2) + self;
			loop i from: 0 to: length(shape.points) - 1{ 
				let geom type: geometry <- square(1.0);
				set geom <- geom translated_to (shape.points at i);
				let myCells type: list of: cell <-  cells_possibles where (each.shape intersects geom);
				let val type: float <- mean (myCells collect (each.val));
				set shape <- shape add_z_pt {i,(val / 2.0)};
			}
		}
		
		ask nodes{	
			//let z type:float  <- cell(cell grid_at shape.location).val;	
			//write " " + 
			
			let cells type:list of:cell <- list(cell) where (each intersects shape.location);
			let z <-first(cells).val;
			/*loop i from: 0 to: length(shape.points) - 1{ 
				set shape <- shape add_z_pt {i,(z / 2.0)};
			}*/
			set shape <- shape add_z  ((z / 2.0));
			//write z;
			write "location1 : " + shape.location;
				
		}	 
	}

}
	

environment bounds: env_file{
	grid cell  width:nb_rows  height: nb_lines neighbours: 4 {
		float val; 
		float water <- 0.0;
		float entry_water <- 0.0;
		list voisins of: cell <- self neighbours_at 1;
		cell voisin_min_elevation;
		
		aspect base {
			draw shape: geometry color:color ; 
		}

	}
	
	species node {
		const radius type: float <- 2.0;
		rgb color <- rgb('red');
		string id;
		string id_next;
		string source;
		node next_node;  
		geometry shape <- circle (radius) ;
		
		aspect circle {
			
			//y' pas de z
			//draw shape: circle size: radius color: color;
			//crŽe le cercle 
			//draw geometry: circle (radius) color: color;
			
			//le shape est crtŽe ˆ l'etat initiale 
			draw geometry: shape color: color;
		}  
		aspect network { 
			if (next_node != nil) { 
				write "location : " + location;
				write "next_node.location : " + next_node.location; 
				//Set the location of the node (not only the shape)
				draw geometry: line([location, next_node.location]) color: rgb('blue');
			}
			
			//draw geometry: shape color: color;
			//draw geometry: circle (radius) color: color;
			//draw shape: circle size: radius color: color;
		}
	}
}

experiment display  type: gui {
	
	output {
	display city_display type:opengl  refresh_every: 1 {
		
		species node aspect: network refresh:false ;	
		species node aspect: circle ;
		species cell aspect: base transparency: 0.5 refresh:false;	
		//image name: 'Background' file: mntImageRaster.path;
		
	}
}
}






