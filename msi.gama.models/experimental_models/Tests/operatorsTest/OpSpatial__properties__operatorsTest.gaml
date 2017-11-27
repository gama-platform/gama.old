/**
 *  OpOpSpatial__properties__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSpatial__properties__operatorsTest.
 */

model OpSpatial__properties__operatorsTest

global {
	init {
		create testOpSpatial__properties__operatorsTest number: 1;
		ask testOpSpatial__properties__operatorsTest {do _step_;}
	}
}

	species testOpSpatial__properties__operatorsTest {

	
		test coversOp {
			bool var0 <- square(5) covers square(2); 	// var0 equals true
			assert var0 equals: true; 

		}
	
		test crossesOp {
			bool var0 <- polyline([{10,10},{20,20}]) crosses polyline([{10,20},{20,10}]); 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- polyline([{10,10},{20,20}]) crosses {15,15}; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- polyline([{0,0},{25,25}]) crosses polygon([{10,10},{10,20},{20,20},{20,10}]); 	// var2 equals true
			assert var2 equals: true; 

		}
	
		test intersectsOp {
			bool var0 <- square(5) intersects {10,10}; 	// var0 equals false
			assert var0 equals: false; 

		}
	
		test partially_overlapsOp {
			bool var0 <- polyline([{10,10},{20,20}]) partially_overlaps polyline([{15,15},{25,25}]); 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {25,25}; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{35,35},{35,45},{45,45},{45,35}]); 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polyline([{10,10},{20,20}]); 	// var4 equals false
			assert var4 equals: false; 
			bool var5 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {15,15}; 	// var5 equals false
			assert var5 equals: false; 
			bool var6 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]); 	// var6 equals false
			assert var6 equals: false; 
			bool var7 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var7 equals true
			assert var7 equals: true; 
			bool var8 <- polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{10,20},{20,20},{20,30},{10,30}]); 	// var8 equals false
			assert var8 equals: false; 

		}
	
		test touchesOp {
			bool var0 <- polyline([{10,10},{20,20}]) touches {15,15}; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- polyline([{10,10},{20,20}]) touches {10,10}; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- {15,15} touches {15,15}; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- polyline([{10,10},{20,20}]) touches polyline([{10,10},{5,5}]); 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <- polyline([{10,10},{20,20}]) touches polyline([{5,5},{15,15}]); 	// var4 equals false
			assert var4 equals: false; 
			bool var5 <- polyline([{10,10},{20,20}]) touches polyline([{15,15},{25,25}]); 	// var5 equals false
			assert var5 equals: false; 
			bool var6 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{15,15},{15,25},{25,25},{25,15}]); 	// var6 equals false
			assert var6 equals: false; 
			bool var7 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,20},{20,20},{20,30},{10,30}]); 	// var7 equals true
			assert var7 equals: true; 
			bool var8 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,10},{0,10},{0,0},{10,0}]); 	// var8 equals true
			assert var8 equals: true; 
			bool var9 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches {15,15}; 	// var9 equals false
			assert var9 equals: false; 
			bool var10 <- polygon([{10,10},{10,20},{20,20},{20,10}]) touches {10,15}; 	// var10 equals true
			assert var10 equals: true; 

		}
	
	}


experiment testOpSpatial__properties__operatorsTestExp type: gui {}	
	