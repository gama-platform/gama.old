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
 */

model SQLConnection_05

/* Insert your model definition here */

  
global {

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [MAELIADBMS] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex {
			do action: helloWorld;			 
			do action: executeUpdateDB{ 
 				arg vendorName value: "MySQL";
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg dbName value: "STUDENTS";
 				arg usrName value: "root";
 				arg password value: "";
 				arg updateComm value: "DELETE FROM Registration " +
                   					   "WHERE id = 101";
 			}
		}
	} 
}      