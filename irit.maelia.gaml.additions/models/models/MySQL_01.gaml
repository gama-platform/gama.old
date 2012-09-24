/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model MySQL_01
  
global {
			var PARAMS type:map init: ['host'::'localhost','dbtype'::'MySQL','database'::'','port'::'3306','user'::'root','passwd'::'root'];

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		reflex createDBMS{
			do action: helloWorld;			 
			do action: executeUpdate{
				arg params value: PARAMS;
				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
		}
	} 
}          