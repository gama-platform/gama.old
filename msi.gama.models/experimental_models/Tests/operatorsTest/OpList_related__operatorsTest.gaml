/**
 *  OpOpList_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpList_related__operatorsTest.
 */

model OpList_related__operatorsTest

global {
	init {
		create testOpList_related__operatorsTest number: 1;
		ask testOpList_related__operatorsTest {do _step_;}
	}
}

	species testOpList_related__operatorsTest {

	
		test copy_betweenOp {
			string var0 <- copy_between("abcabcabc", 2,6); 	// var0 equals "cabc"
			assert var0 equals: "cabc"; 
			container var1 <-  copy_between ([4, 1, 6, 9 ,7], 1, 3); 	// var1 equals [1, 6]
			assert var1 equals: [1, 6]; 

		}
	
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
	
	}


experiment testOpList_related__operatorsTestExp type: gui {}	
	