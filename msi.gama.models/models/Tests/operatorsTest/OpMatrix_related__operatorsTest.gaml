/**
 *  OpOpMatrix_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpMatrix_related__operatorsTest.
 */

model OpMatrix_related__operatorsTest

global {
	init {
		create testOpMatrix_related__operatorsTest number: 1;
		ask testOpMatrix_related__operatorsTest {do _step_;}
	}
}

	species testOpMatrix_related__operatorsTest {

	
		test append_horizontallyOp {
			matrix var0 <- matrix([[1.0,2.0],[3.0,4.0]]) append_horizontally matrix([[1,2],[3,4]]); 	// var0 equals matrix([[1.0,2.0],[3.0,4.0],[1.0,2.0],[3.0,4.0]])
			assert var0 equals: matrix([[1.0,2.0],[3.0,4.0],[1.0,2.0],[3.0,4.0]]); 

		}
	
		test append_verticallyOp {
			matrix var0 <- matrix([[1,2],[3,4]]) append_vertically matrix([[1,2],[3,4]]); 	// var0 equals matrix([[1,2,1,2],[3,4,3,4]])
			assert var0 equals: matrix([[1,2,1,2],[3,4,3,4]]); 

		}
	
		test column_atOp {
			list var0 <- matrix([["el11","el12","el13"],["el21","el22","el23"],["el31","el32","el33"]]) column_at 2; 	// var0 equals ["el31","el32","el33"]
			assert var0 equals: ["el31","el32","el33"]; 

		}
	
		test columns_listOp {
			list<list> var0 <- columns_list(matrix([["el11","el12","el13"],["el21","el22","el23"],["el31","el32","el33"]])); 	// var0 equals [["el11","el12","el13"],["el21","el22","el23"],["el31","el32","el33"]]
			assert var0 equals: [["el11","el12","el13"],["el21","el22","el23"],["el31","el32","el33"]]; 

		}
	
		test detOp {

		}
	
		test determinantOp {
			float var0 <- determinant(matrix([[1,2],[3,4]])); 	// var0 equals -2
			assert var0 equals: -2; 

		}
	
		test DivideOp {
			rgb var0 <- rgb([255, 128, 32]) / 2; 	// var0 equals rgb([127,64,16])
			assert var0 equals: rgb([127,64,16]); 
			rgb var1 <- rgb([255, 128, 32]) / 2.5; 	// var1 equals rgb([102,51,13])
			assert var1 equals: rgb([102,51,13]); 
			float var2 <- 3 / 5.0; 	// var2 equals 0.6
			assert var2 equals: 0.6; 
			point var3 <- {5, 7.5} / 2.5; 	// var3 equals {2, 3}
			assert var3 equals: {2, 3}; 
			point var4 <- {2,5} / 4; 	// var4 equals {0.5,1.25}
			assert var4 equals: {0.5,1.25}; 

		}
	
		test eigenvaluesOp {
			list<float> var0 <- eigenvalues(matrix([[5,-3],[6,-4]])); 	// var0 equals [2.0000000000000004,-0.9999999999999998]
			assert var0 equals: [2.0000000000000004,-0.9999999999999998]; 

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
	
		test inverseOp {
			matrix<float> var0 <- inverse(matrix([[5,-3],[6,-4]])); 	// var0 equals [2.0000000000000004,-0.9999999999999998]
			assert var0 equals: [2.0000000000000004,-0.9999999999999998]; 

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
	
		test MinusOp {
			rgb var7 <- rgb([255, 128, 32]) - 3; 	// var7 equals rgb([252,125,29])
			assert var7 equals: rgb([252,125,29]); 
			rgb var8 <- rgb([255, 128, 32]) - rgb('red'); 	// var8 equals rgb([0,128,32])
			assert var8 equals: rgb([0,128,32]); 
			int var9 <- 1 - 1; 	// var9 equals 0
			assert var9 equals: 0; 
			int var10 <- 1.0 - 1; 	// var10 equals 0.0
			assert var10 equals: 0.0; 
			int var11 <- 3.7 - 1.2; 	// var11 equals 2.5
			assert var11 equals: 2.5; 
			int var12 <- 3 - 1.2; 	// var12 equals 1.8
			assert var12 equals: 1.8; 
			point var13 <- {1, 2} - {4, 5}; 	// var13 equals {-3.0, -3.0}
			assert var13 equals: {-3.0, -3.0}; 
			list<int> var14 <- [1,2,3,4,5,6] - [2,4,9]; 	// var14 equals [1,3,5,6]
			assert var14 equals: [1,3,5,6]; 
			list<int> var15 <- [1,2,3,4,5,6] - [0,8]; 	// var15 equals [1,2,3,4,5,6]
			assert var15 equals: [1,2,3,4,5,6]; 
			matrix var16 <- 3.5 - matrix([[2,5],[3,4]]); 	// var16 equals matrix([[1.5,-1.5],[0.5,-0.5]])
			assert var16 equals: matrix([[1.5,-1.5],[0.5,-0.5]]); 
			date1 - 200
			geometry var18 <- shape - 5; 	// var18 equals a geometry corresponding to the geometry of the agent applying the operator reduced by a distance of 5
			point var19 <- {1, 2} - 4.5; 	// var19 equals {-3.5, -2.5, -4.5}
			assert var19 equals: {-3.5, -2.5, -4.5}; 
			point var20 <- {1, 2} - 4; 	// var20 equals {-3.0,-2.0,-4.0}
			assert var20 equals: {-3.0,-2.0,-4.0}; 
			float var21 <- date1 - date2; 	// var21 equals 598
			assert var21 equals: 598; 
			list<int> var22 <- [1,2,3,4,5,6] - 2; 	// var22 equals [1,3,4,5,6]
			assert var22 equals: [1,3,4,5,6]; 
			list<int> var23 <- [1,2,3,4,5,6] - 0; 	// var23 equals [1,2,3,4,5,6]
			assert var23 equals: [1,2,3,4,5,6]; 
			geometry var24 <- rectangle(10,10) - [circle(2), square(2)]; 	// var24 equals rectangle(10,10) - (circle(2) + square(2))
			assert var24 equals: rectangle(10,10) - (circle(2) + square(2)); 
			//geometry var25 <- geom1 - geom2; 	// var25 equals a geometry corresponding to difference between geom1 and geom2
			map var0 <- ['a'::1,'b'::2] - ['b'::2]; 	// var0 equals ['a'::1]
			assert var0 equals: ['a'::1]; 
			map var1 <- ['a'::1,'b'::2] - ['b'::2,'c'::3]; 	// var1 equals ['a'::1]
			assert var1 equals: ['a'::1]; 
			map var2 <- ['a'::1,'b'::2] - ('b'::2); 	// var2 equals ['a'::1]
			assert var2 equals: ['a'::1]; 
			map var3 <- ['a'::1,'b'::2] - ('c'::3); 	// var3 equals ['a'::1,'b'::2]
			assert var3 equals: ['a'::1,'b'::2]; 
			point var4 <- -{3.0,5.0}; 	// var4 equals {-3.0,-5.0}
			assert var4 equals: {-3.0,-5.0}; 
			point var5 <- -{1.0,6.0,7.0}; 	// var5 equals {-1.0,-6.0,-7.0}
			assert var5 equals: {-1.0,-6.0,-7.0}; 
			int var6 <- - (-56); 	// var6 equals 56
			assert var6 equals: 56; 

		}
	
		test MultiplyOp {
			point var1 <- {2,5} * 4; 	// var1 equals {8.0, 20.0}
			assert var1 equals: {8.0, 20.0}; 
			point var2 <- {2, 4} * 2.5; 	// var2 equals {5.0, 10.0}
			assert var2 equals: {5.0, 10.0}; 
			rgb var3 <- rgb([255, 128, 32]) * 2; 	// var3 equals rgb([255,255,64])
			assert var3 equals: rgb([255,255,64]); 
			matrix<float> m <- (3.5 * matrix([[2,5],[3,4]]));	//m equals matrix([[7.0,17.5],[10.5,14]])
			geometry var5 <- shape * {0.5,0.5,2}; 	// var5 equals a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 0.5 in x, 0.5 in y and 2 in z
			int var6 <- 1 * 1; 	// var6 equals 1
			assert var6 equals: 1; 
			float var7 <- {2,5} * {4.5, 5}; 	// var7 equals 34.0
			assert var7 equals: 34.0; 
			geometry var8 <- circle(10) * 2; 	// var8 equals circle(20)
			assert var8 equals: circle(20); 
			float var0 <- 2.5 * 2; 	// var0 equals 5.0
			assert var0 equals: 5.0; 

		}
	
		test PlusOp {
			point var4 <- {1, 2} + 4; 	// var4 equals {5.0, 6.0,4.0}
			assert var4 equals: {5.0, 6.0,4.0}; 
			point var5 <- {1, 2} + 4.5; 	// var5 equals {5.5, 6.5,4.5}
			assert var5 equals: {5.5, 6.5,4.5}; 
			string var6 <- "hello " + 12; 	// var6 equals "hello 12"
			assert var6 equals: "hello 12"; 
			list<int> var7 <- [1,2,3,4,5,6] + 2; 	// var7 equals [1,2,3,4,5,6,2]
			assert var7 equals: [1,2,3,4,5,6,2]; 
			list<int> var8 <- [1,2,3,4,5,6] + 0; 	// var8 equals [1,2,3,4,5,6,0]
			assert var8 equals: [1,2,3,4,5,6,0]; 
			geometry var9 <- circle(5) + (5,32); 	// var9 equals circle(10)
			assert var9 equals: circle(10); 
			//geometry var10 <- geom1 + geom2; 	// var10 equals a geometry corresponding to union between geom1 and geom2
			date1 + 200
			matrix var12 <- 3.5 + matrix([[2,5],[3,4]]); 	// var12 equals matrix([[5.5,8.5],[6.5,7.5]])
			assert var12 equals: matrix([[5.5,8.5],[6.5,7.5]]); 
			point var13 <- {1, 2} + {4, 5}; 	// var13 equals {5.0, 7.0}
			assert var13 equals: {5.0, 7.0}; 
			rgb var14 <- rgb([255, 128, 32]) + 3; 	// var14 equals rgb([255,131,35])
			assert var14 equals: rgb([255,131,35]); 
			int var15 <- 1 + 1; 	// var15 equals 2
			assert var15 equals: 2; 
			int var16 <- 1.0 + 1; 	// var16 equals 2.0
			assert var16 equals: 2.0; 
			int var17 <- 1.0 + 2.5; 	// var17 equals 3.5
			assert var17 equals: 3.5; 
			geometry var18 <- circle(5) + 5; 	// var18 equals circle(10)
			assert var18 equals: circle(10); 
			geometry var19 <- circle(5) + (5,32,#round); 	// var19 equals circle(10)
			assert var19 equals: circle(10); 
			list<int> var20 <- [1,2,3,4,5,6] + [2,4,9]; 	// var20 equals [1,2,3,4,5,6,2,4,9]
			assert var20 equals: [1,2,3,4,5,6,2,4,9]; 
			list<int> var21 <- [1,2,3,4,5,6] + [0,8]; 	// var21 equals [1,2,3,4,5,6,0,8]
			assert var21 equals: [1,2,3,4,5,6,0,8]; 
			rgb var22 <- rgb([255, 128, 32]) + rgb('red'); 	// var22 equals rgb([255,128,32])
			assert var22 equals: rgb([255,128,32]); 
			map var0 <- ['a'::1,'b'::2] + ['c'::3]; 	// var0 equals ['a'::1,'b'::2,'c'::3]
			assert var0 equals: ['a'::1,'b'::2,'c'::3]; 
			map var1 <- ['a'::1,'b'::2] + [5::3.0]; 	// var1 equals ['a'::1.0,'b'::2.0,5::3.0]
			assert var1 equals: ['a'::1.0,'b'::2.0,5::3.0]; 
			map var2 <- ['a'::1,'b'::2] + ('c'::3); 	// var2 equals ['a'::1,'b'::2,'c'::3]
			assert var2 equals: ['a'::1,'b'::2,'c'::3]; 
			map var3 <- ['a'::1,'b'::2] + ('c'::3); 	// var3 equals ['a'::1,'b'::2,'c'::3]
			assert var3 equals: ['a'::1,'b'::2,'c'::3]; 

		}
	
		test PointAccesOp {
			//unknown var0 <- agent1.location; 	// var0 equals the location of the agent agent1

			assert map(nil).keys raises: "exception"; 
			matrix var2 <- matrix([[1,1],[1,2]]) . matrix([[1,1],[1,2]]); 	// var2 equals matrix([[2,3],[3,5]])
			assert var2 equals: matrix([[2,3],[3,5]]); 

		}
	
		test row_atOp {
			list var0 <- matrix([["el11","el12","el13"],["el21","el22","el23"],["el31","el32","el33"]]) row_at 2; 	// var0 equals ["el13","el23","el33"]
			assert var0 equals: ["el13","el23","el33"]; 

		}
	
		test rows_listOp {
			list<list> var0 <- rows_list(matrix([["el11","el12","el13"],["el21","el22","el23"],["el31","el32","el33"]])); 	// var0 equals [["el11","el21","el31"],["el12","el22","el32"],["el13","el23","el33"]]
			assert var0 equals: [["el11","el21","el31"],["el12","el22","el32"],["el13","el23","el33"]]; 

		}
	
		test shuffleOp {
			matrix var0 <- shuffle (matrix([["c11","c12","c13"],["c21","c22","c23"]])); 	// var0 equals matrix([["c12","c21","c11"],["c13","c22","c23"]]) (for example)
			container var1 <- shuffle ([12, 13, 14]); 	// var1 equals [14,12,13] (for example)
			string var2 <- shuffle ('abc'); 	// var2 equals 'bac' (for example)

		}
	
		test traceOp {
			float var0 <- trace(matrix([[1,2],[3,4]])); 	// var0 equals 5
			assert var0 equals: 5; 

		}
	
		test transposeOp {
			matrix var0 <- transpose(matrix([[5,-3],[6,-4]])); 	// var0 equals [[5,6],[-3,-4]]
			assert var0 equals: [[5,6],[-3,-4]]; 

		}
	
	}

experiment testOpMatrix_related__operatorsTestExp type: gui {}	
	