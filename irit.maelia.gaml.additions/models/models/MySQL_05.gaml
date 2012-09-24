/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement
 *   03: Test executeUpdate with INSERT statement
 *   04: Test executeUpdate with UPDATE statement
 *   05: Test executeUpdate with DELETE statement
 */

model MySQL_05

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
		reflex delete{
			do action: helloWorld;			 
			do action: executeUpdate{
				arg params value: PARAMS;
 				arg updateComm value: "DELETE FROM Registration " +
                   					   "WHERE id = 101";
 			}
		}
	} 
}      