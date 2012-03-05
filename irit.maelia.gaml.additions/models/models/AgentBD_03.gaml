/**
 *  AgentDB_00
 *  Author: Truong Minh Thai
 *  Description: 
 * 		Model testing the irit.maelia.gaml.additions package
 * 		Test of the species.AgentDB 
 * 	00: test connection
 *  01: Test executeUpdateDB
 *  02: Test executeUpdateDB with INSERT statement
 *  03: Test selectDB
 *      DROP TABLE
 *      DROP DATABASE
 */
 
model AgentDB_02 
  
global { 
	
	init {
 		create species: AgentDB number: 1 {

			do action: connectDB with: [
	 						vendorName:: "MySQL", 
	 						url:: "127.0.0.1", 
	 						port:: "3306",
	 						dbName:: "STUDENTS",
	 						usrName:: "root",
	 						password:: ""			
 						];	 	
 			let t value: self.selectDB [
 						selectComm::"SELECT id, first, last, age FROM Registration WHERE id>101 and id<=103 ;"
 			];
			let listRes value: t;	
			do action: write with: [message::t];
 			do action: executeUpdateDB with: [
 				 updateComm::"DROP TABLE REGISTRATION "
 			];
  			do action: executeUpdateDB with: [
 				 updateComm::"DROP DATABASE STUDENTS "
 			];			
 			do action: closeDB;
		}
		create species: toto number: 1;
		
	}
		
} 

entities {
	species toto skills: [ MAELIADBMS ] {
		var listRes type: list init: [ ];
		//var obj type: obj;
		reflex {
			do action: helloWorld;
			do action: connectDB {
				arg vendorName value: "MySQL"; 
 				arg url value: "127.0.0.1";  
 				arg port value: "3306";
 				arg dbName value: "";
 				arg usrName value: "root";
 				arg password value: "";
			}
		}
	}
}      