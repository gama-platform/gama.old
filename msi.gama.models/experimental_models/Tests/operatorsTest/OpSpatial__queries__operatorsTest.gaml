/**
 *  OpOpSpatial__queries__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSpatial__queries__operatorsTest.
 */

model OpSpatial__queries__operatorsTest

global {
	init {
		create testOpSpatial__queries__operatorsTest number: 1;
		ask testOpSpatial__queries__operatorsTest {do _step_;}
	}
}


	species testOpSpatial__queries__operatorsTest {

	
		test agent_closest_toOp {
			agent var0 <- agent_closest_to(self); 	// var0 equals the closest agent to the agent applying the operator.

		}
	
		test agent_farthest_toOp {
			agent var0 <- agent_farthest_to(self); 	// var0 equals the farthest agent to the agent applying the operator.

		}
	
		test agents_at_distanceOp {
			container var0 <- agents_at_distance(20); 	// var0 equals all the agents (excluding the caller) which distance to the caller is lower than 20

		}
	
		test agents_insideOp {
			list<agent> var0 <- agents_inside(self); 	// var0 equals the agents that are covered by the shape of the agent applying the operator.

		}
	
		test agents_overlappingOp {
			list<agent> var0 <- agents_overlapping(self); 	// var0 equals the agents that overlap the shape of the agent applying the operator.

		}
	
		test at_distanceOp {
			//list<geometry> var0 <- [ag1, ag2, ag3] at_distance 20; 	// var0 equals the agents of the list located at a distance <= 20 from the caller agent (in the same order).

		}
	
		test closest_toOp {
			//geometry var0 <- [ag1, ag2, ag3] closest_to(self); 	// var0 equals return the closest agent among ag1, ag2 and ag3 to the agent applying the operator.
			//(species1 + species2) closest_to self

		}
	
		test farthest_toOp {
			//geometry var0 <- [ag1, ag2, ag3] closest_to(self); 	// var0 equals return the farthest agent among ag1, ag2 and ag3 to the agent applying the operator.
			//(species1 + species2) closest_to self

		}
	
		test insideOp {
			//list<geometry> var0 <- [ag1, ag2, ag3] inside(self); 	// var0 equals the agents among ag1, ag2 and ag3 that are covered by the shape of the right-hand argument.
			//list<geometry> var1 <- (species1 + species2) inside (self); 	// var1 equals the agents among species species1 and species2 that are covered by the shape of the right-hand argument.

		}
	
		test neighbors_atOp {
			container var0 <- (self neighbors_at (10)); 	// var0 equals all the agents located at a distance lower or equal to 10 to the agent applying the operator.

		}
	
		test neighbors_ofOp {
			container var3 <- neighbors_of (topology(self), self,10); 	// var3 equals all the agents located at a distance lower or equal to 10 to the agent applying the operator considering its topology.
			//container var0 <- graphEpidemio neighbors_of (node(3)); 	// var0 equals [node0,node2]
			//container var1 <- graphFromMap neighbors_of node({12,45}); 	// var1 equals [{1.0,5.0},{34.0,56.0}]
			container var2 <- topology(self) neighbors_of self; 	// var2 equals returns all the agents located at a distance lower or equal to 1 to the agent applying the operator considering its topology.

		}
	
		test overlappingOp {
			//list<geometry> var0 <- [ag1, ag2, ag3] overlapping(self); 	// var0 equals return the agents among ag1, ag2 and ag3 that overlap the shape of the agent applying the operator.
			//(species1 + species2) overlapping self

		}
	
	}


experiment testOpSpatial__queries__operatorsTestExp type: gui {}	
	