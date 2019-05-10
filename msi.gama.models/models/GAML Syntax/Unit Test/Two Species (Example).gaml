/**
* Name: Example of Unit Test
* Author: Benoit Gaudou
* Description: The model creates 10 agents at initialization, kills one agent at each step. 
 * With the assert we want to ensure that the test_species1 population always contains more than 5 agents. 
 * After four steps the population is below 6 so Errors are raisen. 
* You have to go in the Views, Preferences, Simulation, in "Errors" Uncheck "stop at the first error", to show all the errors.
* Tags: test
 */


model test_unitTest_framework

global {
	init {
		//Create agents : one to test the population of test_species1 and the test_species1 agents
		create test_species2 number: 1;
		create test_species1 number: 10;
	}
	//At each step, one of the agents of test_species1 dies
	reflex one_should_die {
		ask one_of(test_species1){
			do die;
		}
	}
}


species test_species2 {
	//The step is computed each step to test if there is still at least 5 agents of test_species1
	test t1 {
     	assert (length(test_species1) > 5) ;
	}
}

species test_species1 {
}


experiment new type: test until: cycle = 10{}
