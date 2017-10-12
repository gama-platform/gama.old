/**
 *  OpOpLogical__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpLogical__operatorsTest.
 */

model OpLogical__operatorsTest

global {
	init {
		create testOpLogical__operatorsTest number: 1;
		ask testOpLogical__operatorsTest {do _step_;}
	}
}


	species testOpLogical__operatorsTest {

	
		test andOp {

		}
	
		test ELSEoperatorOp {

		}
	
		test IFoperatorOp {
			list<string> var0 <- [10, 19, 43, 12, 7, 22] collect ((each > 20) ? 'above' : 'below'); 	// var0 equals ['below', 'below', 'above', 'below', 'below', 'above']
			assert var0 equals: ['below', 'below', 'above', 'below', 'below', 'above']; 
			rgb color <- (flip(0.3) ? #red : (flip(0.9) ? #blue : #green));

		}
	
		test notOp {

		}
	
		test NOunaryOp {
			bool var0 <- ! (true); 	// var0 equals false
			assert var0 equals: false; 

		}
	
		test orOp {

		}
	
		test xorOp {

		}
	
	}

experiment testOpLogical__operatorsTestExp type: gui {}	
	