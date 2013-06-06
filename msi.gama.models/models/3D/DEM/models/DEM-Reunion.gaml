/**
 *  Author: Arnaud Grignard
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('../includes/Reunion/mntreunion.png');
	file gridfile <- file('../includes/Reunion/mnt_reunion_asc.asc');
	geometry shape <- envelope(gridfile);
	
	init{
		create people number:100{
			//set shape <- shape add_z 10;
		}
	}
}

entities {
	grid cell file: gridfile;
		
	
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
experiment display type: gui {
	output {
		
		display ReunionDEM  type: opengl ambient_light:255 {
			graphics GraphicPrimitive {
				draw dem(dem, dem, 100);
			}
		}
		
		display ReunionGrid  type: opengl ambient_light:255 {
			grid cell texture:false triangulation: true;
			species people aspect:base;
		}
	}
}
