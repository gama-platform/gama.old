/***
* Name: Loops
* Author: Benoit Gaudou
* Description: This model illustrates the possible uses of the loop statement on a list (but everything can be applied to any kind of container).
* Tags: loop, list, over, times, while
***/

model LoopsPossibleUses

global {
	list<string> list_of_string <- ["A","list","of","strings","."];
	
	init  {
		// The facet times: is used to repeat a fixed number of times a set of statements:
		loop times: 2 {
			write "======================================";
		} 
		write "= Various uses of the loop statement =";
		loop times: 2 {
			write 	"======================================";
		} 		
		
		// The facets from: and to: can be used to repeat a set of statements while an index iterates over a range of values with a fixed step of 1:
		// For example, to iterate over the list list_of_string
		write "----------------------------------";
		write "Loop  from: to: " ;
		write "----------------------------------";

		loop index from: 0 to: length(list_of_string) - 1 {
			write "" + index +"th element of " + list_of_string;
			write sample(list_of_string[index]);
		}
		
		// The three facets from:, to: and step: can be used to set the loop step (notice that step can be negative).
		write "----------------------------------";
		write "Loop  from: to: step:" ;
		write "----------------------------------";

		loop index from: 0 to: length(list_of_string) - 1 step: 2 {
			write "" + index +"th element of " + list_of_string;
			write "  " + sample(list_of_string[index]); 
		}

		// When step: is negative,we can iterate over a list in the reverse order
		loop index from: length(list_of_string) - 1 to: 0 step: -1 {
			write "" + index +"th element of " + list_of_string;
			write "  " + sample(list_of_string[index]); 
		}		

		// The facet over: can be used to repeat a set of statements while iterating over a full container:
		// For example, to iterate over the list list_of_string (which is equivalent to iterating over the list with from: and to: facets).
		write "----------------------------------";
		write "Loop  over: " ;
		write "----------------------------------";

		loop elt over: list_of_string {
			write sample(elt);
		}		
		
		// The facet while: is used to repeat a set of statements while a condition is true.
		// Be careful if the condition is always true, the loop will never end.
		
		write "----------------------------------";
		write "Loop  while: " ;
		write "----------------------------------";

		// For example, while can be used to iterate over a list while a given word is not found.
		string word_to_find <- "of";
		int index <- 0;
		
		loop while: ( (index < length(list_of_string)) and (list_of_string[index] != word_to_find )) {
			write "" + index +"th element of " + list_of_string;
			write "  " + sample(list_of_string[index]); 			
			// index should be incremented ... otherwise the loop while never end.
			index <- index + 1;
		}					
		
		do break_loop;
	}
	
	
	
	// You can interrupt a loop at any time by using the break statement.	
	// The following loop will continue until 
	action break_loop {
		int i <- 0;
		loop while: true {
			write "" + i + "th iteration!" ;
			if(flip(0.01)) {
				break;
			}
			i <- i + 1;
		}
	}
}

experiment name type: gui { }
