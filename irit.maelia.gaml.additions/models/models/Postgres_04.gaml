/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement
 *   03: Test executeUpdate with INSERT statement
 *   04: Test executeUpdate with Update statement
 */

model MySQL_04

/* Insert your model definition here */

  
global {
	//var PARAMS type:map init: ['host'::'localhost','dbtype'::'MySQL','database'::'Students','port'::'3306','user'::'root','passwd'::'root'];
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'Postgres','database'::'students','port'::'5432','user'::'postgres','passwd'::'tmt'];
	
	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex update {
			do action: helloWorld;			 
			do action: executeUpdate{
				arg params value: PARAMS;
  				arg updateComm value: "UPDATE Registration " +
                   "SET age = 30 WHERE id in (100, 101)";
 			}
		}
	} 
}  