/**
 *  DB2agentMSSQL
 *  Author: bgaudou
 *  Description: 
 */

model DB2agentMSSQL

global {
	map<string,string> BOUNDS <- [	//"srid"::"32648", // optinal
									"host"::"localhost",
									"dbtype"::"sqlserver",
									"database"::"spatial_DB",
									"port"::"1433",
									"user"::"sa",
									"passwd"::"tmt",
								  	"select"::"SELECT GEOM.STAsBinary() as GEOM FROM bounds;" ];
	map<string,string> PARAMS <- [	//"srid"::"32648", // optinal
									"host"::"localhost",
									"dbtype"::"sqlserver",
									"database"::"spatial_DB",
									"port"::"1433",
									"user"::"sa",
									"passwd"::"tmt"];
	
	string QUERY <- "SELECT name, type, GEOM.STAsBinary() as GEOM FROM buildings ;";
	geometry shape <- envelope(BOUNDS);		  	
	init {
		create DB_accessor {
			create buildings from: list(self select [params:: PARAMS, select:: QUERY]) 
							 with:[ "name"::"name","type"::"type", "shape":: "geom"];
		 }
	}
}

entities {
	species DB_accessor skills: [SQLSKILL];
	
	species buildings {
		string type;
		aspect default {
			draw shape color: rgb("gray") ;
		}	
	}	
}

experiment DB2agentMSSQL type: gui {
	output {
		display fullView {
			species buildings aspect: default;
		}
	}
}
