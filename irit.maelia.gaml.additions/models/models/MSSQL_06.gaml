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
var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'Students','port'::'1433','user'::'sa','passwd'::'tmt'];
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
 			// get result from Select From Where statement
 			let t value: self select[
				        params:: PARAMS,
 						select::"SELECT ID,FIRST,LAST,AGE FROM Registration where id>100"
 			];
			set listRes value: list(t);	
			// listRes(0): List of column name
			// listRes(1): List of column name type 	
			// listRes(2): List of Record -> List of List type
			do action: write with: [message::t];
		}
	}
}
    