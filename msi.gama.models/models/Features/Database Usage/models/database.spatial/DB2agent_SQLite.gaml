/**
 *  DB2agentSQLite
 *  Author: bgaudou
 *  Description: 
 */

model DB2agentSQLite

global {
	file buildingsShp <- file('../../includes/building.shp');
	file boundsShp <- file('../../includes/bounds.shp');
	geometry shape <- envelope(boundsShp);
	//geometry shape <- envelope(BOUNDS);		  	
	
	map<string,string> BOUNDS <- ["dbtype"::"sqlite",
								  "database"::"../../includes/spatialite.db",
								 'srid'::'4326',
								  "select"::"SELECT AsBinary(geom) as geom FROM buildings;"				
				  				 ];
	map<string,string> PARAMS <- ["dbtype"::"sqlite",
								  "database"::"../../includes/spatialite.db",
								  'longitudeFirst'::false,
								  'srid'::'4326'
								  ];
	
	string QUERY <- "SELECT name, type, ST_AsBinary(geom) as geom FROM buildings ;";
	  	
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

//environment bounds: BOUNDS ;
experiment DB2agentSQLite type: gui {
	output {
		display fullView {
			species buildings aspect: default;
		}
	}
}
