/**
* Name: serialGrid
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model serialGrid

global {
	init {
		create people;
	}
	reflex t {
		string s <- serialize(people);
		write s;
		unknown o <- unserialize(s);
		write "" + o;
	}
}
species people;

grid cell height: 1 width: 1 {
	rgb color <- rnd_color(255);
	
	reflex t {
		color <- rnd_color(255);
	}
}

experiment noMem type: gui {
	
	output {
		display d type: opengl {
			grid cell;
		}
	}	
}

experiment mem type: memorize {
	output {
		display d type: opengl {
			grid cell;
		}
	}	
}

experiment serialGrid type: gui {
	list<string> history <- list<string>([]);

	reflex store { //when: (cycle < 5){	
		write "================ store " + self + " - " + cycle;
		string serial <- serializeAgent(self.simulation);
		add serial to: history;
		write serial;
		write "================ END store " + self + " - " + cycle;		
		//write serializeSimulation(cycle);
	}
	
	reflex restore when: (cycle = 1){
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + saveSimulation('file.xml');
		write "================ RESTORE + self " + " - " + cycle ;			
		int ii <- unSerializeSimulation(string(history[0]));
		write "================ RESTORE + self " + " - " + cycle ;		
	} 
		
	output {
		display d type: opengl {
			grid cell;
		}
	}
}
