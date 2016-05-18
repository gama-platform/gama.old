/**
* Name: allTypes
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tag : Tag1, Tag2, TagN
*/

model allTypes

global {
	int i <- 0;
	float f <- 3.0;
 	string s <- "hello";
	unknown u <- "bonjour";
	point po <- {4.6, 5.9, 10.9};
	rgb co <- rnd_color(255);
	geometry ge <- polyline([{34,67},{23,90}]); 
		
	list<int> li <- [1,2,7];
	// revoir matrix ...
	matrix<int> m <- matrix([[1,2],[3,4]]);
	map<string,int> mmmp <- map(["a"::1,"b"::6]);
	map<string, list<int>> mp <- map(['hello'::[1,2], 'titi'::[5,6]]);
	pair<int,string> p <- 3.4::"toto";

	graph g <- graph([]);
//	file fi ;
	file f_csv <- csv_file("../includes/Bary_farmers_list.csv",",",true);
	
	// path
	// topology 	
	
	init {

	}
}

experiment allTypes type: gui {
	
	list<string> history <- list<string>([]);

	reflex store { //when: (cycle < 5){	
		write "================ store " + self + " - " + cycle;
		string serial <- serializeAgent(self.simulation);
		add serial to: history;
		write serial;
		write "================ END store " + self + " - " + cycle;		
		//write serializeSimulation(cycle);
	}
	
	reflex restore when: (cycle = 3){
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + saveSimulation('file.xml');
		write "================ RESTORE + self " + " - " + cycle ;			
		int ii <- unSerializeSimulation(string(history[0]));
		write "================ RESTORE + self " + " - " + cycle ;		
	} 
	
	output {}
}
