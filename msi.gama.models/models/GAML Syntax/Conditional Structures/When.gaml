/***
* Name: reflex_when
* Author: kevinchapuis
* Description: Gives few example on how to use when facets of reflex statement
* Tags: reflex, when, condition
***/

model reflex_when

global {
	
	int no_condition;
	int nb_simple_when;
	int nb_composed_when;
	
	geometry cr2_area <- polygon([{0,0},{50,0},{50,50},{0,50}]);
	
	init {
		create dummy;
	}
	
	reflex r {
		no_condition <- no_condition+1;
	}
	
	/*
	 * You can use any operator that return a boolean to trigger a reflex 
	 * 
	 * Result: Do the reflex when flip(0.5) is true
	 */
	reflex cr when:flip(0.5) {
		nb_simple_when <- nb_simple_when+1;
		write "this reflex is activated "+round(nb_simple_when/(cycle+1)*100)+"% of the time";
	}
	
	/*
	 * You can use a combination of conditions to build more complex reflex trigger
	 * 
	 * Result: Do the reflex when flip(0.1) is true AND the first dummy agent is below coordinate {50,50}
	 */
	reflex cr2 when:flip(0.1) and first(dummy).location < {50,50} {
		nb_composed_when <- nb_composed_when + 1; 
		write "You've been lucky "+nb_composed_when+" times";
	}
	
	/*
	 * You can use as many conditions as necessary and combine them using logical operator
	 * like 'and' and 'or'. A reflex can be schedule in detailed and can be very unlikely
	 *  
	 */
	reflex cr3 when:(flip(0.01) and every(100#cycle) and (nb_composed_when mod 7 = 0)) or every(#year) {
		write "Jackpot after "+no_condition+" cycles";
	}
	
}

species dummy {
	reflex move {location <- any_location_in(world);}
	aspect default {draw circle(1) color:#crimson;}
}

experiment my_xp {
	output {
		display my_display type:3d{
			graphics area transparency:0.3{
				draw cr2_area color:#darkblue;
				draw string(nb_composed_when) at:{25,25} font:font("Helvetica",60,#bold) color:#white;
			}
			species dummy;
		}
	}
}

