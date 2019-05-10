/***
* Name: Iterators
* Author: Benoit Gaudou
* Description: The iterators are a way to make a computation by iterating over a container.
* This model compare the use of iterators with the loop.
* Tags: loop, iterator, max_of, where, with_max_of
***/

model Iterators

global {	
	init {
		// Given a set of 10 dummy_species agents with an integer attribute.	
		create dummy_species number: 10{
			val <- rnd(10);
		}
		
		// Given this population, we can need to:
		// 1. Get the maximum value of the attribute val,		
		// 2. Get the agent with the maximum value of the attribute val,
		// 3. Filter only the agents with a val value greater than 5.
		// .... 
	
		// All these computations requier to iterate over all the population.
		// This can be done using a loop.
		// But GAML also provides many iterators allowing to compute these values very easily.
		
		// 1. Get the biggest value of the attribute val
		// Using a loop
		int val_max <- int(#min_int);
		
		loop agt over: dummy_species {
			if(agt.val > val_max) {
				val_max <- agt.val;
			}
		}
		write "[Using loop] The maximum value of val is: " + val_max;
		
		// Using an iterator
		int val_max_ite <- dummy_species max_of(each.val);
		write "[Using iterator]  The maximum value of val is: " + val_max_ite; 
		
		
		// 2. Get the agent with the maximum value of the attribute val
		// Using a loop
		dummy_species agt_with_max_val <- first(dummy_species);
		loop agt over: dummy_species {
			if(agt.val > agt_with_max_val.val) {
				agt_with_max_val <- agt;
			}			
		}
		write "[Using loop]  The agent with the maximum value of val is: " + agt_with_max_val; 
		
		// Using an iterator
		dummy_species agt_with_max_val_ite <- dummy_species with_max_of(each.val);
		write "[Using iterator]  The agent with the maximum value of val is: " + agt_with_max_val_ite; 
		
		// 3. Filter only the ones with a val value greater than 5
		// Using a loop
		list<dummy_species> list_agt_filtered <- [];
		loop agt over: dummy_species {
			if(agt.val > 5) {
				add agt to: list_agt_filtered;
			}			
		}
		write "[Using loop]  The agents with a value of val greater than 5: " + list_agt_filtered; 
		
		// Using an iterator
		list<dummy_species> list_agt_filtered_ite <- dummy_species where(each.val > 5);
		write "[Using iterator]  The agents with a value of val greater than 5: " + list_agt_filtered_ite; 
		
	}
	
	// Here are some other operators which can be useful to manipulate lists:
	// sort, sort_by, shuffle, reverse, collect, accumulate, among. 
	// Please read the GAML Reference if you want to know more about those operators.
		
}

species dummy_species {
	int val;
}

experiment exp type: gui {}