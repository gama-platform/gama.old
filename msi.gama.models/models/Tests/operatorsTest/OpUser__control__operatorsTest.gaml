/**
 *  OpOpUser__control__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpUser__control__operatorsTest.
 */

model OpUser__control__operatorsTest

global {
	init {
		create testOpUser__control__operatorsTest number: 1;
		ask testOpUser__control__operatorsTest {do _step_;}
	}
}


	species testOpUser__control__operatorsTest {

	
		test user_inputOp {
			map<string,unknown> values2 <- user_input("Enter numer of agents and locations",["Number" :: 100, "Location" :: {10, 10}]);
			//create bug number: int(values2 at "Number") with: [location:: (point(values2 at "Location"))];
			map<string,unknown> values <- user_input(["Number" :: 100, "Location" :: {10, 10}]);
			assert (values at "Number") equals: 100;
			assert (values at "Location") equals: {10,10};
			//create bug number: int(values at "Number") with: [location:: (point(values at "Location"))];

		}
	
	}


experiment testOpUser__control__operatorsTestExp type: gui {}	
	