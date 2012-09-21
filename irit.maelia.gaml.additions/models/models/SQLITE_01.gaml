/**
 *  SQLITE_01
 *  Author: thaitruongminh
 *  Description: select from meteo.db by SQlite
 */

model SQLITE_01

/* Insert your model definition here */

  
global {

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
			// Select with no WHERE con dictions
 			do action: selectDB{  
				arg dbtype value: "sqlite";
				arg host value: "";// IP address or computer name
				arg port value: ""; 
				arg database value: "../includes/meteo.db";
				arg user value: "";
				arg passwd value: "";
 				arg select value: "SELECT * FROM POINTS ;";
 			}
 
 			// get result from Select From Where statement
 			let t value: self selectDB[ 						
 						dbtype::"sqlite",
 						host :: "localhost", 
 						port :: "1433",
 						database :: "../includes/meteo.db",
 						user:: "",
 						passwd:: "",
 						select::"SELECT * FROM POINTS ;"
 			];
			set listRes value: list(t);	
			// listRes(0): List of column name
			// listRes(1): List of column name type 	
			// listRes(2): List of Record -> List of List type
			do action: write with: [message::t];
		}
	}
}
    