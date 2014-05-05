/**
 *  DB2agentSQLite
 *  Author: bgaudou
 *  Description: 
 */

model DB2agentMySQL

global {
	map<string,string> BOUNDS <- [	//'srid'::'32648', // optinal
									'host'::'localhost',
									'dbtype'::'MySQL',
									'database'::'spatial_DB',
									'port'::'8889',
									'user'::'root',
									'passwd'::'root',
								  	"select"::"SELECT geom FROM bounds;" ];
	map<string,string> PARAMS <- [	//'srid'::'32648', // optinal
									'host'::'localhost',
									'dbtype'::'MySQL',
									'database'::'spatial_DB',
									'port'::'8889',
									'user'::'root',
									'passwd'::'root'];
	
	string QUERY <- "SELECT name, type, geom FROM buildings ;";
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
