/**
* Name: TestRecursion
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model TestRecursion

global {
	init {
		create people;	
		
		ask people {
			ami <- self;
		}
	}
	
	reflex t {
		write serialize(people(0));
	}
	
}

species people {
	people ami;
}

experiment TestRecursion type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
	}
}
