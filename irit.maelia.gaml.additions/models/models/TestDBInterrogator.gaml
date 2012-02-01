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
		create species: DBInterrogator number: 1 {
			do action: activate with: [DBName::DB];	
		}
		create species: toto number: 1;
	}
} 
entities { 
	species toto skills: [MAELIA] {
		var listRes type: list init:[];
		
		init {		
			ask target: list(DBInterrogator) {
				let t value: self.maeliaInterrogateDB[request::"SELECT id_point, temp_min FROM points WHERE month='1' AND day='14';"];
				set myself.listRes value: t;					 
			}		
		}
	}
}