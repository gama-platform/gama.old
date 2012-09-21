/**
 *  SQLite_selectNUpdate
 *  Author: thaitruongminh
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE DATABASE statement
 */

model SQLite_selectNUpdate
  
global {
	var PARAMS type:map init: ['host'::'','dbtype'::'sqlite','port'::'','database'::'../includes/meteo.db','user'::'','passwd'::''];

	init {
		create species: toto number: 1 ;
		ask (toto at 0)	
		{ 
			do action: executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "CREATE TABLE REGISTRATION " +
                   "(id INTEGER PRIMARY KEY, " +
                   " first TEXT NOT NULL, " + 
                   " last TEXT NOT NULL, " + 
                   " age INTEGER);";
 			} 	
 			write "REGISTRATION table was created";
			do action: executeUpdate{ 
				arg params value: PARAMS; 
			arg updateComm value: "INSERT INTO REGISTRATION " +
                   "VALUES (100, 'Zara', 'Ali', 18);";
 			}
 			do action: executeUpdate{ 
				arg params value: PARAMS; 
 				arg updateComm value: "INSERT INTO REGISTRATION " +
                   "VALUES (101, 'Mahnaz', 'Fatma', 25);";
 			}
			do action: executeUpdate{ 
				arg params value: PARAMS; 
 				arg updateComm value: "INSERT INTO REGISTRATION " +
                   "VALUES (102, 'Zaid', 'Khan', 30);";
 			}	
 			do action: executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "INSERT INTO REGISTRATION " +
                   "VALUES(103, 'Sumit', 'Mittal', 28);";
 			}	
 			write "Three records were inserted";				

					
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
                   "SET age = 30 WHERE id in (100, 101)";       
            }
            write "Select after updated";
            let t value: self select[params :: PARAMS, select:: "select * from registration"];
 			do action: write with: [message::t];
 		}
        reflex drop {    
 			//set PARAMS value:['url'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'','user'::'root','passwd'::'root'] ;
 			
 			do action: executeUpdate{
				arg params value: PARAMS; 
 				arg updateComm value: "DROP TABLE REGISTRATION"; 
 			}
 			write "Registration table was droped";			
		}
	} 
}      