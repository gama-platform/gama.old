/**
* Name: Spatial Interpolation
* Author: Patrick Taillandier
* Description: A model which shows how to use spatial interpolation
* Tags: topology, grid, spatial_computation
*/

model spatialinterpolation

global {
	map<point, float> pts;
	float max_val; 
	float min_val ; 
	
	//power parameter
	int power <- 2;
	
	//cell properties
	int nb_rows <- 50;
	int nb_columns <- 50;
	
	init {
		//creation of random point for demo puporse
		loop times: 50 {
			point pt <- any_location_in(world);
			pts[pt] <-pt.x ^2 + pt.y ^2;
		}
		
		//computation of the min and max values of the points (just use to define the color of the cells).
		max_val <- max(pts.values);  
		min_val <- min(pts.values); 
		
		//computation of the value for each cell
		map<cell_shape, float> results_shape <- map<cell_shape, float>(IDW(cell_shape, pts, power));
		
		//setting of the value and color of each cell
		ask cell_shape {
			val <- results_shape[self];
			float val_col <- 255 * (val - min_val) / (max_val - min_val);
			color <- rgb(val_col,0,0);
		}
		
		//same computation, but in this case, we consider only the centroid (location) of the cell for the computation of the distance
		map<point, float> results_location <- map<point, float>(IDW(cell_location collect each.location, pts, power));
			
		ask cell_location {
			val <- results_location[self.location];
			float val_col <- 255 * (val - min_val) / (max_val - min_val);
			color <- rgb(val_col,0,0);
		}
	}
}

//cells where the distance is based on the shape of the cell (min distance between the rectangle and the points)
grid cell_shape width: nb_columns height: nb_rows {
	float val;
}

//cells where the distance is based on the centroid of the cell (distance between the centroid and the points)
grid cell_location width: nb_columns height: nb_rows {
	float val;
}


experiment spatialinterpolation type: gui {
	parameter "nb of columns" var: nb_columns;
	parameter "nb of rows" var: nb_rows;
	parameter "power parameter" var: power;
	output {
		display map_shape {
			grid cell_shape;
			graphics "points" {
				loop pt over: pts.keys {
					float val <- pts[pt];
					float val_col <- 255 * (val - min_val) / (max_val - min_val);
					draw circle(1) at: pt color: rgb(val_col,0,0) border: #white;
				}
			}
		}
		display map_location {
			grid cell_location;
			graphics "points" {
				loop pt over: pts.keys {
					float val <- pts[pt];
					float val_col <- 255 * (val - min_val) / (max_val - min_val);
					draw circle(1) at: pt color: rgb(val_col,0,0) border: #white;
				}
			}
		}
	}
}
