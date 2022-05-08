/**
* Name: TestWebAddress
* Example of the is_reachable operator to test the reachability of an web address
* Author: Patrick Taillandier 
* Tags: Web address
*/

model WebUtils

global {
	string address_to_test <- "www.google.com";
	int time_out <- 200; // the time, in milliseconds, before the call aborts
	init {
		write "Is address \"" + address_to_test +"\" is reachable: " + (is_reachable(address_to_test, 200));			
	}
	
}

experiment testWebAddress type: gui ;
