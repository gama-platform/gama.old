/**
* Name:  Computation of the shortest path on a Grid of Cells
* Author:  Patrick Taillandier
* Description: Model to represent how to compute the shortest path from a grid (with the 4 algorithms).
* Tags: grid, obstacle, shortest_path
*/

model Grid

global {
	
	/*4 algorithms for the shortest path computation on a grid:
	*      - A* : default algorithm: Very efficient for both Moore (8) and Von Neumann (4) neighborhoods. An introduction to A*: http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*      - Dijkstra : Classic Dijkstra algorithm. An introduction to Dijkstra : http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*      - JPS : Jump Point Search, only usable for Moore (8) neighborhood. Most of time, more efficient than A*. An introduction to JPS: https://harablog.wordpress.com/2011/09/07/jump-point-search/#3
	*      - BF : Breadth First Search. Should only be used for Von Neumann (4) neighborhood. An introduction to BF: http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*/
	
	string scenario <- "wall" among: ["random", "wall"] parameter: true;
	string algorithm <- "A*" among: ["A*", "Dijkstra", "JPS", "BF"] parameter: true;
	int neighborhood_type <- 8 among:[4,8] parameter: true;
	float obstacle_rate <- 0.1 min: 0.0 max: 0.9 parameter: true;
	int grid_size <- 50 min: 5 max: 100 parameter: true;
	point source;
	point goal;
	path the_path;
	init toto {    
		if (scenario = "wall") {
			ask cell {is_obstacle <- false;}
			int x_max <- round(grid_size * 2/3);
			loop i from: 2 to:x_max {
				cell[i, 3].is_obstacle <- true;
				cell[i, grid_size - 4].is_obstacle <- true;
			}
			loop i from: 3 to: grid_size - 4 {
				cell[x_max, i].is_obstacle <- true;
			}
			ask cell {color <- is_obstacle ? #black : #white;}
		}
		source <- (one_of (cell where not each.is_obstacle)).location;
		goal <- (one_of (cell where not each.is_obstacle)).location;

		using topology(cell) {
			the_path <- path_between((cell where not each.is_obstacle), source, goal);	
		}
	} 
	
	reflex compute_path {
		source <- (one_of (cell where not each.is_obstacle)).location;
		goal <- (one_of (cell where not each.is_obstacle)).location;

		using topology(cell) {
			the_path <- path_between((cell where not each.is_obstacle), source, goal);	
		}
	}
}

grid cell width: grid_size height: grid_size neighbors: neighborhood_type optimizer: algorithm{
	bool is_obstacle <- flip(0.1);
	rgb color <- is_obstacle ? #black : #white;
} 

	

experiment goto_grid type: gui {
	output {
		display objects_display type: 2d antialias:false{
			grid cell border: #black;
			graphics "elements" {
				draw circle(1) color: #green at: source border: #black;
				draw circle(1) color: #red at: goal  border: #black;
				loop v over: the_path.vertices {
					draw triangle(0.5) color: #yellow border: #black at: point(v);
				}
				loop s over: the_path.segments {
					draw s color: #red ;
				}
			}
		}
	}
}
