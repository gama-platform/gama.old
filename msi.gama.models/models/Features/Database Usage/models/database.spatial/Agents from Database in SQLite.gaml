/**
 *  DB2agentSQLite
 *  Author: bgaudou
 *  Description: 
 */

model DB2agentSQLite

global {
//	file buildingsShp <- file('../../includes/building.shp');
//	file boundsShp <- file('../../includes/bounds.shp');
//	geometry shape <- envelope(boundsShp);

	
	map<string,string> BOUNDS <- ["dbtype"::"sqlite",
								  'database'::'../../includes/spatialite.db',
								 //'srid'::'32648',
								  "select"::"SELECT AsBinary(geom) as geom FROM bounds;"				
				  				 ];
	map<string,string> PARAMS <- ["dbtype"::"sqlite",
								  //'srid'::'32648',
								  'database'::'../../includes/spatialite.db'
								  ];
	
	string QUERY <- "SELECT name, type, AsBinary(geom) as geom FROM buildings ;";
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
