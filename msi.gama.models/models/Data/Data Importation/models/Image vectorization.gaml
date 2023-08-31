/***
* Name: EscapeTrainingEnvironment
* Author: pataillandie and kevinchapuis
* Description: Vectorize an image and save result into shapefile
* Tags: Vectorization, image, synthetic environment
***/

model EscapeTrainingEnvironment

global {
	
	/*
	 * How precise the vectorization is
	 */
	float resolution_factor <- 0.2 parameter:true max:1.0;
	 
	/*
	 * Import the image to vectorize
	 */
	image_file im <- image_file("../images/drawing.png");
	
	/*
	 * Get the resolution of the image
	 */
	int res_x <- int(envelope(im).width);
	int res_y <- int(envelope(im).height);
	
	/*
	 * 
	 * Adapt the underlying grid to vectorize and the shape of the world
	 * according to image resolution and the ratio of vectorization
	 * 
	 */
	int g_x <- int(res_x * resolution_factor);
	int g_y <- int(res_y * resolution_factor);	
	geometry shape <- rectangle(res_x,res_y);
	
	/*
	 * The color and associated species
	 * WARNING: Model specific
	 */
	map<rgb,string> color_to_species <- [
		#brown::string(ground),#blue::string(water),#yellow::string(beach),
		#green::string(tree),#gray::string(building)
	];
	
	init {
		float t <- machine_time;
		
		write "START CREATION OF THE ENVIRONMENT";
		
		write "Image resolution : "+string(res_x)+" x "+string(res_y);
		
		/*
		 * Manage resolution ratio
		 */
		float factorDiscret_width <- res_y / g_y;
		float factorDiscret_height <- res_x / g_x;
		ask cell {		
			color <-rgb( (im) at {grid_x * factorDiscret_height,grid_y * factorDiscret_width}) ;
		}
		
		/*
		 * Find the different color in the image
		 */
		map<rgb, list<cell>> cells_per_color <- cell group_by each.color;
		
		write "Found "+length(cells_per_color)+" color in the draw";
		
		/*
		 * Loop over all colors and match them with proper species
		 */
		loop col over: cells_per_color.keys {
			geometry geom <- union(cells_per_color[col]) + 0.001;
			if (geom != nil) {
				
				write "--------";
				rgb best_match;
				list bm <- [255,255,255];
				loop cl over:color_to_species.keys {
					int r <- abs(cl.red-col.red);
					int g <- abs(cl.green-col.green);
					int b <- abs(cl.blue-col.blue);
					if(r+g+b < sum(bm)){
						best_match <- cl;
						bm <- [r,g,b];
					}
				}
				write "Detected color image ["+string(col)+"] has been associated to ["+string(best_match)+"]";

				/*
				 * Create the proper species where color have been detected
				 */
				string species_name <- color_to_species[best_match];
				switch species_name {
					match string(water) {
						create water from: geom.geometries;
					}
					match string(ground) {
						create ground from: geom.geometries;
					}
					match string(beach) {
						create beach from: geom.geometries;
					}
					match string(tree) {
						create tree from: geom.geometries;
					}
					match string(building) {
						create building from: geom.geometries;
					}
				}
			}
		}
		write "END - TIME ELAPSE: "+((machine_time-t)/1000)+"sec";
		
		write "EXPORT TO FILES";
		save water to:"../results/water_body.shp" ;
		save ground to:"../results/ground.shp" ;
		save beach to:"../results/beach.shp" ;
		save tree to:"../results/trees.shp" ;
		save building to:"../results/building.shp" ;
		
	}
	
}

grid cell  width: g_x height: g_y;

species water {
	aspect default {
		draw shape color: #navy border: #black;
	}
}

species building {
	aspect default {
		draw shape color: #gray;
	}
}

species ground {
	aspect default {
		draw shape color: rgb (128, 64, 3) border: #black;
	}
}

species beach {
	float capacity;
	aspect default {
		draw shape color:#yellow;
	}
}

species tree {
	aspect default {
		draw shape border:#black color:#green;
	}
}

experiment Vectorize type: gui {
	output {
		display map_vector type:3d axes:false{
			species water;
			species ground;
			species beach;
			species tree;
			species building;
		}
		display image {
			image im;
		}
	}
}