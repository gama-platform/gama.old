/***
* Name: CreateSimuGraph2
* Author: Benoit Gaudou
* Description: Loads a simulation from a file
* Tags: serialization, load_file
***/

model CreateSimuGraph2

import "Base Model.gaml"


experiment "1. Save Simulation" type: gui parent: Base {
	
	
	string format <- "binary";
	string file_path <- "../includes/saved_simulation.simulation";
	
	text "Run the simulation until cycle 5, when it will be saved in a file and quit" font: font("Helvetica", 14, #bold);
	parameter "File path" var: file_path ;
	
	reflex store when: cycle = 5 {		
		save simulation to: file_path format: format;
		do die;	
	}	

}

experiment "2. Reload Simulation" type: gui parent: Base{
	
	simulation_file input <- file("../includes/saved_simulation.simulation");
	
	text "This experiment has created its initial simulation from the serialized version of the previous simulation saved in the file" font: font("Helvetica", 14, #bold);
	parameter "File to read" var: input <- file("../includes/saved_simulation.simulation");
	
	// We create the initial simulation from the file
	action _init_ {
		create simulation from: input;	
	}

}

experiment "3. Restore Simulation" type: gui parent: Base{
	
	simulation_file input <- file("../includes/saved_simulation.simulation");
	
	text "This experiment has created its initial simulation normally, and uses `restore` at step 10 to initialise it from the serialized version of the previous simulation. This creates an endless loop !" font: font("Helvetica", 14, #bold);
	parameter "File to read" var: input <- file("../includes/saved_simulation.simulation");
	
	// We "restore" the simulation from the file. As it happens each time the simulation reaches 10 cycles, it loops forever between 5 and 10 cycles. 
	reflex when: cycle=10 {
		restore simulation from: input;	
	}

}