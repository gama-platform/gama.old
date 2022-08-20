
@requires [msi.gama.core]

/**
* Name: break_and_continue
* This model shows how (and where) to use break and continue inside loops and other GAML statements. 
* Author: A. Drogoul
* Tags: loop, break, continue, ask
*/


model break_and_continue


global {
	init {
	 	do continue_in_loop_demo();
	 	do break_in_loop_demo();
	 	do break_in_infinite_loop_demo();
	 	do break_in_switch_demo();
	 	do break_in_create_demo();
	 	do break_in_ask_demo();
	}
	
	
	action continue_in_loop_demo {
		write "Use continue to write every odd number from 1 to 100";
		loop i from: 1 to: 100 {
			if (even(i)){ continue;}
			write "This number " + i + " is odd";
		}
	}
	
	action break_in_infinite_loop_demo {
		loop while: true {
			write "Infinite loop";
			if (flip(0.01)) {break;}
		}
	}
	
	action break_in_loop_demo {
		list<int> numbers <- list_with(20, rnd(100));
		write "Use break to write every number until one is even in " + numbers;

		loop i over: numbers {
			if (even(i)) {break;}
			write "Odd number " + i;
		}
	} 
	
	action break_in_switch_demo {
		write "Break can be used in switch (but not continue) to stop the match making";
		int i <- 100;
		switch i { 
			match 100 {
				write "match 100: equal to 100. Let's continue";
			}
			match_one [0,10,100] {
				write "match_one [0,10,100] : This one is ok too, but the last one will be skipped because we break the switch just after";
				break;
			}
			match_between [0,1000] {
				write "match_between [0,1000] : This one is ok too, but never displayed";
			}
		}
	}
	
	action break_in_create_demo {
		write "Break can be used in create (and also release, capture, generate) to stop initializing agents. 
				Warning: it does not stop CREATING them, but stops INITIALIZING them after the first break";
		int j <- 0;
		create dummy number: 200 {
			write "initializing agent " + j;
			j <- j + 1;
			if (j > 10) {break;}
			initialized <- true;
		}
		write "Number of agents created " + length(dummy) + " and initialized " + length(dummy where each.initialized);
	}
	
	action break_in_ask_demo {
		write "Break can be used in ask statements, but it only works (obviously) if no parallel execution is involved";
		int j <- 0;
		ask shuffle(dummy) {
			write "No parallelism: Ask #" + j + " to dummy #" + int(self);
			j <- j + 1;
			if (j >= 5) {break;}
		}
		j <- 0;
		ask shuffle(dummy) parallel: true{
			write "Parallelism: Ask #" + j + " to dummy #" + int(self);
			j <- j + 1;
			// In that case break is not taken into account
			if (j >= 5) {break;}
		}
		
		
	}
	
	species dummy {
		bool initialized <- false;
	}
	
}

experiment "Run me";

