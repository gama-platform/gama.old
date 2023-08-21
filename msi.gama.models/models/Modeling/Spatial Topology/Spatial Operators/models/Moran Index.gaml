/**
* Name: moran_Index
* Author: Patrick Taillandier
* Description: Computes the moran index of geometries
* Tags: spatial, distance
*/

model moranIndex

global {
	string grid_spatial_init <- "random" among: ["random", "checkerboard", "blocks"];
	string weight_type <- "neighbors" among: ["overlapping", "neighbors", "distance"];
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
		weights <- 0.0 as_matrix {length(vals), length(vals)};
		ask cell {
			switch weight_type {
				match "neighbors" {
					ask self.neighbors {
						weights[int(self), int(myself)] <- 1.0;
					}
				}
				match "overlapping" {
					ask cell overlapping self {
						if self != myself {
							weights[int(self), int(myself)] <- 1.0;
						}
					}
				}
				match "distance" {
					ask cell {
						if self != myself {
							weights[int(self), int(myself)] <- 1/(self distance_to myself);
						}
					}
				}
			}
		}
		I <- moran(vals, weights);
		write "moran I: " + I;
	}
}
grid cell width: grid_size height: grid_size neighbors: 4;

experiment "Moran" type: gui {
	parameter "grid size: " var: grid_size min: 2 max: 100;
	parameter "Type of spatial Initialization of grid value: " var: grid_spatial_init;
	parameter "Weight type used for the computation: " var: weight_type ;
	
	output {
		display map type:2d antialias:false {
			grid cell border: #red;
			graphics "moran" {
				draw "I = " + (I with_precision 3) color: #green font:font("Helvetica", 60 * #zoom, #bold);
			}
		}
	}
}
