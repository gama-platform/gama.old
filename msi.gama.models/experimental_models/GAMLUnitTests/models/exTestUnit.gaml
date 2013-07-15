/**
 *  exTestUnit
 *  Author: bgaudou
 *  Description: 
 */

model exTestUnit

global {
	init {
		create exampleAgentTest number: 1;
	}
}

entities {
	species exampleAgentTest {
		setUp {
			write "SetUp";
		}
		
		test t1 {
         	assert value: 100 + 100 equals: 200;
         	assert value: 100 + 100 equals: 201;
		}
		test t2 {
        	assert value: any([1,2,3]) is_not: nil;
        	assert value: any([1,2,3]) is int;
        	assert value: any([1,2,3]) is_not: 5;
        	assert value: any([1,2,3]) is float;
        	assert value: any([1,2,3]) is string;
		}
		test t3 {
	 		list<int> aa <- [];
    	 	assert value: aa[0] raises: "error";
//		 	aa <- [];
		 // 	add 'abc' to: aa;
		 // 	aa << 'abc';
//	        assert (aa << 'abc') raises: error;
		}
	}
}

experiment new type: gui {}
