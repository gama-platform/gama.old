/**
* Name: Lists
* Author: Alexis Drogoul
* Description: Examples of the syntax and various operators used to manipulate the 'list' data type. 
* Read the comments and run the model to get a better idea on how to use lists in GAML. 
* Tags: list, loop
*/

model Lists

species declaring_list_attributes {
	
	/**
	 * Declarations of list attributes
	 */
	 // The simplest declaration identifies empty_list as a list that can contain any type of objects. 
	 // Its default value will be [] (the empty list) if it is not initialized.
	list empty_list;
	// To provide it with an initial value, use the '<-' (or 'init:') facet
	list explicit_empty_list <- [];
	// lists can also be provided with a default size, in which case they are filled with the nil element
	list list_of_size_10 <-[];// list_size(10); // => [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil]
	// to fill them with a given initial value, use the 'list_with' operator
	list list_of_size_10_with_0 <- list_with(10, 0); // => [0,0,0,0,0,0,0,0,0,0]
	
	// lists can be declared so that they only accept a given type of contents.
	// For instance, empty_list_of_int will only accept integer elements
	list<int> empty_list_of_int ;
	
	// the value passed to 'list_with' is verified and casted to the contents type of the list if necessary
	list<int> list_of_int_size_10_filled_with_string<- list<int>(list_with(10,'1')); // list_of_int_size_10_filled_with_string is filled with the casting of '1' to int, i.e. 1
	list<string> list_of_string_size_10_filled_with_string <- list_with(10,'1'); // while list_of_string_size_10_filled_with_string is filled with the string '1'
	// the casting is also realized if the list is initialized with a value
	list<int> list_of_int_with_init_of_string <- list<int>(['10', '20']); // => [10,20]
	list<float> list_of_float_with_init_of_string <- list<float>(list_of_string_size_10_filled_with_string); // => [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0]
	// When the casting is not obvious, the default value is used
	list<float> list_of_float_with_impossible_casting <- list<float>(['A','B']); 
	// lists can of course contain lists
	list<list> list_of_lists <- list_with (5,[]);
	// untyped lists can contain heterogeneous objects
	list untyped_list <- ['5',5,[5]];
	// the casting applies to all elements when a contents type is defined (note the default last value of 0)
	list<int> recasted_list_with_int <- list<int>(untyped_list); //=> [5,5,0]

	
	init {
		write "";
		write "== DECLARING LISTS ==";
		write "";
		write sample(empty_list);
		write sample(explicit_empty_list);
		write sample(list_of_size_10);
		write sample(list_of_size_10_with_0);
		write sample(empty_list_of_int);
		write sample(list_of_int_size_10_filled_with_string);
		write sample(list_of_string_size_10_filled_with_string);
		write sample(list_of_int_with_init_of_string);
		write sample(list_of_float_with_init_of_string);
		write sample(list_of_float_with_impossible_casting);
		write sample(list_of_lists);
		write sample(untyped_list);
		write sample(recasted_list_with_int);
		write "";
		// lists are not always declared litterally and can be obtained from various elements
		// by using the casting 'list()' operator
		// for instance, list(species_name) will return a list of all the instances of species_name
		create test_species number:4;
		list<test_species> my_agents <- list(test_species);
		write sample(my_agents);
		list<string> my_names <- my_agents collect each.name;
		write sample(my_names);
		// 'create' can directly declare (and return) a list of the agents created. For instance:
		create test_species number: 5 returns: my_agents2;
		write (sample(my_agents2));
		// Some special casting operations are applied to specific types, like points...
		write sample(list(any(my_agents).location));
		// ... colors ...
		write sample(list(#pink));
		// ... or strings
		write sample(list("This is a string"));
		
 	}
}

species test_species{}

species accessing_list_elements {
	list<int> l1 <- [1,2,3,4,5,6,7,8,9,10];
	list<string> l2 <- ['this','is','a','list', 'of','strings'];
	init {
		write "";
		write "== ACCESSING LIST ELEMENTS ==";
		write "";
		write sample(l1);
		write sample(l2);
		write sample(first(l1));
		write sample(last(l1));
		write sample(l1 at 1);
		write sample(l1[1]);
		write sample(length(l1));
		write sample(mean(l1));
		write sample(max(l1));
		write sample(min(l1));
		write sample(any(l1));
		write sample(3 among l2);
		write sample(l1 contains 1);
		write sample(l1 contains_all [1,4,6, 14]);
		write sample(l1 contains_any [1,23]);
		write sample(reverse(l2));
		write sample(l1 collect (each + 1));
		write sample(l1 collect (norm({each, each, each})));
		write sample(l1 where (each > 5));
		write sample(l1 count (each > 5));
		write sample(l1 group_by (even(each)));
		write sample(l2 index_by (each + "_index"));
		write sample(l1 index_of 100);
		write sample(l2 last_index_of 'is');
		write sample(l2 sort_by each);
		write sample(l2 sort_by length(each));
		write sample(l2 first_with (first(each)  = 'o'));
		write sample(l2 where (length(each) = 2) );
		write sample(l2 with_min_of (length(each)));
		write sample(l2 with_max_of (length(each)));
		write sample(l2 min_of (length(each)));
		write sample(l2 max_of (length(each))); 
		write sample(copy_between(l2,1,3));
		write sample(copy_between(l2, 1, length(l2) - 1));
		write sample(l2 as_map (length(each)::"new"+each));
		// Ranges
		write sample(l2[1::3]);
	}
}

species combining_lists {
	list<int> l1 <- [1,2,3,4,5,6,7,8,9,10];
	list<int> l2 <- [1,3,5,7,9];
	list<list> useful_list_of_lists <- [['A','B'],['C','D']]; 
	init {
		write "";
		write "== COMBINING LISTS ==";
		write "";
		write sample(list<list<string>>([[1,2,3]]));
		write sample(l1);
		write sample(l2);
		write sample(l1 + l2);
		write sample(l1 - l2);
		write sample(l1 inter l2);
		write sample(l1 union l2);
		write sample(interleave ([l1,l2]));
		list<string> l3 <- list<string>(l1 + l2);
		write "list<string> l3 <- l1 + l2; " + sample(l3);
		write sample(l1 as list<float>);
		write sample(cartesian_product(useful_list_of_lists));
	}
}

species modifying_lists {
	init {
		write "";
		write "== MODIFYING LISTS ==";
		write "";
		trace {
		// Besides assigning a new value to a list, lists can be manipulated using
		// the "add", "remove" and "put" statements. 
		// Let's define an empty list supposed to contain integer elements
		list<int> l1;
		// and add some stuff to it using "add"
		add 1 to: l1;
		add 2 to: l1;
		add 3 to: l1;
		write sample(l1);
		// the same can be done with the compact syntax introduced in GAMA 1.6 for "add"
		l1 <+ 4;
		l1 <+ 5;
		write sample(l1);
		// tired of writing lines of add ? The "all:" facet is here to serve:
		add all: [6, 7, 8, 9] to: l1;
		// or, in a more compact way:
		l1 <<+ [10,11,12,13];
		write sample(l1);
		// modifyng ranges of elements is also possible
		l1[1::3] <- 100;
		write sample(l1);
		// automatic casting applies to any element added to the list
 		l1 <+ (int("14"));
		// as well as any container of elements
		l1 <<+ (list<int>(["15", 16.0]));
		write sample(l1);
		// elements are by default added to the end of the list
		// but they can be introduced at specific positions using the "at:" facet
		add 0 to: l1 at: 0;
		// or
		l1[0] +<- 0;
		// what about replacing some elements once they have been added ?
		// "put" can be used for that purpose
		put -2 at: 0 in: l1;
		// or, more elegantly:
		l1[0] <- -2;
		// Trying to put an element outside the bounds of the list will yield an error
		// l1[20] <- 10; will return the error "Index 20 out of bounds of l1"
		write sample(l1);
		// And what about replacing all the values with a new one ?
		l1[] <- 0;
		write sample(l1);
		// Well, l1 is a bit boring now, isnt't it ?
		// Let's fill it again with fresh values
		loop i from: 0 to: length(l1) -1 {
			l1[i] <- rnd(3);
		}
		write(sample(l1));
		// To remove values from it, the "remove" statement (and its compact forms) can be used
		// For instance, let's try to remove its first element
		remove first(l1) from: l1;
		// it can also be written
		l1 >- first(l1);
		write(sample(l1));
		// To remove all occurrences of a specific element, "all:" (or ">>-") can be used
		// For instance:
		l1 >>- 2;
		l1 >>- 1;
		// or, written using the long syntactic form
		remove all: 1 from: l1;
		write sample(l1);
		// To remove all the elements present in a given container, the same syntax can be used
		l1 >>- [0,3];
		write ("After l1 >>- [0,3] : " + sample( l1));
		// By all means, l1 should now be empty! Let's fill it again
		loop times: 20 {
			l1 <+ rnd(3);
		}
		write(sample(l1));
		// It is also possible to remove an index rather than a value (this will remove the 
		// value present at this index )
		l1[] >- 2;
		write("After l1[] >- 2 : " + sample(l1));
		// Removing several indexes can be written using a syntax similar to the one used
		// for removing values
		l1[] >>- [1,2,4];
		write("After l1[] >>- [1,2,4] : " + sample(l1));
		// Random things to try out
		// Using casting back and forth: all number now vary from 1000 to 31000
		l1 <- list<int>(l1 collect (string(each) + "1000"));
		write sample(l1);
		// Removing elements based on a criteria
		l1 >>- l1 select (each > 20000);
		// Removing duplicates
		l1 <- remove_duplicates(l1);
		// Another way (see "../Maps.gaml")
		l1 <- map(l1).values; 
		write(sample(l1));
	}}
	
}

species looping_on_lists {
	init {
		write "";
		write "== LOOPING ON LISTS ==";
		write "";
		// Besides iterator operators (like "collect", "where", etc.), which provide 
		// functional iterations (i.e. filters), one can loop over lists using the imperative
		// statement 'loop'
		list<string> l1 <- list("This is a list of strings");
		write sample(l1);
		int i <- 0;
		list l2 <- [];
		// Here, the value of 's' will be that of each element of the list
		loop s over: l1 {
			i <- i + 1;
			l2 << "Word #" + i + ": " + s;
		}
		write sample(l2);
		// 'loop' can also directly use an integer index (remember lists have a zero-based index)
		l2 <- [];
		loop k from: 0 to: length(l1) - 1 step:2 {
			l2 << l1[k];
		}
		write sample(l2);
		// Finally, list containing agents can be the support of implicit loops in the 'ask' statement
		create test_species number: 5 returns: my_agents;
		l2 <- [];
		ask my_agents{
			// attributes of each agent can be directly accessed
			l2 << name;
		}
		write sample(l2);
		// ... which is formally equivalent to:
		l2 <- [];
		l2 <<+ my_agents collect each.name;
		write sample(l2);
		// ... or, even simpler (since the casting of an agent to string returns its name)
		list<string> l3 <- list<string>(my_agents);
		write sample(l3);
		// Powerful filter expressions can be built by combining the various 'iterator' operators
		l3 <- list<string>(my_agents where even(int(each))) collect ("Agent " + each + " has an even id");
		write sample(l3);
	}
}

experiment Lists type: gui {
	user_command "Declaring lists" {create declaring_list_attributes;}
	user_command "Accessing lists" {create accessing_list_elements;}
	user_command "Combining lists" {create combining_lists;}
	user_command "Modifying lists" {create modifying_lists;}
	user_command "Looping on lists" {create looping_on_lists;}	
}
