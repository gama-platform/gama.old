model action_return_type

global {
	
}

environment width: 100 height: 100 {
	grid dummy_grid width: 100 height: 100 {
		action dummy_action type: int {
			return 0;
		}
	}
}

entities {
	species another_agent {
		action another_action type: int {
			return 0;
		}
	}
	
	species dummy_agent {
		reflex {
			
			// @Alexis: peut-on Žcrire comme ci-dessous?
			
			
			let i type: int value: another_agent( one_of(list(another_agent)) ) another_action [];
			
			let j type: int value: dummy_grid( one_of(list(dummy_grid)) ) dummy_action [];
		}
	}
}

experiment an_experiment type: gui {
	
}