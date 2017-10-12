/**
 *  OpOpMap_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpMap_related__operatorsTest.
 */

model OpMap_related__operatorsTest

global {
	init {
		create testOpMap_related__operatorsTest number: 1;
		ask testOpMap_related__operatorsTest {do _step_;}
	}
}


	species testOpMap_related__operatorsTest {

	
		test as_mapOp {
			map<int,int> var0 <- [1,2,3,4,5,6,7,8] as_map (each::(each * 2)); 	// var0 equals [1::2, 2::4, 3::6, 4::8, 5::10, 6::12, 7::14, 8::16]
			assert var0 equals: [1::2, 2::4, 3::6, 4::8, 5::10, 6::12, 7::14, 8::16]; 
			map<int,int> var1 <- [1::2,3::4,5::6] as_map (each::(each * 2)); 	// var1 equals [2::4, 4::8, 6::12] 
			assert var1 equals: [2::4, 4::8, 6::12] ; 

		}
	
		test create_mapOp {
			map<int,string> var0 <- create_map([0,1,2],['a','b','c']); 	// var0 equals [0::'a',1::'b',2;;'c']
			assert var0 equals: [0::'a',1::'b',2;;'c']; 
			map<int,float> var1 <- create_map([0,1],[0.1,0.2,0.3]); 	// var1 equals [0::0.1,1::0.2]
			assert var1 equals: [0::0.1,1::0.2]; 
			map<string,float> var2 <- create_map(['a','b','c','d'],[1.0,2.0,3.0]); 	// var2 equals ['a'::1.0,'b'::2.0,'c'::3.0]
			assert var2 equals: ['a'::1.0,'b'::2.0,'c'::3.0]; 

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

experiment testOpMap_related__operatorsTestExp type: gui {}	
	