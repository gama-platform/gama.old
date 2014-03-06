/**
 *  Casting: different ways of transforming objects and agents in GAML
 *  Author: drogoul
 *  Description: 
 */

model Casting

species to_int {
	init {
		write sample(int(1));
		write sample(int(1.0));
		write sample(int("1"));
		//ERROR write sample(int("1.0"));
		write sample(int(°pink));
		write sample(int(true));
		write sample(int(self));
		write sample(int([]));
		write sample(int([0]));
		write sample(int({0,0,0}));
		write sample(int(#AABBCC));
		write sample(int(°meter));				
		
	}
}

experiment Casting type: gui{
	user_command "to int" {create to_int;}
}