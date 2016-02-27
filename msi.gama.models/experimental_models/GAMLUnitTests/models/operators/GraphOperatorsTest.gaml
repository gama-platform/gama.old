/**
 *  GraphTest
 *  Author: bgaudou
 *  Description: 
 */

model GraphTest

global {
	init {
		create graphOpAgentTest number:1 ;
	}
}

entities {
	species node {}
	species edge {}

	species graphOpAgentTest{
// 		graph graphEpidemio;
		graph graphFromMap;
	
		setUp {
			// set graphEpidemio <- generate_barabasi_albert( ["edges_species"::edge,"vertices_specy"::node,"size"::3,"m"::5] );
			set graphFromMap <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);		
		}
		
		test t1 {
			assert value: (graphFromMap contains_vertex {1,5}) equals: true;
			assert value: (graphFromMap contains_vertex {1,0}) equals: false;		
		}
		
		test t2 {
			// graphEpidemio <- node(0) remove_node_from graphEpidemio;
			// assert value: (graphEpidemio weight_of(edge(3))) equals: 5;
		}
	}
}

experiment GraphTest type: gui {}
