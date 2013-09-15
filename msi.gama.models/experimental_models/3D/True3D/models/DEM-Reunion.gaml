/**
 *  Author: Arnaud Grignard
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('../includes/Reunion/mntreunion.png');
	file gridfile <- file('../includes/Reunion/mnt_reunion_asc.asc');
	geometry shape <- envelope(gridfile);
	
	init{
		ask cell where (each.grid_value < 0) {
			grid_value <- 0;
		}
		ask cell{
			set grid_value <- grid_value*10;
		}
		//create people number:100;
	}
	
	
}

entities {
	grid cell file: gridfile{
		reflex decreaseGridValue{
			if(grid_value >0){
			  set grid_value <- grid_value - 100;	
			} 	
	    }
	}
		
	
	 species people skills: [moving]{  
	 	
        reflex move{
            do wander;
            float z <- (cell(location)).grid_value;                 
            set location <- location add_z z+100;
            write "location" + self.location + "z" + z;
        }
        
        aspect base{
        	draw sphere(100);
        }
    }
}
experiment Display type: gui {
	output {
		
		/*display ReunionDEM  type: opengl {
			graphics GraphicPrimitive {
				draw dem(dem, dem, 100);
			}
		}*/
		
		display ReunionGrid  type: opengl  ambient_light:200{
			grid cell texture:false triangulation: true;
			//species people aspect:base;
		}
	}
}
