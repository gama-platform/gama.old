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
 *   06: Test executeQuery with SELECT statement
 *   07: Test executeUpdate with DROP TABLE statement
 *   08: Test executeUpdate with DROP DATABASE statement
 */

model MSSQL_08

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
		reflex dropDatabase {
			do action: helloWorld;			 
			// 
			do action: executeUpdateDB{ 
 				arg dbtype value: "SQLSERVER";
				arg host value: "localhost";// IP address or computer name
				arg port value: "1433"; 
				arg database value: "";
				arg user value: "sa";
				arg passwd value: "tmt";
 				arg updateComm value: "DROP DATABASE STUDENTS";
 			}
		}
	} 
}      