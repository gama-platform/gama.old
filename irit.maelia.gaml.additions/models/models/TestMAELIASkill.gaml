/**
 *  TestMAELIASkill
 *  Author: bgaudou
 *  Description: 
 * 		Model testing the irit.maelia.gaml.additions package
 * 		Test of the skill.MAELIA 
 */

model TestMAELIASkill
  
global {
	var DB type: string parameter: 'DataBase' init:'../includes/meteo.db';
	
	init {
		create species: toto number: 1 ;
	}
} 
entities { 
	species toto skills: [MAELIA] {
		var listRes type: list init:[];
		
		reflex {
			do action: maeliaWrite;			 

			let t value: self.maeliaInterrogateDB[request::'SELECT * FROM points;', DBName::DB];
			set listRes value: t;	
			do action: write with: [message::t];
		}
	}
}