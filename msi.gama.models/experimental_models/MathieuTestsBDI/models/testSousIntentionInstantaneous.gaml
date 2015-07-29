/**
 *  testSousIntentionInstantaneous
 *  Author: taillpat
 *  Description: 
 */

model testSousIntentionInstantaneous

global {
	init{
		create a number:1{
			do add_desire(i1);
//			do add_desire(i3);
		}
	}
}

species a control:simple_bdi{
	predicate i1 <- new_predicate("i1") with_priority(2);
	predicate i2 <- new_predicate("i2");
	predicate i3 <- new_predicate("i3") with_priority(3);
	
	bool probabilistic_choice <- true;
	
	plan a1 intention:i1 /*instantaneous:true*/{
		write "1";
		write get_super_intention(i2);
//		do add_subintention(get_current_intention(),i2);
		do current_intention_on_hold(i2);
	}
	
	plan a2 intention:i2{
		
		write 2;
		write get_super_intention(i2);
		write get_super_intention(get_current_intention());
		do current_intention_on_hold(i3);
//		do remove_intention(get_super_intention(get_current_intention()),true);
//		if(flip(0.5)){
//			do add_belief(i2);
//		}
	}
	
	plan a3 intention:i3{
		write 3;
		write get_super_intention(i2);
		write get_super_intention(i3);
		do add_subintention(get_current_intention(),i2);
	}
	
	reflex info{
		write "info";
		write "base désire : " + desire_base;
		write "base intention : " + intention_base;
		write "intention : " + get_current_intention();
	}
	
}

experiment testSousIntentionInstantaneous type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
	}
}
