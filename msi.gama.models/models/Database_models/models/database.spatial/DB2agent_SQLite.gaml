/**
 *  DB2agentSQLite
 *  Author: bgaudou
 *  Description: 
 */

model DB2agentSQLite

global {
	map<string,string> BOUNDS <- ["dbtype"::'sqlite',
								  "database"::'../../includes/spatialite.db',
								  "extension"::'../../includes/lib/libspatialite.3.dylib',
								  "select"::"SELECT AsBinary(geom) FROM bounds;"				
				  				 ];
	map<string,string> PARAMS <- ['dbtype'::'sqlite',
								  'database'::'../../includes/spatialite.db',
								  'extension'::'../../includes/lib/libspatialite.3.dylib'];
	
	string QUERY <- "SELECT AsBinary(geom) AS geom FROM buildings ;";
				  	
	init {
		create DB_accessor {
			create buildings from: list(self select [params:: PARAMS, select:: QUERY]) 
							 with:[ 'shape':: "geom"];
		 }
	}
}

environment bounds: BOUNDS;

entities {
	species DB_accessor skills: [SQLSKILL];
	
	species buildings {
		aspect default {
			draw shape color: rgb('gray') ;
		}	
	}	
}

experiment DB2agentSQLite type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display fullView {
			species buildings aspect: default;
		}
	}
}
