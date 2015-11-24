/**
 *  evalWhenTest
 *  Author: Mathieu
 *  Description: example of the use of the eval_when operator and of the get_plans action
 */

model evalWhenTest

global{
	init{
		create agentTest number : 1;
		create agentTest2 number : 1;
//		create agentTest3 number : 12; 
	}
}

species agentTest control: simple_bdi{
	int test <- 0;
	predicate truc <- new_predicate("test_agentTest2");
	
	plan toto when: test=10 {
		
	}
	
	plan truc1{
		
	}
	
	plan truc2{
		
	}
	
	reflex titi {
		write "belief : "+ belief_base;
		write "desire : "+ desire_base;
//		do add_desire(new_predicate("test"+test,["value"::test]));
		list<BDIPlan> myplans<-get_plans() as list<BDIPlan>;
		BDIPlan planToto <- first(myplans where (each.name="toto"));
		if(eval_when(planToto)){
			write "10";
			do remove_all_beliefs(new_predicate("test_agentTest2"));
		}
		else{
			write "pas 10";
		}
		test<-test+1;
	}
	
	rule belief: new_predicate("test_agentTest2") desire: new_predicate("test_agentTest2") when: (test=3);
	
	perceive target:agentTest2 {
		if(test=2){
			write "2";
		}
		if(myself.test=2){
			write "mon 2";
		}	
		focus var:test agent: myself;	
	}
	
}

species agentTest2 {
	int test<-10;
	
	reflex bidule{
//		write "beliefs2 : "+ belief_base;
		test<-test-1;
	}
}

species agentTest3 {
}

experiment main type: gui{
	
}

