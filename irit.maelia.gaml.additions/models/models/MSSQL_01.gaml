/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model MSSQL_01
  
global {

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		reflex createDBMS{
			do action: helloWorld;			 
			do action: executeUpdateDB{
				arg dbtype value: "MSSQL";
				arg url value: "localhost";// IP address or computer name
				arg port value: "1433"; 
				arg database value: "";
				arg user value: "sa";
				arg passwd value: "tmt";
 				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
		}
	} 
}      