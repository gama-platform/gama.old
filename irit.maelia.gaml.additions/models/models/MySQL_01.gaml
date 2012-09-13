/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model MySQL_01
  
global {

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex createDatabase {
			do action: helloWorld;			 
			do action: executeUpdateDB{
				arg dbtype value: "MySQL"; 
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "";
 				arg user value: "root";
 				arg passwd value: "root";
 				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
		}
	} 
}      