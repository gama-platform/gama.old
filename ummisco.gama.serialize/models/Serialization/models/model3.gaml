/**
* Name: allTypes
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tag : Tag1, Tag2, TagN
*/

model allTypes

global {
	
	init {
		create people;	
	}
	
	reflex t when: true {
		write "people(0) " + serialize(first(people));
		write "people(0) " + unserialize(serialize(first(people)));		

		write "people " + serialize(people);
		write "people " + unserialize(serialize(people));						
	}
	
}

species people {}

experiment simple type: gui {}

experiment allTypes type: gui {
	
	list<string> history <- list<string>([]);

	reflex store { //when: (cycle < 5){	
		write "================ store " + self + " - " + cycle;
		string serial <- serialize(self.simulation);
		add serial to: history;
		write serial;
		write "================ END store " + self + " - " + cycle;		
	}
	
	reflex restore when: (cycle = 3){
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + saveSimulation('file.xml');
		write "================ RESTORE + self " + " - " + cycle ;			
		int ii <- unSerializeSimulation(string(history[0]));
		write "================ RESTORE + self " + " - " + cycle ;		
	} 
	
	output {
		display f {
			species people;
		}
	}
}
