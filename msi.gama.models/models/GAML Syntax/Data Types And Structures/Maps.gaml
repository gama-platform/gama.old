/**
* Name: Maps
* Author: Alexis Drogoul
* Description: Examples of the syntax and various operators used to manipulate the 'map' data type. 
* Read the comments and run the model to get a better idea on how to use maps in GAML. 
* Tags: map, loop
*/

model Maps

/* Maps is a data structure consisting of a list of pair<key, value> where each key is unique */

species declaring_map_attributes {
	
	/**
	 * Declarations of map attributes
	 */
	 // The simplest declaration identifies empty_map as a map that can contain any type of objects. 
	 // Its default value will be [] (the empty list/map) if it is not initialized.
	map empty_map;
	// To provide it with a literal initial value, use the '<-' (or 'init:') facet and pass a map
	map explicit_empty_map <- [];
	// Or, more explicitely
	map explicit_empty_map2 <- map([]);
	// Values can be declared litterally in this map, which is nothing more than a list of pair objects
	map explicit_filled_map <- ["First"::1, "Second"::2];
	// If a map is initialized with a list that contains non-pair objects, the pairs element::element are added to the map
	map map_initialized_with_list <- map<int, int>([1,2,3,4]);
	// maps can be declared so that they only accept a given type of keys and values
	// For instance, empty_map_of_int will accept string keys and integer values
	map<string, int> empty_map_of_int;
	// The appropriate casting is realized if the map is initialized with a list of values
	map<string, int> map_of_int_with_init_of_string <- map<string, int>(['10', '20']); // => ['10'::10,'20'::20]
	// or with another map
	map<int, float> map_of_float_with_init_of_map <- map<int, float>(map_initialized_with_list); 
	// When the casting is not obvious, the default values are used
	// Here, the list is first casted to return pairs, and they are casted to pair<string, float>
	map<string, float> map_of_float_with_impossible_casting <- map<string, float>(['A','B']); 
	// maps can of course contain maps
	map<string, map> map_of_maps <- map<string, map>(['A'::[], 'B'::[]]);
	// untyped maps can contain heterogeneous objects
	map untyped_map <- [10::'5','11'::5,[12]::[5]];
	// the casting applies to all elements when a key and contents type is defined
	map<int, string> recasted_map_with_int_and_string <- map<int, string>(untyped_map); //=> [5,5,0]

	
	init {
		write "";
		write "== DECLARING MAPS ==";
		write "";
		write sample(empty_map);
		write sample(explicit_empty_map);
		write sample(empty_map_of_int);
		write sample(explicit_filled_map);
		write sample(map_initialized_with_list);
		write sample(map_of_int_with_init_of_string);
		write sample(map_of_float_with_init_of_map);
		write sample(map_of_float_with_impossible_casting);
		write sample(map_of_maps);
		write sample(untyped_map);
		write sample(recasted_map_with_int_and_string);
		write "";
		// Declaring temporary variables of type map follows the same pattern
		map<int,string> map_of_string <- [1::'A',2::'B',3::'C'];
		// maps are not always declared litterally and can be obtained from various elements
		// by using the casting 'map()' operator
		// for instance, map(species_name) will return a list of all the agents of species_name
		// using pairs of agent::agent. If the key is explicit, it is used in the casting:
		create test_species number:4;
		map<string, test_species> my_agents <- map<string, test_species>(test_species);
		write sample(my_agents);
		// Some special casting operations are applied to specific types, like agents (returns a copy of their attributes)
		write sample(map(any(my_agents)));
		// The 'as_map(pair)' iterator operator also provides a way to build more complex maps
		write sample(list("This is a string") as_map (length(each)::each));
		// As well as "group_by" or "index_by"
		write sample(my_agents index_by (each distance_to {0,0}));
		write sample([1,2,3,4,5,6,7] group_by ((each mod 3) = 0));
 	}
}

species test_species{}

species accessing_map_elements {
	map<int, int> l1 <- map<int, int>([1,2,3,4,5,6,7,8,9,10]);
	map<int, string> l2 <- [1::'this',2::'is',3::'a',4::'list', 5::'of',6::'strings'];
	init {
		write "";
		write "== ACCESSING MAPS ELEMENTS ==";
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
		write sample(3 among l1);
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
	}
}

species combining_maps {
	map<int, int> l1 <- map<int, int>([1,2,3,4,5,6,7,8,9,10]);
	map<int, int> l2 <- map<int, int>([1,3,5,7,9]);
	init {

		write "";
		write "== COMBINING MAPS ==";
		write "";
		write sample(l1);
		write sample(l2);
		write sample(l1 + l2);
		write sample(l1 - l2);
		write sample(l1 inter l2);
		write sample(l1 union l2);
		map<int,string> l3 <- map<int, string>(l1 + l2);
		write "map<string> l3 <- l1 + l2; " + sample(l3);
	}
}

