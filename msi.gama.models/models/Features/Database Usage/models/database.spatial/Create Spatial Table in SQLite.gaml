/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 * 		Create a spatial table in Sqlite
 * 	See:
 * 		SQLite_Agent2DB and SQLite_libspatialite models
 *      
 */

model SQLite_selectNUpdate
  
global {
	map PARAMS <- ['dbtype'::'sqlite','database'::'../../includes/spatialite.db'];

	init {
		create dummy ;
		ask (dummy)	
		{ 
			do  executeUpdate params: PARAMS 
					updateComm: "DROP TABLE bounds; " ;
 			do  executeUpdate params: PARAMS 
 					updateComm: "DROP TABLE buildings; " ;
  
 			write "dropped tables!";
			do executeUpdate params: PARAMS updateComm: "CREATE TABLE bounds " +
                   "(id INTEGER PRIMARY KEY, " +
				   " geom BLOB NOT NULL); "  ;
 			do executeUpdate params: PARAMS updateComm: "CREATE TABLE buildings " +
                   "(id INTEGER PRIMARY KEY, " +
                   " name TEXT NOT NULL," +
                   " type TEXT NOT NULL," +
                   " geom BLOB NOT NULL); "  ;
 			 	
		
					
		}
	}
}  
entities {  
	species dummy skills: [SQLSKILL] {  
	} 
} 

experiment default_expr type:gui {

}     