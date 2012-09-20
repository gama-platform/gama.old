/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   08: Test executeUpdate with DROP DATABASE statement
 */

model MySQL_executeUpdate

/* Insert your model definition here */

  
global {
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'','user'::'root','passwd'::'root'];

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex dropDatabase{
			do action: helloWorld;			 
			// 
			do action: executeUpdate{ 			 
				arg params value: PARAMS; 
 				arg updateComm value: "DROP DATABASE STUDENTS";
 			}
		}
	} 
}

