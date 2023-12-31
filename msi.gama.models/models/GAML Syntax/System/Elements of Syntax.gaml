/**
* Name: Syntax
* Author: Alexis Drogoul
* Description: An overview of the new syntactic constructs that have been introduced in GAMA 1.6
* Tags: attribute, ternary, equation, action, container, list
*/

@no_warning
model syntax

/**
 * The global section can now sport skills and control, just like other agents. 
 * The example below shows a potentially moving world that can be controlled by a finite state machine.
 */
// TODO ToDo comments can now be added; they appear in the Validation view.
global skills: [moving] control: fsm {

/**
 * EQUATIONS
 */ 
	float x;
	float y;
	float t;
	
	equation eq {
		diff(x, t) = x / 2;
		diff(y, t) = x + y * 2;
	}

	reflex solving {solve eq step: 1 method: #rk4;}

	/** 
 * ATTRIBUTES
 */

// Attributes can be declared in different ways, ranging from "classic"...
	list a1 of: int const: true init: [1, 2, 3] ;
	list a2 <- [1, 2, 3] of: int;
	// ... to "compact" Java-like syntax.
	list<int> a3 <- [1, 2, 3];

	// The declaration of a default size/value in containers is now working correctly
	list<float> f <- list_with(2000,0.0);

	// Functions can be declared using the regular facet "->" / "function:" 
	int b1 ->  100 + length(a1) ;
	int b2 -> { 100 + length(a1) };
	// ... or using a block (like a statement -- note the absence of semi-column at the end)
	int b3 {
		return 100 + length(a1);
	}
	state first_state initial: true {
	//...

	}
	init {
		create species0 number: 20;
	}
	

	/**
	 * UNITS
	 */
// The usage of units is improved and they can be combined
	float c <- 10 #meter;
	float speed <- 10 #meter / #sec;

	// Attributes can (finally!) be named after unit names
	float meter <- 10.0;

	// Some mathematical constants have been introduced alongside units
	float pi_4 <- #pi / 4;
	float e <- #e;

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
	list<int> dummy1 (int a <- 100, float b) {
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

	// Temporary variables can use the same syntax as attributes. The classic form:
		//let name: t1 type: int value: length(a1);
		// is equivalent to the more compact one:
		int t2 <- length(a1);

		// Assigning a value to variables is also sporting a new syntax 
		set t2 value: 100;
		// ... can be replaced by 
		set t2 <- 100;
		// ... or even by
		t2 <- 100;

		// Species can now act as direct containers of their agents..
		list<species0> spec_with_location <- species0 select (each.location = { 0, 0 });
		species0 agent0 <- species0[10];
		write string(agent0);

		// ...  and agents as direct containers of their attributes (mimicking the internal attributes map). This "virtual map" will now contain, in addition to the attributes, 
		// all the variables loaded from CSV, SQL or shape files (some stored in the agent itself, others in the shape).
		agent0["departure"] <- { 0, 0 };
		write string(agent0);

		// Keys are not necessarily strings, by the way ! A warning is emitted in such cases, but it is just a warning.
		agent0[0] <- 0;

		// Accesses can be combined
		species0[10].name <- "A new name"; // which is equivalent to (spec at 10).name = "A new name";

		// Points can be used (with or without curly parentheses) to access agents in species
		species0 agent1 <- species0[10, 10]; // returns the agent closest to point {10,10} for a "regular" species
		// The behavior is a bit tweaked for grids, to allow for a "natural" access
		my_grid cell0 <- my_grid[10, 10]; // Here, it is the cell at {10, 10} in the matrix of cells

		// Shapes also act as containers of CVS/Shapefile attributes (in case they are loaded without being attributed to an agent), as well as 3D properties (for the display).
		agent0.name <- string(agent0.shape["ID"]);
		geometry geom <- square(100);
		geom["type"] <- "cube";
		
		
		// All these attributes can be accessed using the casting of agents to map
		write string(map(agent0));

		//This access can be used everywhere, easing the use of shape files (and data files in general)
		list<geometry> shapes <- list<geometry>(file("includes/something.shp"));

		// If we suppose val1, val2, etc. are defined in the shape file
		float max <- min(shapes collect float(each["val1"]));

		//To allow for an easier access in case the modeler only uses agents, agents' attributes can "pass through" towards their shape's attributes 
		//(in case the same attribute has not been defined in the agent, of course)
		create species0 from: shapes;
		max <- min(species0 collect float(each["val1"])); // equivalent to each.shape["val1"]. 
		//This last sentence only works, however, for *reading* values.
		any(species0)["val1"] <- 100; // will result in the creation of a new attribute in the agent (not in its shape)
		any(species0).shape["val1"] <- 100; // will be correct in that case

		// Container variables have seen their usability clearly improved 
		map m <- map([]);
		list<int> l <- [1, 2, 3, 4, 5];

		// Adding a value
		add 1 to: l;
		// ... can now be written
		l <+ 1;

		// Removing a value
		remove 1 from: l;
		remove "type" from: geom;

		// ... can now be written
		l >- 1;
		geom >- "type";
		any(species0)[] >- "name"; // removes the "name" attribute from a random agent. Can be dangerous in some cases... 

		// Setting/putting a value
		put "a" at: 'key' in: m;
		put 1 at: 0 in: l;
		// ... can now be written
		m['key'] <- "a";
		l[0] <- 1;
		list<list> ll <- [];
		ll <+ []; // [[]]
		ll[0] <+ 10; // [[10]]

	}

	// Species can now be written within the "global" section (to enforce the idea that the top-level species are indeed contained in the world)
	species inside_global {
	}

	/**
	  * INVOCATION OF ACTIONS
	  */
	reflex calling_actions {
	// IN IMPERATIVE MODE (i.e. in a statement)
	// The classic way
		do dummy1 with: [a::10, b::100.0];

		// Another by distributing the arguments
		do dummy1 a: 10 b: 100.0;

		// The new alternative one 
		do dummy1(a: 10, b: 100.0);
		ask any(species1) {
			do goto(target: { 10, 10 }, speed: 100);
		}
		// ... which has been introduced to unify the functional/imperative use of actions.

		// And finally the new functional way, probably reserved to simple calls (as all the arguments must be passed).
		do dummy1(10, 100.0);

		// IN FUNCTIONAL MODE (i.e. as part of expressions)
		// The "classic" way of calling actions. Note that in that case, dummy1 is used like a binary operator (callee on the left, argument map on the right)
		//list d1 <- self dummy1 [a::10, b::100.0];

		// First improvement, argument maps can now be simplified, which results in a functional syntax with named arguments
		//list d2 <- self dummy1 (b: 100.0); // a is not passed as it has a default value.

		// To improve the readability of this way of calling actions, the dotted notation is now allowed as well 
		list d3 <- self.dummy1(a: 100, b: 100.0);
		float s <- any(species1).compute_speed_using_an_action(max: 100);

		// Finally, the functional syntax is also introduced. In that case, all the arguments need to be passed as they are not named.
		// This unifies the way of calling operators and actions furthermore. 

		// The action can be called as a n_ary operator, and in that case, the callee is implicitely the agent that executes the call
		list d4 <- dummy1(10, 100.0);

		// And it can also be called using the "dotted" syntax, in which case the callee needs to be explicit (can be "self" of course)
		path p <- world.move(100, 45, shape); // speed, heading, bounds


		/**
 	 	 *  As a summary, the syntaxes kept for future developments of models will likely be: 
 	 	 */

		// CALLING WITH NAMED ARGUMENTS + OPTIONAL DOTTED SYNTAX IN EXPRESSIONS + IMPLICIT CALLEE IN CASE OF SELF
		// This method is convenient as it allows to pass only some arguments (if defaults are defined, which is implicitely the case in primitives), 
		// but also to pass them in any order
		do wander(speed: 100, amplitude: 10);
		path p1 <- path(self.wander(amplitude: 10, speed: 100));
		//path p2 <- path(self wander (speed: 100, bounds: square(10)));
		path p3 <- path(wander(speed: 100));
		path p4 <- path(wander());

		// CALLING WITH COMPLETE ARGUMENTS + OPTIONAL DOTTED SYNTAX IN EXPRESSIONS + IMPLICIT CALLEE IN CASE OF SELF
		// This method is convenient as it follows the functional syntax of operators and then allows to declare "quasi-operators" in species, even to redefine existing ones.
		do wander(100, 100, self.shape); // speed, amplitude, bounds as defined in primitive wander
		int d5 <- self.max(10, 100);
		//int d6 <- self max (10, 100);
		int d7 <- max(10, 100);
		list others <- filter(species1);

		// As a side note, the new syntax for arguments maps is not only usable in action calls, but also in create, for instance
		create species0 with: (location: { 100, 100 }, name: "");
	}

	// The ternary operator "(condition) ? statement_if_condition_true : statement_if_condition_false" can be used as follow :
	int max (int i, int j) {
		return i > j ? i : j;
	}

	list<agent> filter (container<agent> my_agents) {
		return my_agents where (each.location = nil);
	}

	/**
	  * USE OF OPERATORS 
	  */
	reflex operators {

	// All the operators can now be written using a function-like form
	// It is true for unary operators (as before)
		bool a <- flip(0.4);
		// .. but also for binary ones (in addition to the classic way)
		int b <- div(100, 23) + 100 div 23;
		geometry c1 <- union(square(1), circle(10));
		// The only exception to this rule is that of the built-in operators (like "and"/"or") and the ones whose names are not alphanumeric (like "+")

		// The big news is that operators are now allowed to be written using any number of parameters (not 1 or 2)
		// Here is an example with the "between" operator (written specially in Java to test this feature)
		// @operator(value = "between", can_be_const = true)
		// public static Boolean between(final Integer a, final Integer inf, final Integer sup) {
		//    	if ( inf > sup ) { return false; }
		//		return a >= sup ? false : a > inf; 
		// }
		bool e1 <- between(5, 1, 10);
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
		draw circle(10 #px);
		draw "Display dimensions: " + #display_width + " " + #display_height;
	}

}

/** Species can "mirror" a list of agents (or another species). That is, their population is dynamically computed after the list or species mirrored.
* Their instances, which are actually "proxy" agents, possess an attribute called "target" that points towards the agent they mirror. Very useful for building graphs, for instance.
* The update of the population tries to preserve, as much as possible, the existing mapping (that is, proxy agents do not change targets if they do not die or disappear from the list) */
species species0 {
	float speed <- float(rnd(1000));
}

species species1 mirrors: species0 skills: [moving] {
	point location update: target.location + { 10, 10 };
	float speed1 update: self.compute_speed_using_an_action(); // No parameter as "max" is defaulted
	float speed2 update: compute_speed_using_a_functional_attribute;
	float compute_speed_using_a_functional_attribute {
		return speed of target;
	}
	float compute_speed_using_an_action (int max <- 100) {
		return min([max, int(speed of target)]);
	}

	init {
		write "I am " + self.name + " and my target is " + target.name;
	}

}

grid my_grid width: 100 height: 100 {
}

experiment "Run this experiment" type: gui {
	output {
		display Display type: 3d {
		// Layers include a new keyword, "graphics", that allows to draw arbitrary shapes using the syntax found in the aspects of species
		// The same capability will be soon added to species/agents layers (in order to define on-the-fly aspects without changing the species itself)
			graphics 'G' {
				draw sphere(10) at: { 50, 50 };
				if (true) {
					draw "true";
				} else {
					draw "false";
				}

			}

		}

	}

}
