/**
* Name: Test Experiment
* Author: A. Drogoul
* Description: Shows how tests can be written using a simple experiment
* Tags: test
*/

experiment "Test Experiment" type: test autorun: true {
	
	string a_string <- "aaa";
	
	test Arithmetic {
		assert 1+1 = 2;
		assert 1-1 = 0;
		assert 1*1 = 1;
		assert 1/1 = 1;
	}
	
	test Strings {
		a_string <- a_string + "a";
		assert a_string = "aaaa";
		a_string <- a_string replace("a","b");
		assert  a_string = "bbbb";
	}
	
}
