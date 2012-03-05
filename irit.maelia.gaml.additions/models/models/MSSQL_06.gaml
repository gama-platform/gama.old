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

model MSSQL_06

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
 				arg vendorName value: "MSSQL";
				arg url value: "193.49.54.112";
				arg port value: "1433";
				arg dbName value: "STUDENTS";
				arg usrName value: "sa";
				arg password value: "tmt";
 				arg selectComm value: "SELECT * FROM Registration";
 			}
 			// get result from Select From Where statement
 			let t value: self.selectDB[ 						
 						vendorName:: "MSSQL",
 						url:: "193.49.54.112", 
 						port:: "1433",
 						dbName:: "STUDENTS",
 						usrName:: "sa",
 						password:: "tmt",
 						selectComm::"SELECT id, first, last, age FROM Registration WHERE id>101 and id<=103"
 			];
			set listRes value: t;	
			do action: write with: [message::t];
		}
	} 
}      