species modifying_maps {
	init {
		write "";
		write "== MODIFYING MAPS ==";
		write "";
		// Besides assigning a new value to a map, maps can be manipulated using
		// the "add", "remove" and "put" statements. 
		// Let's define an empty list supposed to contain integer keys and values
		trace { map<int, int> m1 <- [0::0]; 
		// and add some stuff to it using "add"
		add 1::1 to: m1;
		add 2::2 to: m1;
		add 3::3 to: m1;
		write sample(m1);
		// the same can be done with the compact syntax introduced in GAMA 1.6.1 for "add"
		m1 <+ 4::4;
		m1 <+ 5::5;
		write sample(m1);
		// tired of writing lines of add ? The "all:" facet is here to serve:
		add [6, 7, 8, 9] to: m1 all: true;
		// or, in a more compact way:
		m1 <<+ [10,11,12,13];
		write sample(m1);
		// automatic casting applies to any element added to the map
		m1 <+ int("14");
		// as well as any container of elements
		m1 <<+ map<int, int>([15::"15", 16::16.0]);
		write sample(m1);
		// elements are by default added to the map while their keys are unique
		// So, what about replacing some elements once they have been added ?
		// "put" can be used for that purpose
		put -2 at: 0 in: m1;
		// or, more simply:
		m1[0] <- -3;
		// Trying to put an element outside the "bounds" of the map will of course not yield an error
		 m1[20] <- 10; 
		write sample(m1);
		// And what about replacing all the values with a new one ?
		m1[] <- 0;
		write("m1[] <- 0;");
		write sample(m1);
		// Well, m1 is a bit boring now, isnt't it ?
		// Let's fill it again with fresh values
		loop i over: m1.keys {
			m1[i] <- rnd(3);
		}
		write(sample(m1));
		// To remove values from it, the "remove" statement (and its compact ">-" form) can be used
		// WARNING: this form operation on the *values* of the map (i.e. it will remove the first pair
		// whose value = 0
		remove 0 from: m1;
		// it can also be written
		m1 >- 0;
		write(sample(m1));
		// To remove all occurrences of pairs with a specific value, "all:" (or ">>-") can be used
		// For instance:
		m1 >>- 2;
		// or, written using the long syntactic form
		remove 1 from: m1 all: true;
		write sample(m1);
		// To remove keys instead, the same syntax can be used, but on the keys of the map (i.e. map[])
		m1[] >- 1; // This will remove the (unique) pair whose key = 1
		// The equivalent long syntax is
		remove key: 1 from: m1;
		// To remove a set of keys, the following syntax can be used
		m1[] >>- [2,3,4];
 		// And to remove all the keys present in a given map (using the 'keys' attribute)
		m1[] >>- m1.keys;
		write sample(m1);
		// By all means, m1 should now be empty! Let's fill it again
		int i <- 0;
		loop times: 20 {
			i <- i + 1;
			m1 <+ i::rnd(3);
		}
		// Random things to try out
		// Using the 'pairs' attribute: all number now vary from 1000 to 1003
		m1 <- m1.pairs as_map (each.key::((each.value) + 1000));
		write sample(m1);
		// Removing values based on a criteria
		m1 >>- m1 select (each > 1001);
		write(sample(m1));
	}

	}
	
}

species looping_on_maps {
	init {
		write "";
		write "== LOOPING ON MAPS ==";
		write "";
		// Besides iterator operators (like "collect", "where", etc.), which provide 
		// functional iterations (i.e. filters), one can loop over maps using the imperative
		// statement 'loop'
		list<string> strings <- list("This a list of string");
		write sample(strings);
		map<string, string> l1 <- strings as_map (first(each)::each);
		write sample(l1);
		int i <- 0;
		list l2 <- [];
		// Here, the value of 's' will be that of each value of each pairs of the list
		loop s over: l1 { // equivalent to 'loop s over: l1.values'
			i <- i + 1;
			l2 << "Word #" + i + ": " + s;
		}
		write sample(l2);
		// To loop on the keys of l1, simply use its 'keys' attribute
		l2 <- [];
		 i <- 0;
		loop s over: l1.keys{
			i <- i + 1;
			l2 << "Key #" + i + ": " + s;
		}
		write(sample(l2));
		// Looping on indexes allows to gain access to each element in turn
		l2 <- [];
		loop k over: l1.keys {
			l2 <+ l1[k];
		}
		write sample(l2);
		// Finally, maps containing agents can be the support of implicit loops in the 'ask' statement
		create test_species number: 5 returns: my_agents;
		map<int, test_species> map_of_agents <- map<int, test_species>(my_agents);
		write(sample(map_of_agents));
		l2 <- [];
		ask map_of_agents{
			// attributes of each agent can be directly accessed
			l2 << name;
		}
		write sample(l2);
		// Of course, this can be done more simply like this
		l2 <- map_of_agents collect each.name;
		}
	}


experiment Maps type: gui {
	user_command "Declaring maps" {create declaring_map_attributes;}
	user_command "Accessing maps" {create accessing_map_elements;}
	user_command "Combining maps" {create combining_maps;}
	user_command "Modifying maps" {create modifying_maps;}
	user_command "Looping on maps" {create looping_on_maps;}	
}
