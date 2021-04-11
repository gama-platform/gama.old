/**
* Name:  CreateBuildingTableSQLite
* Author: Truong Minh Thai
* Description: This model shows how to create a database and a table in SQLite using GAMA
 * Tags: database
 */

model SQLite_selectNUpdate
  
global {
	map PARAMS <- ['dbtype'::'sqlite','database'::'../includes/spatialite.db'];

	init {
		write "This model will work only if the corresponding database is installed" color: #red;

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

species dummy skills: [SQLSKILL] { } 

experiment default_expr type:gui {

}     