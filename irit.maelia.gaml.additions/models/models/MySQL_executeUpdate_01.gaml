/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model MySQL_executeUpdate
  
global {
	var PARAMS type:map init: ['url'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'','user'::'root','passwd'::'root'];

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex createDatabase {
			do action: helloWorld;			 
			do action: executeUpdate{
				arg params value: PARAMS; 
 				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
		}
	} 
}      