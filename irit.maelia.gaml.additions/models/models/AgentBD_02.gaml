/**
 *  AgentDB_00
 *  Author: Truong Minh Thai
 *  Description: 
 * 		Model testing the irit.maelia.gaml.additions package
 * 		Test of the species.AgentDB 
 * 	00: test connection
 *  01: Test executeUpdateDB
 *  02: Test executeUpdateDB with INSERT statement
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
 								
 			do action: executeUpdateDB with: [
 				updateComm:: "CREATE TABLE REGISTRATION " +
                   "(id INTEGER not NULL, " +
                   " first VARCHAR(255), " + 
                   " last VARCHAR(255), " + 
                   " age INTEGER, " + 
                   " PRIMARY KEY ( id ))"
 			];
 			do action: executeUpdateDB with: [
 				updateComm:: "INSERT INTO Registration " +
                   "VALUES (100, 'Zara', 'Ali', 18)"
 			];
 			do action: executeUpdateDB with: [
 				updateComm:: "INSERT INTO Registration " +
                   "VALUES (101, 'Mahnaz', 'Fatma', 25)"
 			];
 			do action: executeUpdateDB with: [
 				updateComm:: "INSERT INTO Registration " +
                   "VALUES (102, 'Zaid', 'Khan', 30)"
 			];
 			do action: executeUpdateDB with: [
 				updateComm:: "INSERT INTO Registration " +
                   "VALUES(103, 'Sumit', 'Mittal', 28)"
 			];
 			/* 
 			let t value: self.selectDB[
 						selectComm::"SELECT id, first, last, age FROM Registration WHERE id>101 and id<=103 ;"
 			];
			set listRes value: t;	
			do action: write with: [message::t];
			* 
			*/
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