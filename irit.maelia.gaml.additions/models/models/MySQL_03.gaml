/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement
 *   03: Test executeUpdate with INSERT statement
 */

model MySQL_03

/* Insert your model definition here */

  
global {
			var PARAMS type:map init: ['host'::'localhost','dbtype'::'MySQL','database'::'Students','port'::'3306','user'::'root','passwd'::'root'];

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex insert {
			do action: helloWorld;			 
			do action: executeUpdate{
				arg params value: PARAMS;
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (100, 'Zara', 'Ali', 18)";
 			}
			do action: executeUpdate{
				arg params value: PARAMS;
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (101, 'Mahnaz', 'Fatma', 25)";
 			}
 			do action: executeUpdate{
				arg params value: PARAMS;
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (102, 'Zaid', 'Khan', 30)";
 			}	
			do action: executeUpdate{
				arg params value: PARAMS;
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES(103, 'Sumit', 'Mittal', 28)";
 			}					
		}
	} 
}            