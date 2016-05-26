/**
* Name: serial3D
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model serial3D

global {
//	geometry g <- cube(3);
//	geometry r;
	init {
		create people number: 1 {
			shape <- cube(1);
		}
	}
	
//	reflex t {
//		string s <- serialize(g);
//		write s;
//		r <- unserialize(s);
//		write "" + r;
//		write serialize(r);
//	}	
}

species people {
	reflex sh {
	//	geometry myShape <- shape;
	//	create people with: [shape::myShape, location::any_location_in(world)];
		
		shape <- cube(cycle+1);
		
	}
	
	reflex r {
//		write "SHHHHHHHHHHHHHHHHHHHHHHHHHHHAPE";
////		write serialize(shape);
//		write "SHHHHHHHHHHHHHHHHHHHHHHHHHHHAPE";
		
//		write serialize(unserialize(serialize(shape)));

//		write "SHHHHHHHHHHHHHHHHHHHHHHHHHHHAPE";
		
//		write serialize(unserialize(serialize(cube(3))));
		
	}
	
	aspect asp {
		draw shape color: #blue;
	}
}

experiment smpleSerial type: gui {
	output {
		display d type: opengl { species people aspect: asp; }
	}	
}

experiment mem type: memorize {
	reflex store when: true { //when: (cycle < 5){	
		write "================ store " + self + " - " + cycle;
		write serialize(self.simulation);	
	}
	output {
		display d type: opengl { species people aspect: asp; }
	}	
}

experiment serial3D type: gui {
	
	list<string> history <- list<string>([]);
	
	reflex store when: true { //when: (cycle < 5){	
		write "================ store " + self + " - " + cycle;
		string serial <- serialize(self.simulation);
		add serial to: history;
	//	write serial; 
		write "================ END store " + self + " - " + cycle;		
		//write serializeSimulation(cycle);
	}
	
	reflex restore when: (cycle = 1){
		write "================ START SAVE + self " + " - " + cycle ;		
//		write "Save of simulation : " + saveSimulation('file.xml');
		write "================ RESTORE + self " + " - " + cycle ;			
		int ii <- unSerializeSimulation(string(history[0]));
		write "================ RESTORE + self " + " - " + cycle ;		
	} 	
	output {
		display d type: opengl
		{
			species people aspect: asp;
		}
		
		
		
	}
}
