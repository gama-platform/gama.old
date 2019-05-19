/**
* Name: testPredicates
* Author: Patrick Taillandier
* Description: This wizard creates a new test experiment
* Tags: bdi, test, predicate
*/

model bdi_test

species bdi_agent control: simple_bdi;
experiment testBDI type: test autorun: true {
	setup {
		create bdi_agent {
			do add_belief(new_predicate("test1", ["loc"::{1,1}, "name"::"b1"]));
			do add_belief(new_predicate("test1", ["loc"::{2,2}, "name"::"b2"]));
			do add_belief(new_predicate("test2", ["loc"::{3,3}, "name"::"b3"]));
		}
		
	}
	
	test "Test Predicate" {
		assert new_predicate("test1") = new_predicate("test1");
		assert new_predicate("test1") = new_predicate("test1", ["loc"::location]);	
		assert new_predicate("test1",["loc"::copy(location)]) = new_predicate("test1", ["loc"::location]);	
		assert new_predicate("test1",["loc"::{0,0}]) != new_predicate("test1", ["loc"::location]);
		assert new_predicate("test1",["loc"::location, "name"::name]) = new_predicate("test1", ["loc"::location]);
	}
	
	test "Agent beliefs" {
		assert first(bdi_agent).has_belief(new_predicate("test1"));
		assert first(bdi_agent).has_belief(new_predicate("test1",["loc"::{1,1}]));
		assert not first(bdi_agent).has_belief(new_predicate("test2",["loc"::{1,1}]));
	
	}
	
	
	
}
