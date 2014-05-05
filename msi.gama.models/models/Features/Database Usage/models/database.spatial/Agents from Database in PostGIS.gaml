/**
 *  DB2agentSQLite
 *  Author: bgaudou
 *  Description: 
 */

model DB2agentMySQL 

global {
	map<string,string> BOUNDS <- [	//'srid'::'32648', // optinal
	 								'host'::'localhost',
									'dbtype'::'postgres',
									'database'::'spatial_DB',
									'port'::'5433',
									'user'::'postgres',
									'passwd'::'tmt',
								  	'select'::'SELECT ST_AsBinary(geom) as geom FROM buildings;' ];
	map<string,string> PARAMS <- [	//'srid'::'32648', // optinal
									'host'::'localhost',
									'dbtype'::'postgres',
									'database'::'spatial_DB',
									'port'::'5433',
									'user'::'postgres',
									'passwd'::'tmt'];
	
	string QUERY <- "SELECT name, type, ST_AsBinary(geom) as geom FROM buildings ;";
	geometry shape <- envelope(BOUNDS);		  	
		  	
	init {
		create DB_accessor {
			create buildings from: list(self select [params:: PARAMS, select:: QUERY]) 
							 with:[ 'name'::"name",'type'::"type", 'shape':: "geom"];
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
