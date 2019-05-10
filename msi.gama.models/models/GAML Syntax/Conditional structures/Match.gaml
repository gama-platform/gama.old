/***
* Name: Match
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model Match

global {
	init {
		write "" + (date('2000-01-01') to date('2010-01-01'));
	}
}

experiment exp type: gui {

	
	// Define parameters here if necessary
	// parameter "My parameter" category: "My parameters" var: one_global_attribute;
	
	// Define attributes, actions, a init section and behaviors if necessary
	// init { }
	
	
	output {
	// Define inspectors, browsers and displays here
	
	// inspect one_or_several_agents;
	//
	// display "My display" { 
	//		species one_species;
	//		species another_species;
	// 		grid a_grid;
	// 		...
	// }

	}
}