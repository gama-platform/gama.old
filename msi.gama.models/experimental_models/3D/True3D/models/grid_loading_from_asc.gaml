/**
 *  gridloading
 *  Author: arnaudgrignard
 *  Description: 
 */

model gridloading

global {
	file grid_file <- file("../includes/asc_grid/7x5.asc");
	file map_texture parameter: 'Texture' <- file('../includes/DEM_hanoi/maps.png');
	map colors <- map([1:: hsb(0.66,0.1,0.5), 2:: hsb(0.66,0.2,0.5),3:: hsb(0.66,0.3,0.5),4:: hsb(0.66,0.4,0.5),5:: hsb(0.66,0.5,0.5),6:: hsb(0.66,0.6,0.5),7:: hsb(0.66,0.7,0.5),8:: hsb(0.66,0.8,0.5),9:: hsb(0.66,0.9,0.5),10:: hsb(0.66,1.0,0.5) ]);
	geometry shape <- envelope(grid_file);
}

entities {
	grid cell file: grid_file {
		init {
			color <- colors at int(grid_value);
		}
	}
	
}

experiment gridloading type: gui{
	output {
		display gridTextureWithGridColor type:opengl{
			grid cell;
		}
		display gridTextureWithFile type:opengl{
			grid cell texture:map_texture;
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
		display gridTriangulatedWithFile type:opengl{
			grid cell texture:map_texture triangulation:true;
		}
		display classic{
			grid cell;
	    }
	}
}
