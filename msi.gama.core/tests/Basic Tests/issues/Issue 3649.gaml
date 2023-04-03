/**
* Name: Issue3649
* Model created to test Issue #3649
* See https://github.com/gama-platform/gama/issues/3649
* Author: A. Drogoul
* Tags: batch, abort
*/


model Issue3649

global {
	
	abort {
		// This sentence should be written whatever the type of experiment used (GUI or Batch)
		write "The simulation is aborted";
	}
}


experiment 'Verify me in GUI' {
	init {
		ask simulations {
			do die;
		}
	}
}

experiment 'Verify me in Batch' type: batch until: cycle > 1 autorun: true keep_simulations: false;
