/**
 *  OpOpSpatial__relations__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSpatial__relations__operatorsTest.
 */

model OpSpatial__relations__operatorsTest

global {
	init {
		create testOpSpatial__relations__operatorsTest number: 1;
		ask testOpSpatial__relations__operatorsTest {do _step_;}
	}
}


	species testOpSpatial__relations__operatorsTest {

	
		test direction_betweenOp {
			//int var0 <- my_topology direction_between [ag1, ag2]; 	// var0 equals the direction between ag1 and ag2 considering the topology my_topology

		}
	
		test direction_toOp {

		}
	
		test distance_betweenOp {
			//float var0 <- my_topology distance_between [ag1, ag2, ag3]; 	// var0 equals the distance between ag1, ag2 and ag3 considering the topology my_topology

		}
	
		test distance_toOp {
			//float var0 <- ag1 distance_to ag2; 	// var0 equals the distance between ag1 and ag2 considering the topology of the agent applying the operator

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
	
		test towardsOp {
			//int var0 <- ag1 towards ag2; 	// var0 equals the direction between ag1 and ag2 and ag3 considering the topology of the agent applying the operator

		}
	
	}


experiment testOpSpatial__relations__operatorsTestExp type: gui {}	
	