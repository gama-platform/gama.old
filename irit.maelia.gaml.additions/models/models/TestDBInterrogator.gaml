/**
 *  TestDBInterrogator
 *  Author: bgaudou
 *  Description: 
 * 		Model testing the irit.maelia.gaml.additions package
 * 		Test of the species.DBInterrogator 
 */

model TestDBInterrogator 
  
global {
	var DB type: string parameter: 'DataBase' init:'../includes/meteo.db';
	
	init {
		//create species: DBInterrogator number: 1 {
		//	do action: activate with: [DBName::DB];	
		//}
		
		//create species: skilledAgent number: 1;
		
		//create species: inheritantAgent number: 1{
			//do action: activate with: [DBName::DB];					
			//let t value: self maeliaInterrogateDB[request::"SELECT id_point, temp_min FROM points WHERE month='1' AND day='14';"];
			//set listRes value: t;			
			//do action: write with: [message:: t];
		//}
	} 
} 

entities { 
	species skilledAgent skills: [MAELIA] {
		var listRes type: list init:[];
		
		init {		
			//ask target: list(DBInterrogator) {
			//	let t value: self maeliaInterrogateDB[request::"SELECT id_point, temp_min FROM points WHERE month='1' AND day='14';"];
			//	set myself.listRes value: t;					 
			//}		
		}
	}
	
	species inheritantAgent parent: DBInterrogator {
		var listRes type: list init:[];		
	}
}