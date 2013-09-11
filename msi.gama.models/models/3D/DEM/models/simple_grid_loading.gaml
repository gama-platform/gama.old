/**
 *  gridloading
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: This create a DEM representation from an .asc file
 *  Cells are created with the parameter file:grid_file that will initialize the z value of each cell
 *  
 */

model gridloading

global {
	file grid_file <- file("../includes/asc_grid/7x5.asc");
	file map_texture parameter: 'Texture' <- file('../includes/DEM-vulcano/Texture.png');
	map colors <- map([0:: rgb([240,240,255]),1:: rgb([210,233,254]), 2:: rgb([199,223,244]),3:: rgb([146,197,234]),4:: rgb([125,152,187]),5:: rgb([91,117,167]),6:: rgb([42,69,122])]);
	geometry shape <- envelope(grid_file);
}

entities {
	grid cell file: grid_file {
		init {
			color <- colors at int(grid_value);
		}
		reflex decreaseGridValue{
			if(grid_value >0){
			  grid_value <- grid_value - 0.01;	
			} 	
	    }
	}
	
}

experiment gridloading type: gui {
	output {
						
		display gridNonTextured type:opengl ambient_light:100{
			grid cell;
		}
		
		display gridTextured type:opengl ambient_light:255{
			grid cell texture:map_texture triangulation:false;
		}
		
		display gridTextureWithText type:opengl{
			grid cell text:true;
		}
		
		display gridNonTexturedWithDEMValue type:opengl{
			grid cell texture:false;
		}
		
		display gridTriangulatedWithGridColor type:opengl{
			grid cell triangulation:true;
		}
		display gridTriangulatedWithTexture type:opengl{
			grid cell texture:map_texture triangulation:true;
		}
		display classic type:opengl{
			grid cell draw_as_dem:false;
	    }
		
	}
}
