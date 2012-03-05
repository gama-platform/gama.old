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
 */

model SQLConnection_06

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
			// Select with no WHERE con dictions
			do action: selectDB{ 
 				arg vendorName value: "MySQL";
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg dbName value: "STUDENTS";
 				arg usrName value: "root";
 				arg password value: "";
 				arg selectComm value: "SELECT * FROM Registration";
 			}
 			// get result from Select From Where statement
 			let t value: self.selectDB[
 						vendorName:: "MySQL",
 						url:: "127.0.0.1", 
 						port:: "3306",
 						dbName:: "STUDENTS",
 						usrName:: "root",
 						password:: "",
 						selectComm::"SELECT id, first, last, age FROM Registration WHERE id>101 and id<=103"
 			];
			set listRes value: t;	
			do action: write with: [message::t];
		}
	} 
}      