/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement

 */

model MSSQL_02

/* Insert your model definition here */

  
global {
		var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'Students','port'::'1433','user'::'sa','passwd'::'tmt'];

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[]; 
		reflex createTable{
			do action: helloWorld;			 
			do action: executeUpdate{
				arg params value: PARAMS;
				arg updateComm value: "CREATE TABLE REGISTRATION " +
                   "(id INTEGER not NULL, " +
                   " first VARCHAR(255), " + 
                   " last VARCHAR(255), " + 
                   " age INTEGER, " + 
                   " PRIMARY KEY ( id ))";
 			}
		}
	} 
}      