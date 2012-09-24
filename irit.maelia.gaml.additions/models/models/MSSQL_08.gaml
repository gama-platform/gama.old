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
var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];
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
			do action: executeUpdate{
				arg params value: PARAMS;
 				arg updateComm value: "DROP DATABASE STUDENTS";
 			}
		}
	} 
}      