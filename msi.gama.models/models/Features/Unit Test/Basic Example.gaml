/**
 *  new
 *  Author: bgaudou
 *  Description:
 *    a simple example of the use of the first version of the unit test in GAMA.
 *
 *   Exceptions will be thrown.
 *
 *    NOTE: you have to go in the Preferences, in "General" pane and Uncheck "stop at the first error", to show all the errors.
 */

model test_unitTest_framework

global {
	init {
		create test_species number: 1;
	}
}

entities {
	species test_species {
		int a <- 0;

		setup {
			a <- 10;
			write "SetUp : a = " + a;
		}

		test t1 {
         	assert 100 + 100 equals: 200;
        	assert 100 + 100 equals: 201;
		}
		test t2 {
        	assert any([1,2,3]) is_not: nil;
        	assert any([1,2,3]) is int;
        	assert any([1,2,3]) is_not: 5;
        	assert any([1,2,3]) is float;
        	assert value: any([1,2,3]) is string;
		}

		test incement_a {
       		a<- a + 10;
        	write "a: " + a;
		}

		test t3 {
	 		list<int> aa <- [];
    	 	assert value: aa[0] raises: "error";
    	 	assert value: aa[0] raises: "";
    	 	assert a raises: "error";
		}
	}
}

experiment new type: gui {}
