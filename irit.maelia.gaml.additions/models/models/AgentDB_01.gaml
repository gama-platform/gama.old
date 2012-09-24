/**
 *  
 *  Author: bgaudou
 *  Description: 
 * 		Model testing the irit.maelia.gaml.additions package
 * 		Test of the species.DBInterrogator 
 */

model AgentDB_01 
  
global {
	var DB type: string parameter: 'DataBase' init:'../includes/meteo.db';
	var PARAMS type:map init: ['dbtype'::'sqlite','database'::'../includes/meteo.db'];
	init {
		create species:AgentDB number: 1 {
			do action: connect with: [params::PARAMS];	
		}
		
		create species: inheritantAgent number: 1{
			do action: connect with: [params::PARAMS];					
			let t value: self select[select::"SELECT id_point, temp_min FROM points WHERE month='1' AND day='14';"];
			set listRes value: t;			
			do action: write with: [message:: listRes];
		}
	} 
} 

entities { 
	
	species inheritantAgent parent: AgentDB {
		var listRes type: list init:[];		
	}
}