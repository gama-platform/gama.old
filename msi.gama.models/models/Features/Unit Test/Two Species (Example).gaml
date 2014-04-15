/**
 *  test_unitTest_framework
 *  Author: bgaudou
 *  Description:
 *    a simple example of the use of the first version of the unit test in GAMA.
 *
 *  The model creates 10 agents at initialization, kills one agent at each step.
 * 	With the assert we want to ensure that the test_species1 population always contains more than 5 agents.
 *
 *  After four steps the population is below 6 so Errors are raisen.
 *
 *    NOTE: you have to go in the Preferences, in "General" pane and Uncheck "stop at the first error", to show all the errors.
 */

model test_unitTest_framework

global {
	init {
		create test_species2 number: 1;
		create test_species1 number: 10;
	}

	reflex one_should_die {
		ask one_of(test_species1){
			do die;
		}
	}
}

entities {
	species test_species2 {
		test t1 {
         	assert (length(test_species1) > 5) equals: true;
		}
	}

	species test_species1 {
	}
}

experiment new type: gui {}
