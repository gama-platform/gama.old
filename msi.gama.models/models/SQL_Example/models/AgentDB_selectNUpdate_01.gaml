/**
 *  AgentDB_selectNUpdate_01
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model AgentDB_selectNUpdate_01
  
global {
	//var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','port'::'1433','database'::'','user'::'sa','passwd'::'tmt'];
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'Postgres','database'::'','port'::'5432','user'::'postgres','passwd'::'tmt'];
	init {
		create species:toto number: 1 {
			do action: connect with: [params::PARAMS];	
		}

		ask (toto at 0)	
		{
			do action: executeUpdate{
 				arg updateComm value: "CREATE DATABASE STUDENTS"; 
 			}
 			write "STUDENTS database was created";
			//close current connection;
			do action:close;
			//open connection to Students database 
			remove key: "database" from: PARAMS;
			put "students" key:"database" in: PARAMS;
			do action: connect with: [params::PARAMS];
			//create table	
			do action: executeUpdate{ 
				arg updateComm value: "CREATE TABLE REGISTRATION " +
                   "(id INTEGER not NULL, " +
                   " first VARCHAR(255), " + 
                   " last VARCHAR(255), " + 
                   " age INTEGER, " + 
                   " PRIMARY KEY ( id ))";
 			} 	
 			write "REGISTRATION table was created";
			do action: executeUpdate{ 
			arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (100, 'Zara', 'Ali', 18)";
 			}
 			do action: executeUpdate{ 
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (101, 'Mahnaz', 'Fatma', 25)";
 			}
			do action: executeUpdate{  
 				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES (102, 'Zaid', 'Khan', 30)";
 			}	
 			do action: executeUpdate{  
				arg updateComm value: "INSERT INTO Registration " +
                   "VALUES(103, 'Sumit', 'Mittal', 28)";
 			}	
 			write "Three records were inserted";				
					
		}
	}
}  
entities {  
	species toto parent: AgentDB {
		var listRes type: list init:[];		

		reflex select {	 
			write "Select before updated";
			let t value: self select[select:: "select * from registration"];
 			do action: write with: [message::t];
 		}
 		reflex update {	 
			do action: executeUpdate{ 		
 				arg updateComm value: "UPDATE Registration " +
                   "SET age = 30 WHERE id in (100, 101)";       
            }
            write "Select after updated";
            set listRes value: self select[select:: "select * from registration"];
 			do action: write with: [message::listRes];
 		}
        reflex drop {    
 			//close the connection to Students database
			do action:close;
			//connect to server without database
			remove key: "database" from: PARAMS;
			put "" key:"database" in: PARAMS;			
			do action: connect with: [params::PARAMS];	
			
 			do action: executeUpdate{
 				arg updateComm value: "DROP DATABASE STUDENTS"; 
 			}
 			write "STUDENTS database was droped";			
		}
	} 
}      