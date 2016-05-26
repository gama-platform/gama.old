/**
* Name: Test3D
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model Test3D

global {
	init {
		create people {
			shape <- cube(3);
		}
	}
	
	reflex t {
		write serialize(first(people));	
		write "        " ;
		
		write serialize(first(people).shape);
		
		write "        " ;
		
		
		write serialize(first(people));	
		
	}
}

species people;


experiment test3DMeme type: memorize {}

experiment Test3D type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
	}
}
