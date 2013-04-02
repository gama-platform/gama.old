/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *   00: Test DBMS Connection
 *   01: Test executeUpdate with CREATE/DROP DATABASE or table statement
 *   02: Test insert action
 */

model SQLite_selectNUpdate
  
global {
	var PARAMS type:map init: ['dbtype'::'sqlite','database'::'../includes/meteo.db'];

	init {
		create species: toto number: 1 ;
		ask (toto at 0)	
		{ 
			do  executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "CREATE TABLE REGISTRATION " +
                   "(id INTEGER PRIMARY KEY, " +
                   " first TEXT NOT NULL, " + 
                   " last TEXT NOT NULL, " + 
                   " age INTEGER);";
 			} 	
 			write "REGISTRATION table was created";
			do executeUpdate{ 
				arg params value: PARAMS; 
			arg updateComm value: "INSERT INTO REGISTRATION " +
                   "VALUES (100, 'Zara', 'Ali', 18);";
 			}
 			do action: insert{ 
				arg params value: PARAMS; 
 				arg into value: "Registration" ;
                arg values value:[101, 'Mahnaz', 'Fatma', 25];
 			}
 			do action: insert{ 
				arg params value: PARAMS; 
 				arg into value: "Registration";
                arg values value:[102, 'Zaid', 'Khan', 30];
 			}	
  			do executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "INSERT INTO REGISTRATION " +
                   "VALUES(103, 'Sumit', 'Mittal', 28);";
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
			do executeUpdate{ 
 				arg params value: PARAMS; 			
 				arg updateComm value: "UPDATE Registration " +
                   "SET age = 30 WHERE id in (100, 101)";       
            }
            write "Select after updated";
            let t value: self select[params :: PARAMS, select:: "select * from registration"];
 			do action: write with: [message::t];
 		}
        reflex drop {    
  			do  executeUpdate{
				arg params value: PARAMS; 
 				arg updateComm value: "delete from REGISTRATION where id=100 "; 
 			}
        	 			
 			do  executeUpdate{
				arg params value: PARAMS; 
 				arg updateComm value: "DROP TABLE REGISTRATION"; 
 			}
 			write "Registration table was droped";			
		}
	} 
}      