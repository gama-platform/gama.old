/**
 *  OpOpPath_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpPath_related__operatorsTest.
 */

model OpPath_related__operatorsTest

global {
	init {
		create testOpPath_related__operatorsTest number: 1;
		ask testOpPath_related__operatorsTest {do _step_;}
	}
}


	species testOpPath_related__operatorsTest {

	
		test agent_from_geometryOp {
			//geometry line <- one_of(path_followed.segments);
			//road ag <- road(path_followed agent_from_geometry line);

		}
	
		test all_pairs_shortest_pathOp {
			//matrix<int> var0 <- all_pairs_shortest_paths(my_graph); 	// var0 equals shortest_paths_matrix will contain all pairs of shortest paths

		}
	
		test as_pathOp {
			//path var0 <- [road1,road2,road3] as_path my_graph; 	// var0 equals a path road1->road2->road3 of my_graph

		}
	
		test load_shortest_pathsOp {
			//graph var0 <- load_shortest_paths(shortest_paths_matrix); 	// var0 equals return my_graph with all the shortest paths computed

		}
	
		test path_betweenOp {
			//path var0 <- path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), [ag1, ag2, ag3]); 	// var0 equals A path between ag1 and ag2 and ag3 passing through the given cell_grid agents with minimal cost
			//path var1 <- my_topology path_between [ag1, ag2]; 	// var1 equals A path between ag1 and ag2
			//path var2 <- path_between (cell_grid where each.is_free, ag1, ag2); 	// var2 equals A path between ag1 and ag2 passing through the given cell_grid agents
			//path var3 <- my_topology path_between (ag1, ag2); 	// var3 equals A path between ag1 and ag2
			//path var4 <- path_between (my_graph, ag1, ag2); 	// var4 equals A path between ag1 and ag2
			//path var5 <- path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), ag1, ag2); 	// var5 equals A path between ag1 and ag2 passing through the given cell_grid agents with a minimal cost
			//path var6 <- path_between (cell_grid where each.is_free, [ag1, ag2, ag3]); 	// var6 equals A path between ag1 and ag2 and ag3 passing through the given cell_grid agents

		}
	
		test path_toOp {
			//path var0 <- ag1 path_to ag2; 	// var0 equals the path between ag1 and ag2 considering the topology of the agent applying the operator

		}
	
		test paths_betweenOp {
			//msi.gama.util.IList<msi.gama.util.path.GamaSpatialPath> var0 <- paths_between(my_graph, ag1:: ag2, 2); 	// var0 equals the 2 shortest paths (ordered by length) between ag1 and ag2

		}
	
		test use_cacheOp {

		}
	
	}

experiment testOpPath_related__operatorsTestExp type: gui {}	
	