/**
 *  OpOpGrid_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpGrid_related__operatorsTest.
 */

model OpGrid_related__operatorsTest

global {
	init {
		create testOpGrid_related__operatorsTest number: 1;
		ask testOpGrid_related__operatorsTest {do _step_;}
	}
}

	species testOpGrid_related__operatorsTest {

	
		test as_4_gridOp {
			matrix var0 <- self as_4_grid {10, 5}; 	// var0 equals the matrix of square geometries (grid with 4-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.

		}
	
		test as_gridOp {
			matrix var0 <- self as_grid {10, 5}; 	// var0 equals a matrix of square geometries (grid with 8-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.

		}
	
		test as_hexagonal_gridOp {
			list<geometry> var0 <- self as_hexagonal_grid {10, 5}; 	// var0 equals list of geometries (hexagonal) corresponding to the hexagonal tesselation of the first operand geometry

		}
	
		test grid_atOp {
			//agent var0 <- grid_cell grid_at {1,2}; 	// var0 equals the agent grid_cell with grid_x=1 and grid_y = 2

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
	
	}


experiment testOpGrid_related__operatorsTestExp type: gui {}	
	