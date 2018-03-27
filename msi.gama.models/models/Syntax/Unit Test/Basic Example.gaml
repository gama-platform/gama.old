/**
* Name: Example of Unit Test
* Author: Benoit Gaudou & Alexis Drogoul
* Description: A model which shows how to use the unit test to show the possible errors 
* Tags: test
 */

model unit_tests

global {
	init {
		create test_species number: 1;
	}
}

//Species to do the different unit tests
species test_species {
	int a <- 0;
	
	//Setup a to 10 launched before each test
	setup {
		a <- 10;
		write "SetUp : a = " + a;
	}

	//First test showing some options and operators
	test t1 {
     	assert 100 + 100 = 200;
    	assert 100 + 100 = 201 warning: true;
    	assert is_error(a/0) warning: true;
	}
	
	//Second test executing comparison between list and type
	test t2 
	{
    	assert any([1,2,3]) != nil;
    	assert any([1,2,3]) is int;
    	assert any([1,2,3]) != 5;
    	assert any([1,2,3]) is float;
    	float t <- 1/0; // An exception outside an assertion aborts the test. The following assertion will never be tested
    	assert any([1,2,3]) is string;
	}



	//test the incrementation of a
	test increment_a {
		int b <- a;
   		a<- a + 10;
   		assert a-b = 10;
	}
	
	//Third test for lists
	test t3 {
 		list<int> aa;
	 	assert is_error(aa[0]);
	}
}


experiment new type: test until: cycle=10{
	// Tests can also be placed directly into the experiment
	test in_experiment {
		assert 1+2 = 3;
	}
	
	test "No Error" {
		assert is_error(100+100);
	}
}
