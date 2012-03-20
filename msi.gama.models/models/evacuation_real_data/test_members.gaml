model test_members

global {
	
}

environment width: 100 height: 100;

entities {
	species A {
		
		init {
			create B number: 2;
		}
		
		species B {
			
		}
	}
}

experiment default_expr type: gui {
	output {
		monitor Bs value: (list(A) collect each.members);
	}
}