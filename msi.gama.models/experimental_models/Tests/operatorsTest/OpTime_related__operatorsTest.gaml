/**
 *  OpOpTime_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpTime_related__operatorsTest.
 */

model OpTime_related__operatorsTest

global {
	init {
		create testOpTime_related__operatorsTest number: 1;
		ask testOpTime_related__operatorsTest {do _step_;}
	}
}


	species testOpTime_related__operatorsTest {

	
		test dateOp {
			date("1999-12-30", 'yyyy-MM-dd')

		}
	
		test stringOp {
			format(#now, 'yyyy-MM-dd')

		}
	
	}


experiment testOpTime_related__operatorsTestExp type: gui {}	
	