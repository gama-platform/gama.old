/**
* Name: Model1
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model Model1

global {
	int toot <- 0;
	string s <- "test";
}

experiment Model1 type: gui {

	list<string> history <- [];

	reflex store { 
		write "================ store " + self + " - " + cycle;
		string serial <- serializeAgent(self.simulation);
		write serial;
		write "================ END store " + self + " - " + cycle;		
	}

	output {
		
	}
}
