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
 				arg dbtype value: "MySQL"; 
 				arg host value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "students";
 				arg user value: "root";
 				arg passwd value: "root";
 				arg updateComm value: "UPDATE Registration " +
                   "SET age = 30 WHERE id in (100, 101)";
 			}
		}
	} 
}      