model test

global {
	list test_list <- [5, 5, 7, 8, 5];
	
	reflex {
		do write {
			arg message value: 'test_list: ' + (string(test_list));
		}

		set test_list value: remove_duplicates(test_list);

		do write {
			arg message value: 'test_list AFETR: ' + (string(test_list));
		}
	}
}

environment width: 100 height: 100;

entities {
	
}

experiment default_expr type: gui {
	
}