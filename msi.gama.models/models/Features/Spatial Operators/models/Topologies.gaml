/**
 *  gridfilter
 *  Author: administrateur
 *  Description: 
 */

model gridfilter

global {
	graph the_graph <- spatial_graph([]); 
	init {
		do create_dummy_agents;
		do create_graph;
 

		
		//do test_agents_at_distance;
		//do test_distance_between;
		do test_distance_to;
		//do test_at_distance;
		//do test_neighbours_at;
		//do test_neighbours_of;
	/*do test_path_between;
		do test_path_to;*/
		//do test_simple_clustering_by_distance;
		//do test_hierarchical_clustering;
		/*do test_agent_closest_to; */	
	}  
	
	action test_agent_closest_to {
		write "\n************** AGENT_CLOSEST_TO **************";
		write "CONTINUOUS TOPOLOGY : agent closest to dummy 8: " + (dummy closest_to (dummy(8))).id;
		using(topology(cell)) {
			write "GRID TOPOLOGY : agent closest to dummy 8: " + (dummy closest_to (dummy(8))).id;
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : agent closest to dummy 8: " + (dummy closest_to (dummy(8))).id;
		}
		using(topology(world)) {
			write "CONTINUOUS TOPOLOGY : agent closest to cell 40: " + (cell closest_to (cell(40)));
		}
		using(topology(cell)) {
			write "GRID TOPOLOGY : agent closest to cell 40: " + (cell closest_to (cell(40)));
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY  : agent closest to cell 40: " + (cell closest_to (cell(40)));
		}
	}
	action test_agents_at_distance {
		write "\n************** AGENTS_AT_DISTANCE **************";
		ask dummy(8) {
			write "CONTINUOUS TOPOLOGY : agents at distance 20 of dummy 8 : " + length(agents_at_distance(20)  of_species cell);
			using(topology(cell)) {
				write "GRID TOPOLOGY : agents at distance 2 of dummy 8: " + length(agents_at_distance(2) of_species cell);
			}
			using(topology(cell)) {
				write "GRAPH TOPOLOGY : agents at distance 20 of dummy 8: " + agents_at_distance(20);
			}	
		}
		ask cell(40) {
			using(topology(world)) {
				write "CONTINUOUS TOPOLOGY : agents at distance 10 of cell 40 : " + agents_at_distance(10) ;
			}
			using(topology(cell)) {
				write "GRID TOPOLOGY : agents at distance 1 of cell 40: " + agents_at_distance(1);
			}
			using(topology(cell)) {
				write "GRAPH TOPOLOGY : agents at distance 20 of cell 40: " + agents_at_distance(20);
			}	
		} 
	}
	
	action test_at_distance {
		write "\n************** AT_DISTANCE **************";
		ask dummy(8) {
			write "CONTINUOUS TOPOLOGY : agents at distance 20 of dummy 8: " + ((dummy at_distance 20) collect (each.id));
			using(topology(cell)) {
				write "GRID TOPOLOGY : agents at distance 2 of dummy 8: " + length((cell at_distance 2) of_species cell);
			}
			using(topology(the_graph)) {
				write "GRAPH TOPOLOGY : agents at distance 30 of dummy 8: " + ((dummy at_distance 30) collect (each.id));
			}
		}
		ask cell(40) {
			using(topology(world)) {
				write "CONTINUOUS TOPOLOGY : agents at distance 10 of cell 40: " + (cell at_distance 10);
			}
			using(topology(cell)) {
				write "GRID TOPOLOGY : agents at distance 1 of cell 40: " + (cell at_distance 1);
			}
			using(topology(the_graph)) {
				write "GRAPH TOPOLOGY : agents at distance 20 of cell 40: " + (cell at_distance 20);
				ask (cell at_distance 20) {color <- #pink;}
			}
		}
	}
	action test_neighbours_at {
		write "\n************** NEIGHBOURS_AT **************";
		write "CONTINUOUS TOPOLOGY :neighbours at 20  of dummy 8: " + ((dummy(8) neighbours_at 20) collect (each.id));
 		using(topology(cell)) { 
			write "GRID TOPOLOGY : neighbours at 2 of dummy 8: "+ ((dummy(8) neighbours_at 2) collect (each.id));
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : neighbours at 20 of dummy 8: " + ((dummy(8) neighbours_at 20) collect (each.id));
		}
		using(topology(world)) { 
			write "CONTINUOUS TOPOLOGY :neighbours at 10  of cell 40: " + (cell(40) neighbours_at 10);
 		}
 		using(topology(cell)) { 
			write "GRID TOPOLOGY : neighbours at 1 of cell 40: "+ (cell(40) neighbours_at 1);
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : neighbours at 20 of cell 40: "+ (cell(40) neighbours_at 20);
		}
	}
	
	action test_neighbours_of{
		write "\n************** NEIGHBOURS_OF **************";
		write "CONTINUOUS TOPOLOGY : neighbours of dummy 8 at distance 20: " + ((topology(world) neighbours_of (dummy(8) ,20)));
		write "GRID TOPOLOGY : neighbours of dummy 8 at distance 2: " + ((topology(cell) neighbours_of (dummy(8) ,2)));
		write "GRAPH TOPOLOGY: neighbours of dummy 8 at distance 20: " + ((topology(the_graph) neighbours_of (dummy(8) ,20)));
		
		write "CONTINUOUS TOPOLOGY : neighbours of cell 40 at distance 20: " + ((topology(world) neighbours_of (cell(40) ,10)));
		write "GRID TOPOLOGY : neighbours of cell 40 at distance 2: " + ((topology(cell) neighbours_of (cell(40) ,1)));
		write "GRAPH TOPOLOGY: neighbours of cell 40 at distance 20: " + ((topology(the_graph) neighbours_of (cell(40) ,10)));
	}
	
	
	action test_distance_between {
		write "\n************** DISTANCE_BETWEEN **************";
		write "CONTINUOUS TOPOLOGY : distance between dummy 8 and dummy 3: " + (topology(world) distance_between [dummy(8) ,dummy(3)]);
		write "GRID TOPOLOGY : distance between dummy 8 and dummy 3: " + (topology(cell) distance_between [dummy(8) ,dummy(3)]);
		write "GRAPH TOPOLOGY :distance between dummy 8 and dummy 3: "+ (topology(the_graph) distance_between [dummy(8) ,dummy(3)]);
		write "CONTINUOUS TOPOLOGY : distance between cell 10 and cell 60: " + (topology(world) distance_between [cell(10) ,cell(60)]);
		write "GRID TOPOLOGY : distance between cell 10 and cell 60: " + (topology(cell) distance_between [cell(10) ,cell(60)]);
		write "GRAPH TOPOLOGY :distance between cell 10 and cell 60: "+ (topology(the_graph) distance_between [cell(10) ,cell(60)]);
	}
	
	action test_distance_to {
		write "\n************** DISTANCE_TO **************";
		write "CONTINUOUS TOPOLOGY : dummy 8 distance to dummy 3: " + (dummy(8) distance_to dummy(3));
		using(topology(cell)) {
			write "GRID TOPOLOGY : dummy 8 distance to dummy 3: " + (dummy(8) distance_to dummy(3));
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : dummy 8 distance to dummy 3: "+ (dummy(8) distance_to dummy(3));
		}
		using(topology(world)) {
			write "CONTINUOUS TOPOLOGY : cell 10 distance to cell 27: " + (cell(10) distance_to cell(27));
		}
		using(topology(cell)) {
			write "GRID TOPOLOGY : cell 10 distance to cell 27: " + (cell(10) distance_to cell(27));
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : cell 10 distance to cell 60: " + (cell(10) distance_to cell(60));
			
			write "GRID TOPOLOGY : cell 40 distance to cell 50: " + (cell(40) distance_to cell(50));
			write "GRID TOPOLOGY : cell 40 distance to cell 60: " + (cell(40) distance_to cell(60));
			write "GRID TOPOLOGY : cell 40 distance to cell 61: " + (cell(40) distance_to cell(61));
		}
	}
	action test_path_between {
		write "\n************** PATH_BETWEEN **************";
		write "CONTINUOUS TOPOLOGY : path between dummy 8 and dummy 3: " + (topology(world) path_between [dummy(8) ,dummy(3)]).edges;
		write "GRID TOPOLOGY : path between dummy 8 and dummy 3: " + (topology(cell) path_between [dummy(8) ,dummy(3)]).edges;
		write "GRAPH TOPOLOGY :path between dummy 8 and dummy 3: "+ (topology(the_graph) path_between [dummy(8) ,dummy(3)]).edges;
	}
	
	action test_path_to {
		write "\n************** PATH_TO **************";
		write "CONTINUOUS TOPOLOGY : dummy 8 path to dummy 3: " + (dummy(8) path_to dummy(3)).edges;
		using(topology(cell)) {
			write "GRID TOPOLOGY : dummy 8 path to dummy 3: " + (dummy(8) path_to dummy(3)).edges;
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : dummy 8 path to dummy 3: "+ (dummy(8) path_to dummy(3)).edges;
		}
	}
	action test_simple_clustering_by_distance {
		write "\n************** SIMPLE_CLUSTERING_BY_DISTANCE **************";
		write "CONTINUOUS TOPOLOGY : groups (distance 20): " + dummy simple_clustering_by_distance 20;
		using(topology(cell)) {
			write "GRID TOPOLOGY : groups (distance 1): " + dummy simple_clustering_by_distance 1;
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : groups (distance 20): " + dummy simple_clustering_by_distance 20;
		}
	}
	action test_hierarchical_clustering {
		write "\n************** HIERARCHICAL_CLUSTERING **************";
		write "CONTINUOUS TOPOLOGY : dendrogram (distance 20): " + dummy hierarchical_clustering 20;
		using(topology(cell)) {
			write "GRID TOPOLOGY : dendrogram (distance 1): " + dummy hierarchical_clustering 1;
		}
		using(topology(the_graph)) {
			write "GRAPH TOPOLOGY : dendrogram (distance 20): " + dummy hierarchical_clustering 20;
		}
	}
	action create_dummy_agents {
		create dummy with: [location :: {5,5}];
		create dummy with: [location :: {8,9}];
		create dummy with: [location :: {14,6}];
		create dummy with: [location :: {35,55}];
		create dummy with: [location :: {25,75}];
		create dummy with: [location :: {56,80}];
		create dummy with: [location :: {10,70}];
		create dummy with: [location :: {80,8}];
		create dummy with: [location :: {34,78}];
		create dummy with: [location :: {67,32}];
		loop i from: 0 to: length(dummy) - 1 {
			ask dummy[i] {id <- string(i);}
		}
	}
	
	action create_graph {
		add edge:(dummy[0]::dummy[1]) to: the_graph;
		add edge:(dummy[1]::dummy[2]) to: the_graph;
		add edge:(dummy[0]::dummy[2]) to: the_graph;
		add edge:(dummy[2]::dummy(3)) to: the_graph;
		add edge:(dummy(3)::dummy[4]) to: the_graph;
		add edge:(dummy(3)::dummy[5]) to: the_graph;
		
		add edge:(dummy(3)::dummy[6]) to: the_graph;
		add edge:(dummy[9]::dummy[7]) to: the_graph;
		add edge:(dummy[5]::dummy(8)) to: the_graph;
		add edge:(dummy[5]::dummy[9]) to: the_graph;
	}
}

grid cell width: 10 height: 10 neighbours: 4{
	rgb color <- #green;
}

species dummy {
	string id;
	init {
		add node: self to: the_graph;
	}
	aspect default {
		draw circle(2) color: #yellow;
		draw id size: 6 color: #black;
	}
}
experiment topology_test type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display main_display  {
			grid cell lines: #black;
			species dummy;
			graphics graph{
				loop edg over: the_graph.edges {
					draw edg color: #red;	
				}
			}
		}
	}
}
