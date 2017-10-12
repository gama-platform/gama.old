/**
 *  OpOpRandom__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpRandom__operatorsTest.
 */

model OpRandom__operatorsTest

global {
	init {
		create testOpRandom__operatorsTest number: 1;
		ask testOpRandom__operatorsTest {do _step_;}
	}
}

	species testOpRandom__operatorsTest {

	
		test binomialOp {
			int var0 <- binomial(15,0.6); 	// var0 equals a random positive integer

		}
	
		test flipOp {
			bool var0 <- flip (0.66666); 	// var0 equals 2/3 chances to return true.

		}
	
		test gaussOp {
			float var0 <- gauss(0,0.3); 	// var0 equals 0.22354
			float var1 <- gauss(0,0.3); 	// var1 equals -0.1357
			float var2 <- gauss({0,0.3}); 	// var2 equals 0.22354
			float var3 <- gauss({0,0.3}); 	// var3 equals -0.1357

		}
	
		test improved_generatorOp {
			float var0 <- improved_generator(2,3,4,253); 	// var0 equals 10.2

		}
	
		test open_simplex_generatorOp {
			float var0 <- open_simplex_generator(2,3,253); 	// var0 equals 10.2

		}
	
		test poissonOp {
			int var0 <- poisson(3.5); 	// var0 equals a random positive integer

		}
	
		test rndOp {
			point var0 <- rnd ({2.0, 4.0}, {2.0, 5.0, 10.0}); 	// var0 equals a point with x = 2.0, y between 2.0 and 4.0 and z between 0.0 and 10.0
			int var1 <- rnd (2); 	// var1 equals 0, 1 or 2
			float var2 <- rnd (1000) / 1000; 	// var2 equals a float between 0 and 1 with a precision of 0.001
			point var3 <- rnd ({2.0, 4.0}, {2.0, 5.0, 10.0}, 1); 	// var3 equals a point with x = 2.0, y equal to 2.0, 3.0 or 4.0 and z between 0.0 and 10.0 every 1.0
			int var4 <- rnd (2, 4); 	// var4 equals 2, 3 or 4
			float var5 <- rnd (2.0, 4.0, 0.5); 	// var5 equals a float number between 2.0 and 4.0 every 0.5
			int var6 <- rnd (2, 12, 4); 	// var6 equals 2, 6 or 10
			float var7 <- rnd(3.4); 	// var7 equals a random float between 0.0 and 3.4
			point var8 <- rnd ({2.5,3, 0.0}); 	// var8 equals {x,y} with x in [0.0,2.0], y in [0.0,3.0], z = 0.0
			float var9 <- rnd (2.0, 4.0); 	// var9 equals a float number between 2.0 and 4.0

		}
	
		test rnd_choiceOp {
			int var0 <- rnd_choice([0.2,0.5,0.3]); 	// var0 equals 2/10 chances to return 0, 5/10 chances to return 1, 3/10 chances to return 2

		}
	
		test sampleOp {
			container var0 <- sample([2,10,1],2,false,[0.1,0.7,0.2]); 	// var0 equals [10,2]
			container var1 <- sample([2,10,1],2,false); 	// var1 equals [1,2]

		}
	
		test shuffleOp {
			matrix var0 <- shuffle (matrix([["c11","c12","c13"],["c21","c22","c23"]])); 	// var0 equals matrix([["c12","c21","c11"],["c13","c22","c23"]]) (for example)
			container var1 <- shuffle ([12, 13, 14]); 	// var1 equals [14,12,13] (for example)
			string var2 <- shuffle ('abc'); 	// var2 equals 'bac' (for example)

		}
	
		test simplex_generatorOp {
			float var0 <- simplex_generator(2,3,253); 	// var0 equals 10.2

		}
	
		test skew_gaussOp {
			float var0 <- skew_gauss(0.0, 1.0, 0.7,0.1); 	// var0 equals 0.1729218460343077

		}
	
		test TGaussOp {

		}
	
		test truncated_gaussOp {
			float var0 <- truncated_gauss ({0, 0.3}); 	// var0 equals a float between -0.3 and 0.3
			float var1 <- truncated_gauss ([0.5, 0.0]); 	// var1 equals 0.5
			assert var1 equals: 0.5; 

		}
	
	}

experiment testOpRandom__operatorsTestExp type: gui {}	
	