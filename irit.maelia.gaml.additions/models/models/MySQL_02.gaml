/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement

 */

model MySQL_02

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
		reflex createTable{
			do action: helloWorld;			 
			do action: executeUpdateDB{ 
				arg dbtype value: "MySQL"; 
 				arg host value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "students";
 				arg user value: "root";
 				arg passwd value: "root";
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