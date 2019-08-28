/***
* Name: CreateSimuFromFileSimu
* Author: Benoit Gaudou
* Description: Loads a simulation from a file
* Tags: serialization, load_file
***/

model CreateSimuFromFileSimu

global {
	int toot <- 0;
	string s <- "test";
	list<people> pList <- [];
	pair<people,people> pPair ;
	map<people, people> pMap <- map([]);
	list<list<people>> plistlist <- [];
	graph g;
	graph g2;
	path non_spatial_path ;
	path spatial_path;
	
	init {
		create people number: 5;
		add people(0) to: pList;
		add people(2) to: pList;
		add people(4) to: pList;	
		
		add pList to: plistlist;
		add pList to: plistlist;
		
		pPair <- people(1)::people(3);	
		
		put people(1) key: people(0) in: pMap;
		put people(2) key: people(1) in: pMap;
		put people(3) key: people(2) in: pMap;
		
		g <- graph([{1,6},{5,8},{10,10}]);
		g <- g add_edge ({1,6}::{5,8});
		g <- g add_edge ({5,8}::{10,10});
		
		non_spatial_path <- g path_between({5,8},{10,10});
		
		g2 <- graph([people(0),people(1),people(2)]);
		g2 <- g2 add_edge (people(0)::people(1));
		g2 <- g2 add_edge (people(1)::people(2));
		
		spatial_path <- g2 path_between(people(1),people(2));
		
	}
}

species people {
	int t;
	list<int> lo <- [1,2,3];
}

experiment saveSimu type: gui {
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + save_simulation('simpleSimuList.gsim');
		write "================ RESTORE + self " + " - " + cycle ;		
	}	
}

experiment reloadSingleSimu type: gui {
	
	action _init_ {
		create simulation from: saved_simulation_file("simpleSimuList.gsim");	
		write "init simulation at step " + simulation.cycle;		
	}
}