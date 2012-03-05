/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement
 *   03: Test executeUpdate with INSERT statement
 */

model SQLConnection_03

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
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (100, 'Zara', 'Ali', 18)";
 			}
 			do action: executeUpdateDB{ 
 				arg vendorName value: "MySQL";
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg dbName value: "STUDENTS";
 				arg usrName value: "root";
 				arg password value: "";
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (101, 'Mahnaz', 'Fatma', 25)";
 			}
 			do action: executeUpdateDB{ 
 				arg vendorName value: "MySQL";
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg dbName value: "STUDENTS";
 				arg usrName value: "root";
 				arg password value: "";
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (102, 'Zaid', 'Khan', 30)";
 			}	
 			do action: executeUpdateDB{ 
 				arg vendorName value: "MySQL";
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg dbName value: "STUDENTS";
 				arg usrName value: "root";
 				arg password value: "";
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES(103, 'Sumit', 'Mittal', 28)";
 			}					
		}
	} 
}      