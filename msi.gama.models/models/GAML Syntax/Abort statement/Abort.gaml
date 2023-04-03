/**
* Name: Abort
* Shows the usage of 'abort' and 'init'
* Author: drogoul
* Tags: abort, init
*/
model Abort

global {

	init {
		write "Simulation executes 'init' and creates one agent of species a";
		create a;
		write "Simulation kills itself";
	}

	reflex {
		write "Simulation does nothing";
	}

	abort {
		write "Simulation executes 'abort' and kills the agents of species a";
		ask a {
			do die;
		}

	}

}

species a {

	init {
		write "Agent of species a executes init";
	}

	reflex {
		write "Neither does agent of species a";
		if (user_confirm("Close simulation", "Should we close the simulation ?")) {
			ask world {
				do die;
			}

		}

	}

	abort {
		write "Agent of species a executes abort";
	}

}

experiment "Run me" {
	abort {
		do tell("You are now leaving this experiment. Hope you enjoyed it ! ", false);
	}

}

