/**
 *  SQLConnection
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model MSSQL_01
  
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
				arg vendorName value: "MSSQL";
				arg url value: "193.49.54.112";
				arg port value: "1433";
				arg dbName value: "";
				arg usrName value: "sa";
				arg password value: "tmt";
 				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
		}
	} 
}      