/**
 *  SQLITE_01
 *  Author: thaitruongminh
 *  Description: select from meteo.db by SQlite
 */

model SQLITE_01

/* Insert your model definition here */

  
global {
    var PARAMS type:map init: ['dbtype'::'sqlite','database'::'../includes/meteo.db'];
    
	init {
		create species: toto number: 1 ;
	}
}   

entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj; 
		reflex select{
			do action: helloWorld;	
			if (self testConnection[ params::PARAMS])		
			{
				do action: write with: [message::"Connection is OK"] ;
	 			let t value: self select[params::PARAMS, select::"SELECT * FROM POINTS ;"]  ;				
 				set listRes value: list(t);	
				// listRes(0): List of column name
				// listRes(1): List of column name type 	
				// listRes(2): List of Record -> List of List type
				do action: write with: [message::t];
			}else{
				do action: write with: [message::"Connection is false"] ;
			}
			
		}
	}
}
    