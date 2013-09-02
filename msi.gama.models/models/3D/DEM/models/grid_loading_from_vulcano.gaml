/**
 *  gridloading
 *  Author: arnaudgrignard
 *  Description: This create a DEM representation from an .asc file
 *  Cells are created with the parameter file:grid_file that will initialize the z value of each cell
 *  
 * 
 */

model gridloading

global {
	file grid_file <- file("../includes/asc_grid/vulcano_50.asc");
	file map_texture parameter: 'Texture' <- file('../includes/DEM-vulcano/Texture.png');
	map colors <- map([1:: rgb([178,180,176]), 2:: rgb([246,111,0]),3:: rgb([107,0,0]),4:: rgb([249,0,255]),5:: rgb([144,96,22]),6:: rgb([255,255,86]),7:: rgb([19,114,38]),8:: rgb("black"),9:: rgb([107,94,255]),10:: rgb([43,255,255]) ]);
	geometry shape <- envelope(grid_file);
	
	init{
		create people number:100 {
			float z <- (cell(location)).grid_value;                 
            set location <- location add_z z;
		}
	}
}

entities {
	grid cell file: grid_file {
		init {
			color <- colors at int(grid_value);
		}
		reflex decreaseGridValue{
			if(grid_value >0){
			  set grid_value <- grid_value - 0.01;	
			} 	
	    }
	}
	
    species people skills: [moving]{  
	 	rgb color;
        reflex move{
            do wander;
            float z <- (cell(location)).grid_value;                 
            set location <- location add_z z;
        }
        
        aspect base{
        	let heading1 <-rnd(360);
        	let hue <- heading1/360;
			let  color <- color hsb_to_rgb ([hue,1.0,1.0]);
        	let geometry1 <- geometry (triangle(1));
			draw geometry: geometry1    size: 1 rotate: heading1 color: color border:color depth:0.5;
        }
    }
}

experiment gridloading type: gui {
	output {
		display gridTextured type:opengl ambient_light:255{
			grid cell texture:map_texture triangulation:false;
			species people aspect:base;
		}
				
		display gridTexturedTriangulated type:opengl ambient_light:100{
			grid cell texture:map_texture text:false triangulation:true;
			species people aspect:base;
		}
		
		display gridNonTextured type:opengl ambient_light:100{
			grid cell;
			species people aspect:base;
		}
		
	}
}
