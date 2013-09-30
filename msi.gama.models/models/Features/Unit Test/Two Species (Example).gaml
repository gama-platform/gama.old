/**
 *  new
 *  Author: bgaudou
 *  Description: 
 *    a simple example of the use of the first version of the unit test in GAMA.
 *    
 *    NOTE: you have to go in the Preferences, in "General" pane and Uncheck "stop at the first error", to show all the errors.
 */

model test_unitTest_framework

global {
	init {
		create testToto number: 1;
		create toto number: 10;
	}
	
	reflex one_should_die {
		ask one_of(toto){
			do die;
		}
	}
}

entities {
	species testToto {			
		test t1 {
         	assert (length(toto) > 5) equals: true; 
		}
	}
	
	species toto {
	}
}

experiment new type: gui {}
