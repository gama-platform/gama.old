/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 *   02: Test executeUpdate with CREATE TABLE statement
 *   03: Test executeUpdate with INSERT statement
 *   04: Test executeUpdate with UPDATE statement
 *   05: Test executeUpdate with DELETE statement
 *   06: Test executeQuery with SELECT statement
 */

model MySQL_06

/* Insert your model definition here */

  
global {
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'Students','user'::'root','passwd'::'root'];

	init {
		create species: toto number: 1 ;
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex select {
			do action: helloWorld;			  
			// Select with WHERE con dictions
 			let t value: self select [
 						params:: PARAMS,
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