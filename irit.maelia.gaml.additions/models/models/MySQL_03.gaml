/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement
 *   03: Test executeUpdate with INSERT statement
 */

model MySQL_03

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
		reflex insert{
			do action: helloWorld;			 
			do action: executeUpdateDB{ 
 				arg dbtype value: "MySQL"; 
 				arg host value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "students";
 				arg user value: "root";
 				arg passwd value: "root";
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (100, 'Zara', 'Ali', 18)";
 			}
 			do action: executeUpdateDB{ 
 				arg dbtype value: "MySQL"; 
 				arg host value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "students";
 				arg user value: "root";
 				arg passwd value: "root";
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (101, 'Mahnaz', 'Fatma', 25)";
 			}
 			do action: executeUpdateDB{ 
 				arg dbtype value: "MySQL"; 
 				arg host value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "students";
 				arg user value: "root";
 				arg passwd value: "root";
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (102, 'Zaid', 'Khan', 30)";
 			}	
 			do action: executeUpdateDB{ 
 				arg dbtype value: "MySQL"; 
 				arg host value: "127.0.0.1";  
 				arg port value: "3306";
 				arg database value: "students";
 				arg user value: "root";
 				arg passwd value: "root";
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES(103, 'Sumit', 'Mittal', 28)";
 			}					
		}
	} 
}      