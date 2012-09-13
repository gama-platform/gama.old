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
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex select{
			do action: helloWorld;			 
			// Select with no WHERE con dictions
			do action: selectDB{ 
				arg dbtype value: "MSSQL";
				arg url value: "localhost";// IP address or computer name
				arg port value: "1433"; 
				arg database value: "Students";
				arg user value: "sa";
				arg passwd value: "tmt";
 				arg select value: "SELECT * FROM Registration";
 			}
 
 			// get result from Select From Where statement
 			let t value: self selectDB[ 						
 						dbtype:: "MSSQL",
 						url :: "localhost", 
 						port :: "1433",
 						database :: "STUDENTS",
 						user:: "sa",
 						passwd:: "tmt",
 						select::"SELECT id, first, last, age FROM Registration WHERE id>101 and id<=103"
 			];
			set listRes value: t;	
			// listRes(0): List of column name
			// listRes(1): List of column name type 	
			// listRes(2): List of Record -> List of List type
			do action: write with: [message::t];
		}
	}
}
    