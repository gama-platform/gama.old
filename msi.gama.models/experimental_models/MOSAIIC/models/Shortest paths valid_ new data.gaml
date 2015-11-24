/**
 *  ShortestPath
 *  Author: Patrick Taillandier
 *  Description: Give some example of shortest path computation. 
 *  For more details about the use of such paths see models in Feature/Goto Action
 */

model ShortestPath

global {
	//file shape_file_in <- file('../includes/roads_validationL93.shp') ;
	file shape_file_roads  <- file("../includes/roads_7200_with_pm_small_small.shp") ;
	file shape_file_nodes  <- file("../includes/nodes_7200_with_pm_small_small_Nettoye.shp");
	
	map<node_,int> nodeId;
	
	file file_ssp;// <- csv_file("../includes/data_validation/shortest_paths_speed_lanes.csv",";", (0 as_matrix {13417,13417})); 
	geometry shape <- envelope(shape_file_roads);
	graph road_graph; 
	node_ source;
	node_ target;
	path shortest_path; 
	list<path> k_shortest_paths;
	int k <- 3; 
	list<rgb> colors <- [rgb("red"),rgb("green"),rgb("blue"),rgb("pink"),rgb("cyan"),rgb("magenta"),rgb("yellow")];
	bool save_shortest_paths <- true;
	bool load_shortest_paths <- false;
	string shortest_paths_file1 <- "shortest_paths_speed_lanes_2.csv";
	string shortest_paths_file2 <- "shortest_paths_speed_2.csv";
	string shortest_paths_file3 <- "shortest_paths_distance_2.csv";
	string shortest_paths_file4 <- "shortest_paths_traffic_light_2.csv";
	
	
	init {
		
		create node_ from: shape_file_nodes with:[is_traffic_signal::bool(read("signal")), is_crossing :: bool(read("crossing"))];
		create road from: shape_file_roads with:[name::string(read("name")),highway::string(get("highway")),junction::string(read("junction")),lanes::int(read("lanes")), maxspeed::float(read("maxspeed")), oneway::string(read("oneway")), lanes_forward ::int (get( "lanesforwa")), lanes_backward :: int (get("lanesbackw"))];
		
		
		//------------------------------shortest_paths_speed_lanes----------------------------
		map poids <- road as_map (each::(each.shape.perimeter / each.maxspeed / each.lanes ));
		road_graph <-  use_cache(as_driving_graph(road, node_) with_weights (poids), false) with_optimizer_type "static";
		write "Graph 1 built: " + length(road) ;
		write "length(vertices) : " + length(road_graph.vertices);
		
		
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(road_graph);
			write "Matrix of all shortest paths1: " ;
			save ssp type:"text" to:shortest_paths_file1;
			
		} 
		
		loop i from: 0 to: length(road_graph.vertices) - 1 {
			nodeId[road_graph.vertices[i]] <- i; 
		}
		//------------------------------shortest_paths_speed ----------------------------
		poids <- road as_map (each::(each.shape.perimeter / each.maxspeed ));
		road_graph <-  use_cache(road_graph with_weights (poids), false) with_optimizer_type "static";
		
		write "Graph 2 built: " + length(road) ;
		
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(road_graph);
			write "Matrix of all shortest paths2: " ;
			save ssp type:"text" to:shortest_paths_file2;
			
		} 
		
		//------------------------------shortest_paths_distance ----------------------------
		
		poids <- road as_map (each::(each.shape.perimeter));
		road_graph <-  use_cache(road_graph with_weights (poids), false) with_optimizer_type "static";
		
		write "Graph 3 built: " + length(road) ;
		
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(road_graph);
			write "Matrix of all shortest paths3: " ;
			save ssp type:"text" to:shortest_paths_file3;
			
		} 
		
		//------------------------------shortest_paths_traffic_light ----------------------------
		list<road> traffic_signal_road <- (road where (node_(each.target_node).is_traffic_signal));
		
		poids <-  road as_map (each::(each.shape.perimeter / each.maxspeed* ((each in traffic_signal_road) ? 1000 : 1)));
		road_graph <-  use_cache(road_graph with_weights (poids), false) with_optimizer_type "static";
		
		write "Graph 4 built: " + length(road) ;
		
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(road_graph);
			write "Matrix of all shortest paths4: " ;
			save ssp type:"text" to:shortest_paths_file4;
			
		} 
		/*poids <- route as_map (each::(each.shape.perimeter / each.maxspeed));
		road_graph <- directed(as_edge_graph(route) with_weights (poids))with_optimizer_type "static";
		
		//computes all the shortest paths, puts them in a matrix, then saves the matrix in a file
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(road_graph);
			write "Matrix of all shortest paths2: " ;
			save ssp type:"text" to:shortest_paths_file2;
			
		//loads the file of the shortest paths as a matrix and uses it to initialize all the shortest paths of the graph
		} 
		
		poids <- route as_map (each::(each.shape.perimeter));
		road_graph <- directed(as_edge_graph(route) with_weights (poids))with_optimizer_type "static";
		
		//computes all the shortest paths, puts them in a matrix, then saves the matrix in a file
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(road_graph);
			write "Matrix of all shortest paths3: " ;
			save ssp type:"text" to:shortest_paths_file3;
			*/
		//loads the file of the shortest paths as a matrix and uses it to initialize all the shortest paths of the graph
		/*else if load_shortest_paths {
			//road_graph <- road_graph load_shortest_paths matrix(file(shortest_paths_file1));
			file_ssp <- csv_file(shortest_paths_file1,";", (0 as_matrix {length(node_),length(node_)})); 
		} */
		
		
	}
	
}


species node_ skills: [skill_road_node] frequency: 0{
	bool is_traffic_signal;
	int time_to_change_green <- 100;
	int time_to_change_red <- 100;
	int counter ;
	rgb color_centr; 
	rgb color_fire;
	float centrality;	
	bool is_blocked <- false;
	bool is_crossing;
	bool is_green;
	int time_to_change;
	bool is_ok;
	
	aspect default {
		if (not is_ok) {
			draw circle(20) color: #red;
		}
		draw string(int(self)) size: 10 color: #black;
	}
	
}


species road skills: [skill_road] frequency: 0 { 
	string oneway;
	geometry geom_display;
	bool is_blocked <- false;
	int nb_people <- 0;
	int nb_people_tot <- 0;
	
	int lanes_backward;
	int lanes_forward;
	
	float max_embouteillage_vitesse;
	bool traffic_jam <- false;
	int capacite_max ;
	float nb_bloques <- 0.0;
	int min_traffic_jam_people_destroy;
	int min_traffic_jam_people_creation;
	
	road next_linked_road;
	string highway;
	string junction;
	bool is_ok;
	
	aspect default {    
		draw (is_ok ? shape : (shape + 5)) color: (is_ok ? #black : #red );
	}  
	
}



experiment ShortestPath type: gui {
		
	output {
		display map_shortest_path {
			species road refresh: false;
			species node_;
			graphics "shortest path" {
				if (shortest_path != nil) {
					draw circle(5) at:point(source) color: rgb("green");
					draw circle(5) at: point(target) color: rgb("cyan");
					draw (shortest_path.shape + 2.0) color: rgb("magenta");
				}
			}
			
		}
		
	}
}
