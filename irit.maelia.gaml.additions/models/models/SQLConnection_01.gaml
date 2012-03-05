/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model SQLConnection_01
  
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
 				arg dbName value: "";
 				arg usrName value: "root";
 				arg password value: "";
 				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
		}
	} 
}      