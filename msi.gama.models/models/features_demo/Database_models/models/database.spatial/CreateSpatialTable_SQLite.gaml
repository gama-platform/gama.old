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
	map PARAMS <- ['dbtype'::'sqlite','database'::'../includes/spatialite.db','extension'::'../includes/lib/libspatialite.3.dylib'];

	init {
		create dummy ;
		ask (dummy)	
		{ 
			do  executeUpdate params: PARAMS updateComm: "DROP TABLE location; " ;
 			do  executeUpdate params: PARAMS updateComm: "DELETE FROM geometry_columns where f_table_name='location'; " ;
 
 			write "dropped table!";
			do executeUpdate params: PARAMS updateComm: "CREATE TABLE location " +
                   "(id INTEGER PRIMARY KEY, " +
                   " name TEXT NOT NULL," +
                   " geom BLOB NOT NULL); "  ;
 			 	
 			write "Insert Geometry Meta data";
			do executeUpdate params: PARAMS updateComm: "INSERT INTO geometry_columns"
				      +"(f_table_name,f_geometry_column, geometry_type,coord_dimension,srid,geometry_format) "
				      +"values('location', 'geom',3,2, 4326,'WKB');" ;
          
 			
 			write "Created location table";				
					
		}
	}
}  
entities {  
	species dummy skills: [SQLSKILL] {  
	} 
} 

experiment default_expr type:gui {

}     