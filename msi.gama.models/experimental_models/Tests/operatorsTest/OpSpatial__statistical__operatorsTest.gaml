/**
 *  OpOpSpatial__statistical__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSpatial__statistical__operatorsTest.
 */

model OpSpatial__statistical__operatorsTest

global {
	init {
		create testOpSpatial__statistical__operatorsTest number: 1;
		ask testOpSpatial__statistical__operatorsTest {do _step_;}
	}
}


	species testOpSpatial__statistical__operatorsTest {

	
		test hierarchical_clusteringOp {
			//container var0 <- [ag1, ag2, ag3, ag4, ag5] hierarchical_clustering 20.0; 	// var0 equals for example, can return [[[ag1],[ag3]], [ag2], [[[ag4],[ag5]],[ag6]]

		}
	
		test simple_clustering_by_distanceOp {
			//list<list<agent>> var0 <- [ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0; 	// var0 equals for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]

		}
	
		test simple_clustering_by_envelope_distanceOp {

		}
	
	}


experiment testOpSpatial__statistical__operatorsTestExp type: gui {}	
	