/**
 *  OpOpMap__comparaison__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpMap__comparaison__operatorsTest.
 */

model OpMap__comparaison__operatorsTest

global {
	init {
		create testOpMap__comparaison__operatorsTest number: 1;
		ask testOpMap__comparaison__operatorsTest {do _step_;}
	}
}

	species testOpMap__comparaison__operatorsTest {

	
		test fuzzy_kappaOp {
			//fuzzy_kappa([ag1, ag2, ag3, ag4, ag5],[cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0],[0,1,0],[0,0,1]], 2, [1.0,3.0,2.0,2.0,4.0])
			//fuzzy_kappa([ag1, ag2, ag3, ag4, ag5],[cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0],[0,1,0],[0,0,1]], 2)

		}
	
		test fuzzy_kappa_simOp {
			//fuzzy_kappa_sim([ag1, ag2, ag3, ag4, ag5], [cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0,0,0,0,0,0,0],[0,1,0,0,0,0,0,0,0],[0,0,1,0,0,0,0,0,0],[0,0,0,1,0,0,0,0,0],[0,0,0,0,1,0,0,0,0],[0,0,0,0,0,1,0,0,0],[0,0,0,0,0,0,1,0,0],[0,0,0,0,0,0,0,1,0],[0,0,0,0,0,0,0,0,1]], 2,[1.0,3.0,2.0,2.0,4.0])
			//fuzzy_kappa_sim([ag1, ag2, ag3, ag4, ag5], [cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2], similarity_per_agents,[cat1,cat2,cat3],[[1,0,0,0,0,0,0,0,0],[0,1,0,0,0,0,0,0,0],[0,0,1,0,0,0,0,0,0],[0,0,0,1,0,0,0,0,0],[0,0,0,0,1,0,0,0,0],[0,0,0,0,0,1,0,0,0],[0,0,0,0,0,0,1,0,0],[0,0,0,0,0,0,0,1,0],[0,0,0,0,0,0,0,0,1]], 2)

		}
	
		test kappaOp {
			//kappa([cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2],[cat1,cat2,cat3])
			float var1 <- kappa([1,3,5,1,5],[1,1,1,1,5],[1,3,5]); 	// var1 equals the similarity between 0 and 1
			float var2 <- kappa([1,1,1,1,5],[1,1,1,1,5],[1,3,5]); 	// var2 equals 1.0
			assert var2 equals: 1.0; 
			//kappa([cat1,cat1,cat2,cat3,cat2],[cat2,cat1,cat2,cat1,cat2],[cat1,cat2,cat3], [1.0, 2.0, 3.0, 1.0, 5.0])

		}
	
		test kappa_simOp {
			//kappa([cat1,cat1,cat2,cat2,cat2],[cat2,cat1,cat2,cat1,cat3],[cat2,cat1,cat2,cat3,cat3], [cat1,cat2,cat3])
			//kappa([cat1,cat1,cat2,cat2,cat2],[cat2,cat1,cat2,cat1,cat3],[cat2,cat1,cat2,cat3,cat3], [cat1,cat2,cat3],[1.0, 2.0, 3.0, 1.0, 5.0])

		}
	
		test percent_absolute_deviationOp {
			//percent_absolute_deviation([200,300,150,150,200],[250,250,100,200,200])

		}
	
	}


experiment testOpMap__comparaison__operatorsTestExp type: gui {}	
	