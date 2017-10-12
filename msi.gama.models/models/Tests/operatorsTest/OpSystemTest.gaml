/**
 *  OpOpSystemTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpSystemTest.
 */

model OpSystemTest

global {
	init {
		create testOpSystemTest number: 1;
		ask testOpSystemTest {do _step_;}
	}
}


	species testOpSystemTest {

	
		test commandOp {

		}
	
		test copyOp {

		}
	
		test deadOp {
			//bool var0 <- dead(agent_A); 	// var0 equals true or false

		}
	
		test eval_gamlOp {
			unknown var0 <- eval_gaml("2+3"); 	// var0 equals 5
			assert var0 equals: 5; 

		}
	
		test everyOp {
			reflex when: every(2#days) since date('2000-01-01') { .. }
			state a { transition to: b when: every(2#mn);} state b { transition to: a when: every(30#s);} // This oscillatory behavior will use the starting_date of the model as its starting point in time
			if every(2) {write "the cycle number is even";}
				     else {write "the cycle number is odd";}
			(date('2000-01-01') to date('2010-01-01')) every (#month) // builds an interval between these two dates which contains all the monthly dates starting from the beginning of the interval

		}
	
		test every_cycleOp {

		}
	
		test is_errorOp {

		}
	
		test is_warningOp {

		}
	
		test ofOp {

		}
	
		test PointAccesOp {
			//unknown var0 <- agent1.location; 	// var0 equals the location of the agent agent1

			assert map(nil).keys raises: "exception"; 
			matrix var2 <- matrix([[1,1],[1,2]]) . matrix([[1,1],[1,2]]); 	// var2 equals matrix([[2,3],[3,5]])
			assert var2 equals: matrix([[2,3],[3,5]]); 

		}
	
		test user_inputOp {
			map<string,unknown> values2 <- user_input("Enter numer of agents and locations",["Number" :: 100, "Location" :: {10, 10}]);
			//create bug number: int(values2 at "Number") with: [location:: (point(values2 at "Location"))];
			map<string,unknown> values <- user_input(["Number" :: 100, "Location" :: {10, 10}]);
			assert (values at "Number") equals: 100;
			assert (values at "Location") equals: {10,10};
			//create bug number: int(values at "Number") with: [location:: (point(values at "Location"))];

		}
	
	}


experiment testOpSystemTestExp type: gui {}	
	