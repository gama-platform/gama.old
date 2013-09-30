/**
 *  DB2agentSQLite
 *  Author: bgaudou
 *  Description: 
 */

model DB2agentMySQL 

global {
	map<string,string> BOUNDS <- [	'host'::'localhost',
									'dbtype'::'postgres',
									'database'::'spatial_db',
									'port'::'5433',
									'user'::'postgres',
									'passwd'::'tmt',
								  	'select'::'SELECT ST_AsBinary(geom) as geom FROM bounds;' ];
	map<string,string> PARAMS <- [	'host'::'localhost',
									'dbtype'::'postgres',
									'database'::'spatial_db',
									'port'::'5433',
									'user'::'postgres',
									'passwd'::'tmt'];
	
	string QUERY <- "SELECT name, type, ST_AsBinary(geom) as geom FROM buildings ;";
	geometry shape <- envelope(BOUNDS);		  	
		  	
	init {
		create DB_accessor {
			create buildings from: list(self select [params:: PARAMS, select:: QUERY]) 
							 with:[ 'name'::"name",'type'::"type", 'shape':: geometry("geom")];
		 }
	}
}

entities {
	species DB_accessor skills: [SQLSKILL];
	
	species buildings {
		string type;
		aspect default {
			draw shape color: rgb('gray') ;
		}	
	}	
}

experiment DB2agentSQLite type: gui {
	output {
		display fullView {
			species buildings aspect: default;
		}
	}
}
