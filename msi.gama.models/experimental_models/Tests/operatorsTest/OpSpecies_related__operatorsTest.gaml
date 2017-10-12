/**
 *  OpOpSpecies_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSpecies_related__operatorsTest.
 */

model OpSpecies_related__operatorsTest

global {
	init {
		create testOpSpecies_related__operatorsTest number: 1;
		ask testOpSpecies_related__operatorsTest {do _step_;}
	}
}


	species testOpSpecies_related__operatorsTest {

	
		test index_ofOp {
			int var1 <-  "abcabcabc" index_of "ca"; 	// var1 equals 2
			assert var1 equals: 2; 
			point var2 <- matrix([[1,2,3],[4,5,6]]) index_of 4; 	// var2 equals {1.0,0.0}
			assert var2 equals: {1.0,0.0}; 
			int var3 <- [1,2,3,4,5,6] index_of 4; 	// var3 equals 3
			assert var3 equals: 3; 
			int var4 <- [4,2,3,4,5,4] index_of 4; 	// var4 equals 0
			assert var4 equals: 0; 
			unknown var0 <- [1::2, 3::4, 5::6] index_of 4; 	// var0 equals 3
			assert var0 equals: 3; 

		}
	
		test last_index_ofOp {
			int var0 <- "abcabcabc" last_index_of "ca"; 	// var0 equals 5
			assert var0 equals: 5; 
			point var1 <- matrix([[1,2,3],[4,5,4]]) last_index_of 4; 	// var1 equals {1.0,2.0}
			assert var1 equals: {1.0,2.0}; 
			int var2 <- [1,2,3,4,5,6] last_index_of 4; 	// var2 equals 3
			assert var2 equals: 3; 
			int var3 <- [4,2,3,4,5,4] last_index_of 4; 	// var3 equals 5
			assert var3 equals: 5; 
			unknown var4 <- [1::2, 3::4, 5::4] last_index_of 4; 	// var4 equals 5
			assert var4 equals: 5; 

		}
	
		test of_generic_speciesOp {
			// species test {}
			// species sous_test parent: test {}
			//container var2 <- [sous_test(0),sous_test(1),test(2),test(3)] of_generic_species test; 	// var2 equals [sous_test0,sous_test1,test2,test3]
			//container var3 <- [sous_test(0),sous_test(1),test(2),test(3)] of_generic_species sous_test; 	// var3 equals [sous_test0,sous_test1]
			//container var4 <- [sous_test(0),sous_test(1),test(2),test(3)] of_species test; 	// var4 equals [test2,test3]
			//container var5 <- [sous_test(0),sous_test(1),test(2),test(3)] of_species sous_test; 	// var5 equals [sous_test0,sous_test1]

		}
	
		test of_speciesOp {
			//container var0 <- (self neighbors_at 10) of_species (species (self)); 	// var0 equals all the neighboring agents of the same species.
			//container var1 <- [test(0),test(1),node(1),node(2)] of_species test; 	// var1 equals [test0,test1]

		}
	
	}


experiment testOpSpecies_related__operatorsTestExp type: gui {}	
	