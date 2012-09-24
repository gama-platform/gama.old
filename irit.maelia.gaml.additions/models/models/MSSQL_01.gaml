/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model MSSQL_01
  
global {
		var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];

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