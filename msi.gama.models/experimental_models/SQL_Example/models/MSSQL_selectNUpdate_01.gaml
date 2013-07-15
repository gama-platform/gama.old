/**
 *  SQLConnection
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model MSSQL_selectNUpdate  

global {
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','port'::'1433','database'::'','user'::'sa','passwd'::'tmt'];

	init {
		create species: toto number: 1 ;
		ask (toto at 0)	
		{
			do action: executeUpdate{ 
				arg params value: PARAMS; 
 				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
 			write "STUDENTS database was created";
			remove key: "database" from: PARAMS;
			put "STUDENTS" key:"database" in: PARAMS;
			do action: executeUpdate{ 
				 arg params value: PARAMS; 
				 arg updateComm value: "CREATE TABLE REGISTRATION " +
                   "(id INTEGER not NULL, " +
                   " first VARCHAR(255), " + 
                   " last VARCHAR(255), " + 
                   " age INTEGER, " +  
                   " PRIMARY KEY ( id ))"; 
 			} 	
 			write "REGISTRATION table was created";
			do action: insert{ 
				arg params value: PARAMS; 
				arg into value: "Registration";
                arg columns value:["id","first","last","age"]; 
                arg values value: [100, 'Zara', 'Ali',18]  ;
 			}
 			do action: executeUpdate{ 
				arg params value: PARAMS; 
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (101, 'Mahnaz', 'Fatma', 25)";
 			}
 			// insert with all columns of table
			do action: insert{ 
				arg params value: PARAMS;  
 				arg into value: "Registration ";
                arg values value:[102, 'Zaid', 'Khan', 30];
 			}	
 			do action: executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES(?, ?, 'Mittal', ?)";
                arg values value:[103, 'Sumit',  28];
 			}	
 			write "Four records were inserted";				

					
		}
	}
}   
entities {  
	species toto skills: [SQLSKILL] {  
		var listRes type: list init:[];
		//var obj type: obj;
		reflex select {	 
			write "Select before updated";
			let t value: self select[params :: PARAMS, select:: "select * from registration"];
 			do action: write with: [message::t];
 		}
 		reflex update {	 
			do action: executeUpdate{ 
 				arg params value: PARAMS; 			
 				arg updateComm value: "UPDATE Registration " +
                   "SET age = ? WHERE id in (?, ?)";  
                arg values value:[30,100,101];
            }
            write "Select after updated";
            let t value: self select[params :: PARAMS, select:: "select * from registration"];
 			do action: write with: [message::t];
 		}
        reflex drop {    
 			remove key: "database" from: PARAMS;
			put "" key:"database" in: PARAMS;
 			do action: executeUpdate{
				arg params value: PARAMS; 
 				arg updateComm value: "DROP DATABASE STUDENTS"; 
 			}
 			write "STUDENTS database was droped";			
		}
	} 
}      