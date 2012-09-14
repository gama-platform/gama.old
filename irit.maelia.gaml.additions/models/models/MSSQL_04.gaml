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

model MSSQL_04

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
		reflex update {
			do action: helloWorld;			 
			do action: executeUpdateDB{ 
				arg dbtype value: "MSSQL";
				arg url value: "localhost";// IP address or computer name
				arg port value: "1433"; 
				arg database value: "Students";
				arg user value: "sa";
				arg passwd value: "tmt";
 				arg updateComm value: "UPDATE Registration " +
                   "SET age = 30 WHERE id in (100, 101)";
 			}
		}
	} 
}      