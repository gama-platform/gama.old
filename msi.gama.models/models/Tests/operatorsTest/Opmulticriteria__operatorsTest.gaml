/**
 *  OpOpmulticriteria__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category Opmulticriteria__operatorsTest.
 */

model Opmulticriteria__operatorsTest

global {
	init {
		create testOpmulticriteria__operatorsTest number: 1;
		ask testOpmulticriteria__operatorsTest {do _step_;}
	}
}

	species testOpmulticriteria__operatorsTest {

	
		test electre_DMOp {
			int var0 <- electre_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [["name"::"utility", "weight" :: 2.0,"p"::0.5, "q"::0.0, "s"::1.0, "maximize" :: true],["name"::"price", "weight" :: 1.0,"p"::0.5, "q"::0.0, "s"::1.0, "maximize" :: false]]); 	// var0 equals 0
			assert var0 equals: 0; 

		}
	
		test evidence_theory_DMOp {
			int var0 <- evidence_theory_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [["name"::"utility", "s1" :: 0.0,"s2"::1.0, "v1p"::0.0, "v2p"::1.0, "v1c"::0.0, "v2c"::0.0, "maximize" :: true],["name"::"price",  "s1" :: 0.0,"s2"::1.0, "v1p"::0.0, "v2p"::1.0, "v1c"::0.0, "v2c"::0.0, "maximize" :: true]], true); 	// var0 equals 0
			assert var0 equals: 0; 

		}
	
		test fuzzy_choquet_DMOp {
			int var0 <- fuzzy_choquet_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], ["utility", "price", "size"],[["utility"]::0.5,["size"]::0.1,["price"]::0.4,["utility", "price"]::0.55]); 	// var0 equals 0
			assert var0 equals: 0; 

		}
	
		test promethee_DMOp {
			int var0 <- promethee_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [["name"::"utility", "weight" :: 2.0,"p"::0.5, "q"::0.0, "s"::1.0, "maximize" :: true],["name"::"price", "weight" :: 1.0,"p"::0.5, "q"::0.0, "s"::1.0, "maximize" :: false]]); 	// var0 equals 1
			assert var0 equals: 1; 

		}
	
		test weighted_means_DMOp {
			int var0 <- weighted_means_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [["name"::"utility", "weight" :: 2.0],["name"::"price", "weight" :: 1.0]]); 	// var0 equals 1
			assert var0 equals: 1; 

		}
	
	}

experiment testOpmulticriteria__operatorsTestExp type: gui {}	
	