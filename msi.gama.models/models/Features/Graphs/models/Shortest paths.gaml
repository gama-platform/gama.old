/**
 *  ShortestPath
 *  Author: Patrick Taillandier
 *  Description: Give some example of shortest path computation. 
 *  For more details about the use of such paths see models in Feature/Goto Action
 */

model ShortestPath

global {
	file shape_file_in <- file('../includes/road.shp') ;
	file shape_file_bounds <- file('../includes/bounds.shp') ;
	geometry shape <- envelope(shape_file_bounds);
	graph road_graph; 
	point source;
	point target;
	path shortest_path;
	list<path> k_shortest_paths;
	int k <- 3; 
	list<rgb> colors <- [rgb("red"),rgb("green"),rgb("blue"),rgb("pink"),rgb("cyan"),rgb("magenta"),rgb("yellow")];
	bool save_shortest_paths <- false;
	bool load_shortest_paths <- false;
	string shortest_paths_file <- "../includes/shortest_paths.csv";
	
	init {
		create road from: shape_file_in;
		road_graph <- as_edge_graph(road);
		
		//computes all the shortest paths, puts them in a matrix, then saves the matrix in a file
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(road_graph);
			write "Matrix of all shortest paths: " + ssp;
			save ssp type:"text" to:shortest_paths_file;
			
		//loads the file of the shortest paths as a matrix and uses it to initialize all the shortest paths of the graph
		} else if load_shortest_paths {
			road_graph <- road_graph load_shortest_paths matrix(file(shortest_paths_file));
		}
	}
	
	reflex compute_shortest_paths {
		source <- point(one_of(road_graph.vertices));
		target <- point(one_of(road_graph.vertices));
		if (source != target) {
			shortest_path <- path_between (road_graph, source,target);
			k_shortest_paths <- list<path>(paths_between(road_graph,source::target,k));	
		}
	}
}

species road  {
	aspect base {
		draw shape color: rgb('black') ;
	} 
}

experiment ShortestPath type: gui {
	parameter "number of shortest paths (k)" var: k min: 1 max: 7;
	parameter "Computed all the shortest paths and save the results" var: save_shortest_paths;
	parameter "Load the shortest paths from the file" var: load_shortest_paths;
	
	output {
		display map_shortest_path {
			species road aspect: base;
			graphics "shortest path" {
				if (shortest_path != nil) {
					draw circle(5) at: source color: rgb("green");
					draw circle(5) at: target color: rgb("cyan");
					draw (shortest_path.shape + 2.0) color: rgb("magenta");
				}
			}
		}
		display map_k_shortest_paths {
			species road aspect: base;
			graphics "k shortest paths" {
				if (shortest_path != nil) {
					draw circle(5) at: source color: rgb("green");
					draw circle(5) at: target color: rgb("cyan");
					loop i from: 0 to: length(k_shortest_paths) - 1{
						draw ((k_shortest_paths[i]).shape + 2.0) color: colors[i];
					}
				}
			}
		}
	}
}
