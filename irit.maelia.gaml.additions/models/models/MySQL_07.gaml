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
 */

model MySQL_07

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
		reflex dropTable {
			do action: helloWorld;			 
			// 
			do action: executeUpdateDB{ 
 				arg dbtype value: "MySQL"; 
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "students";
 				arg user value: "root";
 				arg passwd value: "root";
 				arg updateComm value: "DROP TABLE REGISTRATION ";
 			}
		}
	} 
}      