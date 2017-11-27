/**
 *  OpOpedgeTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpedgeTest.
 */

model OpedgeTest

global {
	init {
		create testOpedgeTest number: 1;
		ask testOpedgeTest {do _step_;}
	}
}

	species testOpedgeTest {

	
		test edge_betweenOp {
			//unknown var0 <- graphFromMap edge_between node1::node2; 	// var0 equals edge1

		}
	
		test strahlerOp {

		}
	
	}

experiment testOpedgeTestExp type: gui {}	
	