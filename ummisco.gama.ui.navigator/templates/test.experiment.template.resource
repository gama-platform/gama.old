/**
* Name: $TITLE$
* $DESC$ 
* Author: $AUTHOR$
* Tags: 
*/

experiment $TITLE$ type: test autorun: true {
	
	/** Insert the global definitions, variables and actions here */
		
		
		
	setup {
		/** Insert any initialization to run before each of the tests */
	}
	
	test "My First Test" {
	/** Insert here any assertion you need to test */
		assert 0 = 0;
		assert 0 = 1 warning: true;
	}
	
}
