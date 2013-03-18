/**
 *  syntax
 *  Author: A. Drogoul
 *  Description: An overview of the new syntactic constructs that will be introduced in GAMA 1.6
 */

model syntax

/**
 * GLOBAL: the global section can now sport skills and control, just like other agents. 
 * The example below shows a potentially moving world that can be controlled by a finite state machine.
 */
 
 global skills: [moving] control: fsm {
 	 
/** 
 * ATTRIBUTES
 */
 
 	 // Attributes can be declared in different ways, ranging from "classic"...
 	 var a1 type: list <- [1,2,3] of: int;
 	 list a2 <- [1,2,3] of: int;
 	 // ... to "compact" Java-like syntax.
 	 list<int> a3 <- [1,2,3];
 	 
 	 // The declaration of a default size/value in containers is now working correctly
 	 list<float> f size: 2000 fill_with: 0.0;
 	 
 	 // Functions can be declared using the regular facet "->" / "function:" 
 	 int b1 -> {100 + length(a1)}; 
 	 int b2 function: {100 + length(a1)};
 	 // ... or using a block (like a statement -- note the absence of semi-column at the end)
 	 int b3 {100 + length(a1)}
 	 
 /**
 * UNITS
 */
 
 	 // The usage of units is improved ( "#" being replaced by "°"), and they can be combined
 	 float c <- 10 °meter;
 	 float speed <- 10 °meter/°sec;
 	 
 	 // Attributes can (finally!) be named after unit names
 	 float meter <- 10.0;
 	 
 	 // Some mathematical constants have been introduced alongside units
 	 float pi_4 <- °pi / 4;
 	 float e <- °e;
 	 
 	 
 /**
  * ACTIONS
  */
 	 // Actions can also be declared in different ways. Classic:
 	 action dummy1 type: list of: int {
 	 	arg a type: int default: 100;
 	 	arg b type: float;
 	 	return [a, int(b)];
 	 }
 	 // Semi-classic (prefixed by the type)
 	 list dummy2 of: int {
 	 	arg a type: int default: 100;
 	 	arg b type: float;
 	 	return [a, int(b)];
 	 }
 	 //Compact
 	 list<int> dummy3 (int a <- 100, float b) {
 	 	return [a, int(b)];
 	 }
 	 
 	 // An action that returns nothing can still be called "action"
 	 action dummy_void {
 	 	write "dummy_void";
 	 }
 	 
/**
 * DEALING WITH VARIABLES
 */
 
 	reflex variables {
 		
 		// Temporary variables can use the same syntax than attributes. The classic form:
 		let name: t type: int value: length(a1);
 		// is equivalent to the more compact one:
 		int t <- length(a1);
 		
 		// Assigning a value to variables is also sporting a new syntax
 		set t value: 100;
 		// ... can be replaced by 
 		set t <- 100;
 		// ... or even by
 		t <- 100;
 		
 		// Container variables have seen their usability clearly improved 
 		// If we define:
 		map m <- map([]);
 		list<int> l <- [];
 		
 		// Adding a value
 		add item: 1 to: l;
 		// ... can now be written
 		l ++ 1;
 		// ... or (the definitive syntax will be chosen later)
 		l << 1;
 		
 		// Removing a value
 		remove 1 from: l;
 		// ... can now be written
 		l -- 1;
 		//l >> 1;
 		
 		// Setting/putting a value
 		put "a" at: 'key' in: m;
 		put 1 at: 0 in: l;
 		// ... can now be written
 		m['key'] <- "a";
 		l[0] <- 1;
 		
 	}
 	 
 /**
  * INVOCATION OF ACTIONS
  */
 	 reflex calling_actions {
 	 	
 	 	// The classic way
 	 	do action: dummy1 with: [a::10, b::100] ;
 	 	
 	 	// Another classic way using facets
 	 	do action: dummy1 a: 10 b: 100;
 	 	
 	 	// Another by removing the first facet
 	 	do dummy1 a:10 b: 100;
 	 	
 	 	// The new alternative one 
 	 	do dummy1 (a:10, b:100);
 	 	// ... which has been introduced to unify the functional/imperative use of actions.
 	 	
 	 	// The "classic" functional call
 	 	list d <- self dummy1 [a::10, b::100];
 	 	// And the new one, closer to the previous imperative syntax
 	 	list d <- self dummy1 (a: 10, b:100);
 	 	
 	 	// This new syntax is not only usable in action calls, but also here, for instance
 	 	
 	 }
 	 
 /**
  * USE OF OPERATORS 
  */
  
  	reflex operators {
  		 
  		// All the operators can now be written using a function-like form
  		// It is true for unary operators (as before)
  		bool a <- flip(0.4);
  		// .. but also for binary ones (in addition to the classic way)
  		int b <- div(100,23) + 100 div 23;
  		geometry c <- union(square(1),circle(10));
  		// The only exception to this rule is that of the built-in operators (like "and"/"or") and the ones whose names are not alphanumeric (like "+")
  		
  		// The big news is that operators are now allowed to be written using any number of parameters (not 1 or 2)
  		// Here is an example with the "between" operator (written specially in Java to test this feature)
  		// @operator(value = "between", can_be_const = true)
		// public static Boolean between(final Integer a, final Integer inf, final Integer sup) {
		//    	if ( inf > sup ) { return false; }
		//		return a >= sup ? false : a > inf; 
		// }
		bool e <- between(5, 1, 10);
  	} 
 /**
  * DEFINITION OF ASPECTS
  */
  
 	aspect default {
 		// draw now accepts any kind of arguments and decides what to do based on the type of the argument
 		draw circle(1);
 		draw name;
 		draw file("image.jpg");
 		
 		// special units (that take a value in the drawing scope) have been introduced to allow for a better control of the display
 		// This statement will draw a circle of radius 10 pixels, whatever the level of zoom and size of the display
 		draw circle (10 °px);
 		draw "Display dimensions: " + °display_width + " " + °display_height;
 	}
 	 
 	 
 }
 
 species spec {}
 
 
 experiment exp1 type: gui {

	output{
		display Display type: opengl{
			// Layers include a new keyword, "graphics", that allows to draw arbitrary shapes using the syntax found in the aspects of species
			// The same capability will be soon added to species/agents layers (in order to define on-the-fly aspects without changing the species
			graphics G {
				draw sphere(10) at:{50, 50};
				if (true) {draw "true";} else {draw "false";}
			}

		}
	}



 }