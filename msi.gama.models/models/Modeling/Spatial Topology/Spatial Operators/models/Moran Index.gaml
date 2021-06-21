/**
* Name: moran_Index
* Author: Patrick Taillandier
* Description: Computes the moran index of geometries
* Tags: spatial, distance
*/

model moranIndex

global {
	string grid_spatial_init <- "random" among: ["random", "checkerboard", "blocks"];
	string weight_type <- "overlapping" among: ["overlapping", "distance"];
	int grid_size <- 20;
	
	list<float> vals;
	matrix<float> weights;
	
	float I;
	
	init {
		ask cell {
			switch grid_spatial_init {
				match "random" {color <- flip(0.5) ? #white: #black;}
				match "checkerboard" {color <- even(grid_x) ? (even(grid_y) ? #white: #black) : (not even(grid_y) ? #white: #black) ;}
				match "blocks" {color <- grid_x < grid_size/2 ? #white: #black;}
			}	
		}
		vals <- cell collect (each.color = #white ? 0.0 : 1.0);
		weights <- 0.0 as_matrix {grid_size, grid_size};
		loop i from: 0 to: grid_size -1 {
			loop j from: 0 to:grid_size -1 {
				if (i = j) {weights[i,j] <- 0.0;}
				else {
					switch weight_type {
						match "overlapping" {
							weights[i,j] <- (cell[i] overlaps cell[j]) ? 1.0 : 0.0;
						}
						match "distance" {
							using topology(cell) {
								weights[i,j] <- 1/(cell[i] distance_to cell[j]);
							}
						}
							
					}
					
				}	
			}
		}
		I <- moran(vals, weights);
		write "moran I: " + I;
	}
}
grid cell width: grid_size height: grid_size neighbors: 8;

experiment "Moran" type: gui {
	parameter "grid size: " var: grid_size min: 2 max: 100;
	parameter "Type of spatial Initialization of grid value: " var: grid_spatial_init;
	parameter "Weight type used for the computation: " var: weight_type ;
	
	output {
		display map {
			grid cell border: #red;
			graphics "moran" {
				draw "I = " + (I with_precision 3) color: #green font:font("Helvetica", 60 * #zoom, #bold);
			}
		}
	}
}
