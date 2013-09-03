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
		create species: toto number: 1 ;
		ask (toto at 0)	
		{ 
			do  executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "DROP TABLE location; " ;
 			} 	
 			do  executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "DELETE FROM geometry_columns where f_table_name='location'; " ;
 			} 	
 			
 			write "dropped table!";
			do  executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "CREATE TABLE location " +
                   "(id INTEGER PRIMARY KEY, " +
                   " name TEXT NOT NULL," +
                   " geom BLOB NOT NULL); "  ;
 			} 	
 			write "Insert Geometry Meta data";
			do executeUpdate{ 
				arg params value: PARAMS; 
				arg updateComm value: "INSERT INTO geometry_columns"
				      +"(f_table_name,f_geometry_column, geometry_type,coord_dimension,srid,geometry_format) "
				      +"values('location', 'geom',3,2, 4326,'WKB');" ;
          
 			}
 		write "Created location table";				
					
		}
	}
}  
entities {  
	species toto skills: [SQLSKILL] {  
	} 
} 

experiment default_expr type:gui {

}     